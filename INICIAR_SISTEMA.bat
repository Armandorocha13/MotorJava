@echo off
chcp 65001 > nul
set "ROOT=%~dp0"
set "JAVA_HOME=%ROOT%ferramentas\jdk-17.0.10+7"
set "MAVEN_BIN=%ROOT%ferramentas\apache-maven-3.9.9\bin\mvn.cmd"
set "PATH=%JAVA_HOME%\bin;%ROOT%ferramentas\lib;%PATH%"

echo ==========================================
echo       MOTOR JAVA NATIVO - INICIALIZANDO
echo ==========================================

:: Verificar integridade dos componentes locais
if not exist "%JAVA_HOME%\bin\java.exe" (
    echo [ERRO] Java Portatil nao encontrado!
    pause
    exit /b
)

echo [MOTOR] Verificando compilacao...
cd /d "%ROOT%"

:: Se o 'target' do nucleo ja existe, pula o install para abrir instantaneamente
if exist "motor-nucleo\target\classes" (
    echo [MOTOR] Iniciando modo rapido...
    call "%MAVEN_BIN%" exec:java -pl motor-nucleo -o
) else (
    echo [MOTOR] Primeira inicializacao detectada. Compilando...
    call "%MAVEN_BIN%" install -DskipTests -am -pl motor-nucleo
    call "%MAVEN_BIN%" exec:java -pl motor-nucleo
)

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ALERTA] O motor parou com erro (Codigo: %ERRORLEVEL%).
    pause
)
