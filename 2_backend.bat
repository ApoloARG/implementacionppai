@echo off
title 2 - Backend CU36 (8081)
call "%~dp0usar_jdk.bat"
cd /d "%~dp0backend"
call mvnw.cmd spring-boot:run
