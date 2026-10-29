# =====================================================================
# Urban Eats - verifica que el equipo tenga todo lo necesario
#   Java 21+ (JDK), Maven Wrapper, XAMPP (MariaDB), Python 3.10+ con su
#   entorno virtual, archivos .env y puertos libres. Ollama es opcional.
# Lo que se puede arreglar solo, se arregla: copia los .env desde los
# .env.example, crea el entorno virtual de Python e instala sus paquetes,
# y si JAVA_HOME falta o apunta a un Java viejo lo corrige para esta sesion.
# Uso (desde la carpeta Urban-Eats):
#   powershell -ExecutionPolicy Bypass -File scripts\verificar_entorno.ps1
# Termina con codigo 0 si todo esta bien y 1 si falta algo obligatorio.
# =====================================================================
param(
    [string]$XamppDir = $(if ($env:XAMPP_DIR) { $env:XAMPP_DIR } else { "C:\xampp" }),
    # Solo revisa, sin copiar .env ni instalar paquetes de Python.
    [switch]$SinArreglar
)

$raiz = Split-Path -Parent $PSScriptRoot
$carpetaPython = Join-Path $raiz "servicio-python"
$venvPython = Join-Path $carpetaPython ".venv\Scripts\python.exe"
$script:fallos = 0
$script:avisos = 0

function Ok([string]$texto)    { Write-Host "   [OK]    $texto" -ForegroundColor Green }
function Aviso([string]$texto) { Write-Host "   [AVISO] $texto" -ForegroundColor Yellow; $script:avisos++ }
function Falla([string]$texto) { Write-Host "   [FALTA] $texto" -ForegroundColor Red; $script:fallos++ }

function Leer-Env([string]$ruta) {
    $valores = @{}
    if (Test-Path -LiteralPath $ruta) {
        foreach ($linea in Get-Content -LiteralPath $ruta) {
            if ($linea -match '^\s*([A-Za-z_][A-Za-z0-9_]*)\s*=(.*)$') { $valores[$Matches[1]] = $Matches[2].Trim() }
        }
    }
    return $valores
}

function Version-Java([string]$exe) {
    $texto = (& $exe -version 2>&1 | Out-String)
    if ($texto -match 'version "(\d+)(?:\.(\d+))?') {
        $mayor = [int]$Matches[1]
        if ($mayor -eq 1) { $mayor = [int]$Matches[2] }   # Java 8 y anteriores: "1.8.0"
        return $mayor
    }
    return 0
}

function Dueno-Puerto([int]$puerto) {
    $conexion = Get-NetTCPConnection -LocalPort $puerto -State Listen -ErrorAction SilentlyContinue | Select-Object -First 1
    if (-not $conexion) { return $null }
    $proceso = Get-Process -Id $conexion.OwningProcess -ErrorAction SilentlyContinue
    if ($proceso) { return $proceso.ProcessName } else { return "desconocido" }
}

Write-Host "Verificando el entorno de Urban Eats en $raiz" -ForegroundColor Cyan

# ---------------------------------------------------------------------
Write-Host "1) Java (JDK 21 o superior)" -ForegroundColor Yellow
# Se prueba JAVA_HOME, luego las carpetas de instalacion comunes y por
# ultimo el java del PATH. Hace falta un JDK (con javac), no solo un JRE.
$candidatos = @()
if ($env:JAVA_HOME) { $candidatos += Join-Path $env:JAVA_HOME "bin\java.exe" }
foreach ($base in "C:\Program Files\Eclipse Adoptium", "C:\Program Files\Java", "C:\Program Files\Microsoft",
                  "C:\Program Files\Zulu", "C:\Program Files\Amazon Corretto", "C:\Program Files\BellSoft") {
    if (Test-Path -LiteralPath $base) {
        Get-ChildItem -LiteralPath $base -Directory -ErrorAction SilentlyContinue |
            Sort-Object Name -Descending | ForEach-Object { $candidatos += Join-Path $_.FullName "bin\java.exe" }
    }
}
$javaPath = Get-Command java -ErrorAction SilentlyContinue
if ($javaPath) { $candidatos += $javaPath.Source }

$jdk = $null
$versionVieja = 0
foreach ($exe in ($candidatos | Select-Object -Unique)) {
    if (-not (Test-Path -LiteralPath $exe)) { continue }
    if (-not (Test-Path -LiteralPath (Join-Path (Split-Path $exe) "javac.exe"))) { continue }
    $version = Version-Java $exe
    if ($version -ge 21) { $jdk = @{ Exe = $exe; Version = $version; Home = Split-Path (Split-Path $exe) }; break }
    if ($version -gt $versionVieja) { $versionVieja = $version }
}
if ($jdk) {
    Ok "JDK $($jdk.Version) en $($jdk.Home)"
    if ($env:JAVA_HOME -ne $jdk.Home) {
        # Solo para esta sesion (y las ventanas que abra): no toca la configuracion de Windows.
        $env:JAVA_HOME = $jdk.Home
        $env:PATH = (Join-Path $jdk.Home "bin") + ";" + $env:PATH
        Aviso "JAVA_HOME no apuntaba a este JDK; se ajusto para esta sesion. Para dejarlo fijo: Configuracion > Variables de entorno > JAVA_HOME = $($jdk.Home)"
    }
} elseif ($versionVieja -gt 0) {
    Falla "Se encontro Java $versionVieja, pero el proyecto necesita JDK 21 o superior. Descargalo de https://adoptium.net (Temurin 21 o 25, instalador .msi)"
} else {
    Falla "No hay un JDK instalado. Descarga Temurin 21 o 25 de https://adoptium.net (instalador .msi, marca 'Set JAVA_HOME')"
}

# ---------------------------------------------------------------------
Write-Host "2) Maven Wrapper" -ForegroundColor Yellow
if ((Test-Path -LiteralPath (Join-Path $raiz "mvnw.cmd")) -and
    (Test-Path -LiteralPath (Join-Path $raiz ".mvn\wrapper\maven-wrapper.properties"))) {
    Ok "mvnw.cmd presente (no hace falta instalar Maven)"
    if (-not (Test-Path -LiteralPath (Join-Path $HOME ".m2\wrapper\dists"))) {
        Aviso "Es la primera ejecucion: Maven y las dependencias se descargan de internet (unos minutos)"
    }
} else {
    Falla "Falta mvnw.cmd o la carpeta .mvn. Vuelve a clonar el repositorio completo"
}

# ---------------------------------------------------------------------
Write-Host "3) XAMPP / MariaDB ($XamppDir)" -ForegroundColor Yellow
$mysqld = Join-Path $XamppDir "mysql\bin\mysqld.exe"
$mysql = Join-Path $XamppDir "mysql\bin\mysql.exe"
if ((Test-Path -LiteralPath $mysqld) -and (Test-Path -LiteralPath $mysql)) {
    Ok "MariaDB de XAMPP encontrado"
    $dueno = Dueno-Puerto 3306
    if ($dueno -and $dueno -notmatch 'mysqld|mariadbd') {
        Aviso "El puerto 3306 lo usa '$dueno'. Si no es tu base de datos, cierralo antes de iniciar"
    }
} else {
    Falla "No se encontro XAMPP en $XamppDir. Instalalo desde https://www.apachefriends.org o indica la carpeta: -XamppDir D:\xampp"
}

# ---------------------------------------------------------------------
Write-Host "4) Archivos .env" -ForegroundColor Yellow
foreach ($par in @(@{ Env = Join-Path $raiz ".env"; Ejemplo = Join-Path $raiz ".env.example" },
                   @{ Env = Join-Path $carpetaPython ".env"; Ejemplo = Join-Path $carpetaPython ".env.example" })) {
    $relativo = $par.Env.Substring($raiz.Length + 1)
    if (Test-Path -LiteralPath $par.Env) {
        Ok "$relativo existe"
    } elseif ($SinArreglar) {
        Falla "Falta $relativo. Copialo desde $relativo.example"
    } else {
        Copy-Item -LiteralPath $par.Ejemplo -Destination $par.Env
        Ok "$relativo creado a partir de .env.example (valores para XAMPP local)"
    }
}
$envSpring = Leer-Env (Join-Path $raiz ".env")
$envPython = Leer-Env (Join-Path $carpetaPython ".env")
if ("$($envSpring['PYTHON_API_KEY'])" -ne "$($envPython['API_KEY'])") {
    Aviso "PYTHON_API_KEY (.env) y API_KEY (servicio-python\.env) son distintas: Spring Boot no podra hablar con el servicio Python"
}
if ("$($envSpring['PAGOS_MODO_DEMO'])" -eq "true") {
    Ok "Pagos en modo demostracion (PAGOS_MODO_DEMO=true): no se usa MercadoPago"
} elseif ("$($envSpring['MERCADOPAGO_PUBLIC_KEY'])" -match '^(TEST|APP_USR)-' -and "$($envSpring['MERCADOPAGO_ACCESS_TOKEN'])" -match '^(TEST|APP_USR)-') {
    Ok "Credenciales de MercadoPago configuradas (pagos de prueba con el formulario de MercadoPago)"
} else {
    Aviso "Faltan MERCADOPAGO_PUBLIC_KEY y MERCADOPAGO_ACCESS_TOKEN en el .env (credenciales de prueba TEST-), o pon PAGOS_MODO_DEMO=true"
}

# ---------------------------------------------------------------------
Write-Host "5) Python 3.10+ y servicio-python" -ForegroundColor Yellow
function Version-Python([string]$exe) {
    $texto = & $exe -c "import sys; print('%d.%d' % sys.version_info[:2])" 2>$null
    if ($LASTEXITCODE -eq 0 -and $texto) { return [version]("$texto".Trim()) }
    return $null
}
if (-not (Test-Path -LiteralPath $venvPython)) {
    # Python base: primero el lanzador 'py', luego 'python' (sin el acceso directo de Microsoft Store).
    $pythonBase = $null
    if (Get-Command py -ErrorAction SilentlyContinue) {
        $ruta = & py -3 -c "import sys; print(sys.executable)" 2>$null
        if ($LASTEXITCODE -eq 0 -and $ruta) { $pythonBase = "$ruta".Trim() }
    }
    if (-not $pythonBase) {
        $pythonBase = Get-Command python, python3 -All -ErrorAction SilentlyContinue |
            Where-Object { $_.Source -notlike "*WindowsApps*" } | Select-Object -First 1 -ExpandProperty Source
    }
    $versionBase = if ($pythonBase) { Version-Python $pythonBase } else { $null }
    if (-not $versionBase) {
        Falla "No hay Python instalado. Descargalo de https://www.python.org (3.13 recomendado, marca 'Add python.exe to PATH')"
    } elseif ($versionBase -lt [version]"3.10") {
        Falla "Python $versionBase es muy viejo; se necesita 3.10 o superior"
    } elseif ($SinArreglar) {
        Falla "Falta el entorno virtual servicio-python\.venv"
    } else {
        Write-Host "   Creando el entorno virtual con Python $versionBase..."
        & $pythonBase -m venv (Join-Path $carpetaPython ".venv")
        if ($LASTEXITCODE -ne 0) { Falla "No se pudo crear servicio-python\.venv" }
    }
}
if (Test-Path -LiteralPath $venvPython) {
    $versionVenv = Version-Python $venvPython
    if (-not $versionVenv) {
        Falla "El entorno virtual servicio-python\.venv esta danado (se creo con un Python que ya no existe). Borra la carpeta .venv y vuelve a ejecutar"
    } else {
        Ok "Entorno virtual con Python $versionVenv"
        $modulos = "import fastapi, uvicorn, sqlalchemy, pymysql, pydantic, pydantic_settings, httpx, multipart, openpyxl, fpdf"
        & $venvPython -c $modulos 2>$null
        if ($LASTEXITCODE -eq 0) {
            Ok "Paquetes de requirements.txt instalados"
        } elseif ($SinArreglar) {
            Falla "Faltan paquetes de Python. Ejecuta: servicio-python\.venv\Scripts\python.exe -m pip install -r servicio-python\requirements.txt"
        } else {
            Write-Host "   Instalando paquetes de requirements.txt (necesita internet)..."
            & $venvPython -m pip install --disable-pip-version-check -r (Join-Path $carpetaPython "requirements.txt")
            & $venvPython -c $modulos 2>$null
            if ($LASTEXITCODE -eq 0) { Ok "Paquetes instalados" } else { Falla "No se pudieron instalar los paquetes de Python. Revisa los mensajes de pip arriba" }
        }
    }
}

# ---------------------------------------------------------------------
Write-Host "6) Puertos 8000 y 8080" -ForegroundColor Yellow
foreach ($par in @(@{ Puerto = 8000; Esperado = 'python'; Servicio = "servicio Python" },
                   @{ Puerto = 8080; Esperado = 'java';   Servicio = "Spring Boot" })) {
    $dueno = Dueno-Puerto $par.Puerto
    if (-not $dueno) {
        Ok "Puerto $($par.Puerto) libre"
    } elseif ($dueno -match $par.Esperado) {
        Ok "Puerto $($par.Puerto) ocupado por $dueno (probablemente el $($par.Servicio) ya esta encendido)"
    } else {
        Falla "El puerto $($par.Puerto) lo usa '$dueno'. Cierralo: el $($par.Servicio) lo necesita"
    }
}

# ---------------------------------------------------------------------
Write-Host "7) IA local (Ollama, opcional)" -ForegroundColor Yellow
if (Get-Command ollama -ErrorAction SilentlyContinue) {
    $modelo = if ($envPython['OLLAMA_MODEL']) { $envPython['OLLAMA_MODEL'] } else { "qwen3:4b-instruct" }
    $lista = & ollama list 2>$null
    if ($LASTEXITCODE -ne 0) {
        Ok "Ollama instalado (no esta encendido; el script de inicio lo enciende)"
    } elseif ("$lista" -match [regex]::Escape($modelo)) {
        Ok "Ollama con el modelo $modelo"
    } else {
        Aviso "Ollama no tiene el modelo $modelo. Descargalo con: ollama pull $modelo"
    }
} else {
    Aviso "Ollama no esta instalado: el chat funcionara en modo basico (https://ollama.com para la IA local)"
}

# ---------------------------------------------------------------------
Write-Host ""
if ($script:fallos -eq 0) {
    Write-Host "Entorno listo ($script:avisos aviso(s))." -ForegroundColor Green
    exit 0
}
Write-Host "Faltan $script:fallos requisito(s) obligatorio(s). Corrigelos y vuelve a ejecutar." -ForegroundColor Red
exit 1
