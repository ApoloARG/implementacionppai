@echo off
title PPAI CU 36 - Iniciar demo
cd /d "%~dp0"
echo ============================================================
echo   PPAI DSI 2026 - CU 36 Consultar seguimiento de bolsines
echo ============================================================
echo.
call "%~dp0usar_jdk.bat"
where javac >nul 2>nul
if errorlevel 1 goto sin_java
where node >nul 2>nul
if errorlevel 1 goto sin_node
java -version
echo.
echo Abriendo 3 ventanas: API del GPS (8080), backend (8081) y front (3000)...
start "1 - API GPS catedra (8080)" cmd /k call "%~dp01_api_gps.bat"
timeout /t 3 /nobreak >nul
start "2 - Backend CU36 (8081)" cmd /k call "%~dp02_backend.bat"
start "3 - Front (3000)" cmd /k call "%~dp03_front.bat"
echo.
echo Esperando a que levanten el backend y el front (la primera vez tarda unos minutos)...
:esperar_backend
curl -s -o nul http://localhost:8081/api/demo/usuarios
if errorlevel 1 (
  timeout /t 5 /nobreak >nul
  goto esperar_backend
)
:esperar_front
curl -s -o nul http://localhost:3000
if errorlevel 1 (
  timeout /t 5 /nobreak >nul
  goto esperar_front
)
echo Listo. Abriendo http://localhost:3000
start "" http://localhost:3000
timeout /t 5 >nul
exit /b 0

:sin_java
echo [ERROR] No se encontro un JDK 17. Instala el JDK 17 o descomprimi un JDK 17 portable en la carpeta jdk\jdk-17.0.13+11
pause
exit /b 1

:sin_node
echo [ERROR] No se encontro Node.js. Instala Node 18 o superior y volve a intentar.
pause
exit /b 1
