@echo off
rem Si existe el JDK 17 portable en la carpeta jdk, lo usa. Si no, usa el JDK instalado en el sistema.
if exist "%~dp0jdk\jdk-17.0.13+11\bin\javac.exe" (
  set "JAVA_HOME=%~dp0jdk\jdk-17.0.13+11"
)
if defined JAVA_HOME set "PATH=%JAVA_HOME%\bin;%PATH%"
