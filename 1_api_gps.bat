@echo off
title 1 - API GPS catedra (8080)
call "%~dp0usar_jdk.bat"
cd /d "%~dp0dsi-api-bolsines"
call mvnw.cmd spring-boot:run
