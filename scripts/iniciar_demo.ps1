# =====================================================================
# Urban Eats - arranca todo para la demostracion (Windows PowerShell)
#   0. Verifica requisitos (scripts\verificar_entorno.ps1)
#   1. MariaDB de XAMPP y migraciones pendientes (scripts\migrar_bd.ps1)
#   2. Ollama (si esta instalado)   3. Servicio Python   4. Spring Boot
# Uso (desde la carpeta Urban-Eats), o doble clic en scripts\iniciar_demo.cmd:
#   powershell -ExecutionPolicy Bypass -File scripts\iniciar_demo.ps1
#   ... -XamppDir D:\xampp     si XAMPP no esta en C:\xampp
#   ... -SinDatosDemo          no carga los usuarios y cocinas de prueba
# Cada servicio se salta si ya esta escuchando en su puerto. Si falta un
# requisito o falla una migracion, se detiene antes de encender lo demas.
# =====================================================================
param(
    [string]$XamppDir = $(if ($env:XAMPP_DIR) { $env:XAMPP_DIR } else { "C:\xampp" }),
    [switch]$SinDatosDemo
)

$raiz = Split-Path -Parent $PSScriptRoot
$python = Join-Path $raiz "servicio-python\.venv\Scripts\python.exe"
$fallos = 0

function Esta-escuchando([int]$puerto) {
    [bool](Get-NetTCPConnection -LocalPort $puerto -State Listen -ErrorAction SilentlyContinue)
}

function Esperar-puerto([int]$puerto, [int]$segundos) {
    for ($i = 0; $i -lt $segundos; $i++) {
        if (Esta-escuchando $puerto) { return $true }
        Start-Sleep -Seconds 1
    }
    return $false
}

Write-Host "0) Verificacion de requisitos" -ForegroundColor Yellow
# Se llama con '&' (no en otra ventana) para que el JAVA_HOME que corrija llegue a Maven.
& (Join-Path $PSScriptRoot "verificar_entorno.ps1") -XamppDir $XamppDir
if ($LASTEXITCODE -ne 0) {
    Write-Host "No se inicio nada: faltan requisitos (ver arriba)." -ForegroundColor Red
    exit 1
}
Write-Host ""

Write-Host "1) Base de datos (XAMPP MariaDB)" -ForegroundColor Yellow
if (Esta-escuchando 3306) {
    Write-Host "   Ya estaba encendida (puerto 3306)"
} else {
    $mysqld = Join-Path $XamppDir "mysql\bin\mysqld.exe"
    $myIni = Join-Path $XamppDir "mysql\bin\my.ini"
    Start-Process -FilePath $mysqld -ArgumentList "--defaults-file=`"$myIni`" --standalone" -WindowStyle Minimized
    if (Esperar-puerto 3306 60) {
        Write-Host "   MariaDB encendida (puerto 3306)"
    } else {
        Write-Host "   MariaDB no respondio en 60 s. Revisa $XamppDir\mysql\data\mysql_error.log" -ForegroundColor Red
        exit 1
    }
}
Write-Host "   Migraciones de la base:"
& (Join-Path $PSScriptRoot "migrar_bd.ps1") -XamppDir $XamppDir -SinDatosDemo:$SinDatosDemo
if ($LASTEXITCODE -ne 0) {
    Write-Host "No se encendio la aplicacion: la base no esta al dia (ver arriba)." -ForegroundColor Red
    exit 1
}

Write-Host "2) IA local (Ollama)" -ForegroundColor Yellow
$ollama = Get-Command ollama -ErrorAction SilentlyContinue
if ($ollama) {
    if (-not (Get-Process ollama -ErrorAction SilentlyContinue)) { Start-Process ollama -ArgumentList "serve" -WindowStyle Minimized }
    Write-Host "   Ollama encendido"
} else {
    Write-Host "   Ollama no esta instalado: el chat funcionara en modo basico"
}

Write-Host "3) Servicio Python (http://127.0.0.1:8000/docs)" -ForegroundColor Yellow
if (Esta-escuchando 8000) {
    Write-Host "   Ya estaba encendido (puerto 8000)"
} elseif (-not (Test-Path -LiteralPath $python)) {
    Write-Host "   Falta el entorno virtual de servicio-python. Crealo con: cd servicio-python; python -m venv .venv; .\.venv\Scripts\python.exe -m pip install -r requirements.txt" -ForegroundColor Red
    $fallos++
} else {
    Start-Process -FilePath $python -ArgumentList "main.py" -WorkingDirectory (Join-Path $raiz "servicio-python")
    if (Esperar-puerto 8000 90) {
        Write-Host "   Servicio Python encendido (puerto 8000)"
    } else {
        Write-Host "   El servicio Python no respondio en 90 s. Revisa su ventana" -ForegroundColor Red
        $fallos++
    }
}

Write-Host "4) Spring Boot (http://localhost:8080)" -ForegroundColor Yellow
if (Esta-escuchando 8080) {
    Write-Host "   Ya estaba encendido (puerto 8080)"
} else {
    # Se lanza desde PowerShell y no con 'cmd /c mvnw.cmd': en este equipo cmd no encuentra mvnw.cmd.
    $orden = "Set-Location -LiteralPath '$raiz'; .\mvnw.cmd -DskipTests spring-boot:run"
    $codificado = [Convert]::ToBase64String([Text.Encoding]::Unicode.GetBytes($orden))
    Start-Process -FilePath "powershell.exe" -ArgumentList "-NoExit", "-EncodedCommand", $codificado
    if (Esperar-puerto 8080 300) {
        Write-Host "   Spring Boot encendido (puerto 8080)"
    } else {
        Write-Host "   Spring Boot no respondio en 5 min. Revisa la ventana de PowerShell que se abrio" -ForegroundColor Red
        $fallos++
    }
}

Write-Host ""
if ($fallos -eq 0) {
    Write-Host "Listo. Abre http://localhost:8080 (aplicacion) y http://127.0.0.1:8000/docs (servicio Python)" -ForegroundColor Green
} else {
    Write-Host "Hubo $fallos problema(s). Revisa los mensajes de arriba." -ForegroundColor Red
}
Write-Host "Para reiniciar los datos de prueba: servicio-python\.venv\Scripts\python.exe -m scripts.demo.preparar_demo"
