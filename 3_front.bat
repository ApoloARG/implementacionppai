@echo off
title 3 - Front (3000)
cd /d "%~dp0front"
if exist node_modules goto correr
echo Instalando dependencias del front, solo la primera vez...
call npm install
:correr
call npm run dev
