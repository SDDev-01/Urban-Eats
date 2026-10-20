# =====================================================================
# Urban Eats - arranca todo para la demostracion (Windows PowerShell)
#   1. MariaDB de XAMPP   2. Ollama (si esta instalado)
#   3. Servicio Python    4. Spring Boot
# Uso (desde la carpeta Urban-Eats):  powershell -ExecutionPolicy Bypass -File scripts\iniciar_demo.ps1
# =====================================================================
$raiz = Split-Path -Parent $PSScriptRoot

Write-Host "1) Base de datos (XAMPP MariaDB)" -ForegroundColor Yellow
if (-not (Get-Process mysqld -ErrorAction SilentlyContinue)) {
    Start-Process -FilePath "C:\xampp\mysql\bin\mysqld.exe" -ArgumentList '--defaults-file="C:\xampp\mysql\bin\my.ini" --standalone' -WindowStyle Minimized
    Start-Sleep -Seconds 4
}
Write-Host "   MariaDB encendida"

Write-Host "2) IA local (Ollama)" -ForegroundColor Yellow
$ollama = Get-Command ollama -ErrorAction SilentlyContinue
if ($ollama) {
    if (-not (Get-Process ollama -ErrorAction SilentlyContinue)) { Start-Process ollama -ArgumentList "serve" -WindowStyle Minimized }
    Write-Host "   Ollama encendido"
} else {
    Write-Host "   Ollama no esta instalado: el chat funcionara en modo basico"
}

Write-Host "3) Servicio Python (http://127.0.0.1:8000/docs)" -ForegroundColor Yellow
Start-Process -FilePath "$raiz\servicio-python\.venv\Scripts\python.exe" -ArgumentList "main.py" -WorkingDirectory "$raiz\servicio-python"

Write-Host "4) Spring Boot (http://localhost:8080)" -ForegroundColor Yellow
Start-Process -FilePath "cmd.exe" -ArgumentList "/c", "mvnw.cmd -DskipTests spring-boot:run" -WorkingDirectory $raiz

Write-Host ""
Write-Host "Listo. Cuando Spring Boot termine de arrancar abra http://localhost:8080" -ForegroundColor Green
Write-Host "Para reiniciar los datos de prueba: servicio-python\.venv\Scripts\python.exe -m scripts.demo.preparar_demo"
