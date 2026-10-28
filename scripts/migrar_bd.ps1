# =====================================================================
# Urban Eats - deja la base 'urbaneats' al dia (MariaDB debe estar encendida)
#   * Si la base no existe o esta vacia: Schema.sql + Inserts.sql +
#     fase6/01_migracion.sql + fase6/03_datos_bogota_nocturno.sql
#   * Si ya existe: aplica solo lo que falte. Cada archivo se reconoce por
#     las columnas o filas que crea, asi que se puede ejecutar las veces
#     que se quiera: nunca repite una migracion ya aplicada (ni las que se
#     corrieron a mano antes de que existiera este script).
#   * Antes de modificar una base que ya tenia datos, guarda un respaldo
#     en database\respaldos\.
# Uso (desde la carpeta Urban-Eats):
#   powershell -ExecutionPolicy Bypass -File scripts\migrar_bd.ps1
#   ... -ConDatosDemo   aplica tambien 03_datos_bogota_nocturno.sql en una
#                       base que ya estaba en uso (mueve las cocinas de
#                       prueba a Bogota y crea los usuarios de prueba)
#   ... -SoloRevisar    solo dice que falta, sin cambiar nada
# Usuario y contrasena: DB_USERNAME / DB_PASSWORD del .env (root sin clave si faltan).
# =====================================================================
param(
    [string]$XamppDir = $(if ($env:XAMPP_DIR) { $env:XAMPP_DIR } else { "C:\xampp" }),
    [switch]$ConDatosDemo,
    [switch]$SinDatosDemo,
    [switch]$SoloRevisar
)

$raiz = Split-Path -Parent $PSScriptRoot
$carpetaBd = Join-Path $raiz "database"
$mysql = Join-Path $XamppDir "mysql\bin\mysql.exe"
$mysqldump = Join-Path $XamppDir "mysql\bin\mysqldump.exe"
$base = "urbaneats"   # los .sql traen 'USE urbaneats', no se puede cambiar el nombre aqui

if (-not (Test-Path -LiteralPath $mysql)) {
    Write-Host "   No se encontro $mysql" -ForegroundColor Red
    exit 1
}

# Conexion tomada del .env de Spring Boot.
$envSpring = @{}
$rutaEnv = Join-Path $raiz ".env"
if (Test-Path -LiteralPath $rutaEnv) {
    foreach ($linea in Get-Content -LiteralPath $rutaEnv) {
        if ($linea -match '^\s*([A-Za-z_][A-Za-z0-9_]*)\s*=(.*)$') { $envSpring[$Matches[1]] = $Matches[2].Trim() }
    }
}
$servidor = "localhost"; $puerto = "3306"
if ($envSpring['DB_URL'] -match '^jdbc:(?:mysql|mariadb)://([^:/?]+)(?::(\d+))?/([^?]+)') {
    $servidor = $Matches[1]
    if ($Matches[2]) { $puerto = $Matches[2] }
    if ($Matches[3] -ne $base) { Write-Host "   Aviso: DB_URL usa la base '$($Matches[3])', pero los scripts SQL crean '$base'" -ForegroundColor Yellow }
}
$usuario = if ($envSpring['DB_USERNAME']) { $envSpring['DB_USERNAME'] } else { "root" }
# La clave va por variable de entorno para que no aparezca en la linea de comandos.
$env:MYSQL_PWD = $envSpring['DB_PASSWORD']
$conexion = @("--host=$servidor", "--port=$puerto", "--user=$usuario", "--default-character-set=utf8mb4")

function Consultar([string]$sql) {
    $salida = & $mysql @conexion --batch --skip-column-names -e $sql 2>&1
    if ($LASTEXITCODE -ne 0) { throw "MariaDB respondio: $($salida | Out-String)" }
    return "$salida".Trim()
}

function Ejecutar-Archivo([string]$relativo, [string]$baseDatos) {
    $archivo = Join-Path $carpetaBd $relativo
    if ($SoloRevisar) {
        Write-Host "   [PENDIENTE] database\$relativo" -ForegroundColor Yellow
        return
    }
    Write-Host "   Ejecutando database\$relativo..."
    # Se usa redireccion de entrada (como 'mysql < archivo') para respetar los
    # DELIMITER de los triggers y los acentos tal cual estan en el archivo.
    $errores = [IO.Path]::GetTempFileName()
    $salida = [IO.Path]::GetTempFileName()
    $argumentos = $conexion + $(if ($baseDatos) { @($baseDatos) } else { @() })
    $proceso = Start-Process -FilePath $mysql -ArgumentList $argumentos -NoNewWindow -PassThru `
        -RedirectStandardInput $archivo -RedirectStandardOutput $salida -RedirectStandardError $errores
    $null = $proceso.Handle   # sin esto PowerShell 5.1 a veces pierde el ExitCode
    $proceso.WaitForExit()
    $mensaje = (Get-Content -LiteralPath $errores -Raw)
    Remove-Item -LiteralPath $errores, $salida -ErrorAction SilentlyContinue
    if ($proceso.ExitCode -ne 0) { throw "Fallo database\$relativo`n$mensaje" }
    Write-Host "   [OK] database\$relativo aplicada" -ForegroundColor Green
}

function Contar-Columnas([string[]]$columnas) {
    # Cuantas de las columnas 'tabla.columna' ya existen en la base.
    $condiciones = ($columnas | ForEach-Object {
        $t, $c = $_.Split('.')
        "(TABLE_NAME='$t' AND COLUMN_NAME='$c')"
    }) -join " OR "
    return [int](Consultar "SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA='$base' AND ($condiciones)")
}

# Migraciones en orden. 'Marcas' son columnas que el archivo crea: si estan
# todas, ya se aplico; si no hay ninguna, falta; si hay algunas, quedo a medias.
$columnasFase6 = @("pedido.FechaHora", "envio.CodigoDireccion", "direccion.Latitud",
                   "restaurante.Activo", "plato.Calorias", "opinion.Calificacion")

try {
    $version = Consultar "SELECT VERSION()"
    Write-Host "   Conectado a $servidor`:$puerto como $usuario (servidor $version)"
} catch {
    Write-Host "   No se pudo conectar a MariaDB en $servidor`:$puerto con el usuario '$usuario'." -ForegroundColor Red
    Write-Host "   Revisa que este encendida y que DB_USERNAME / DB_PASSWORD del .env sean correctos." -ForegroundColor Red
    Write-Host "   $($_.Exception.Message)" -ForegroundColor DarkGray
    exit 1
}

try {
    $tablas = [int](Consultar "SELECT COUNT(*) FROM information_schema.TABLES WHERE TABLE_SCHEMA='$base'")
    $instalacionNueva = ($tablas -eq 0)
    $respaldoHecho = $false

    if ($instalacionNueva) {
        Write-Host "   La base '$base' no existe o esta vacia: instalacion nueva" -ForegroundColor Cyan
        Ejecutar-Archivo "Schema.sql" $null
        Ejecutar-Archivo "Inserts.sql" $base
    } else {
        Write-Host "   La base '$base' ya existe ($tablas tablas)"
    }

    # Respaldo previo a cambiar una base con datos (solo una vez por ejecucion).
    function Respaldar {
        if ($instalacionNueva -or $SoloRevisar -or $script:respaldoHecho) { return }
        $carpeta = Join-Path $carpetaBd "respaldos"
        New-Item -ItemType Directory -Force -Path $carpeta | Out-Null
        $destino = Join-Path $carpeta ("urbaneats_" + (Get-Date -Format "yyyyMMdd_HHmmss") + ".sql")
        & $mysqldump @conexion --routines --triggers --result-file=$destino $base
        if ($LASTEXITCODE -ne 0) { throw "No se pudo crear el respaldo; no se modifico la base" }
        Write-Host "   Respaldo guardado en $destino"
        $script:respaldoHecho = $true
    }

    # fase6/01_migracion.sql
    $presentes = Contar-Columnas $columnasFase6
    if ($presentes -eq $columnasFase6.Count) {
        Write-Host "   [OK] fase6/01_migracion.sql ya estaba aplicada" -ForegroundColor Green
    } elseif ($presentes -eq 0) {
        Respaldar
        Ejecutar-Archivo "fase6\01_migracion.sql" $base
    } else {
        throw ("fase6/01_migracion.sql quedo aplicada a medias ($presentes de $($columnasFase6.Count) columnas). " +
               "No se puede repetir sin riesgo: restaura un respaldo o termina a mano las secciones que faltan.")
    }

    # fase6/03_datos_bogota_nocturno.sql (datos de prueba)
    $usuariosDemo = [int](Consultar "SELECT COUNT(*) FROM $base.usuario WHERE Correo = 'cliente.demo@urbaneats.test'")
    if ($usuariosDemo -gt 0) {
        Write-Host "   [OK] Datos de prueba (03_datos_bogota_nocturno.sql) ya cargados" -ForegroundColor Green
    } elseif ($SinDatosDemo) {
        Write-Host "   Datos de prueba omitidos (-SinDatosDemo)"
    } elseif ($instalacionNueva -or $ConDatosDemo) {
        Respaldar
        Ejecutar-Archivo "fase6\03_datos_bogota_nocturno.sql" $base
        if (-not $SoloRevisar) { Write-Host "   Usuarios de prueba: database\fase6\USUARIOS_DE_PRUEBA.md" }
    } else {
        Write-Host "   Datos de prueba sin cargar: es una base en uso. Para cargarlos: scripts\migrar_bd.ps1 -ConDatosDemo" -ForegroundColor Yellow
    }

    # fase6/02_tablas_servicio_python.sql no se ejecuta: el servicio Python crea sus tablas al arrancar.
} catch {
    Write-Host "   $($_.Exception.Message)" -ForegroundColor Red
    exit 1
} finally {
    Remove-Item Env:MYSQL_PWD -ErrorAction SilentlyContinue
}
exit 0
