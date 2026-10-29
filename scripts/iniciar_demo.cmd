@echo off
rem Doble clic: verifica requisitos, migra la base y enciende Urban Eats.
rem La ventana queda abierta para poder leer los mensajes.
powershell.exe -NoExit -ExecutionPolicy Bypass -File "%~dp0iniciar_demo.ps1" %*
