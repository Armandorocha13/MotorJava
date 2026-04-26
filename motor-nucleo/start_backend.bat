@echo off
set "MAVEN_PATH=%~dp0..\tools\apache-maven-3.9.9\bin\mvn.cmd"

if not exist "%MAVEN_PATH%" (
    echo [ERRO] Maven nao encontrado em: %MAVEN_PATH%
    echo Por favor, execute o script scripts\setup_maven.ps1 para instalar o Maven localmente.
    pause
    exit /b 1
)

echo [MOTOR] Compilando e Iniciando Engine...
call "%MAVEN_PATH%" compile
call "%MAVEN_PATH%" exec:java -Dexec.mainClass="com.motorjava.MainApp"
pause

