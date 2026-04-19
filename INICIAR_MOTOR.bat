@echo off
chcp 65001 > nul
set "ROOT=%~dp0"
set "JAVA_HOME=%ROOT%tools\jdk-17.0.10+7"
set "MAVEN_BIN=%ROOT%tools\apache-maven-3.9.9\bin\mvn.cmd"
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo ==========================================
echo       MOTOR JAVA NATIVO - INICIALIZANDO
echo ==========================================

:: Verificar integridade dos componentes locais
if not exist "%JAVA_HOME%\bin\java.exe" (
    echo [ERRO] Java Portatil nao encontrado!
    pause
    exit /b
)

echo [MOTOR] Abrindo Dashboard Nativo...
cd /d "%ROOT%backend"
call "%MAVEN_BIN%" compile exec:java

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ALERTA] O motor parou com erro (Codigo: %ERRORLEVEL%).
    pause
)
