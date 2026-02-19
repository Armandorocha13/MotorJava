@echo off
setlocal
title Iniciar MotorJava GUI

:: --- CONFIGURACAO ---
set "MAVEN_VERSION=3.9.9"
set "TOOLS_DIR=%~dp0..\tools"
set "LOCAL_MAVEN_HOME=%TOOLS_DIR%\apache-maven-%MAVEN_VERSION%"
set "LOCAL_MVN=%LOCAL_MAVEN_HOME%\bin\mvn.cmd"
set "SETUP_SCRIPT=%~dp0setup_maven.ps1"

:: --- 1. VERIFICAR MAVEN LOCAL ---
if exist "%LOCAL_MVN%" (
    echo [INFO] Usando Maven Local...
    set "MAVEN_CMD=%LOCAL_MVN%"
    goto :START_PROJECT
)

:: --- 2. VERIFICAR MAVEN GLOBAL ---
where mvn >nul 2>nul
if %errorlevel% equ 0 (
    echo [INFO] Usando Maven Global...
    set "MAVEN_CMD=mvn"
    goto :START_PROJECT
)

:: --- 3. MAVEN NAO ENCONTRADO ---
cls
color 0e
echo ========================================================
echo [ATENCAO] O Maven nao foi encontrado no seu sistema.
echo ========================================================
echo.
echo O MotorJava precisa do Apache Maven para funcionar.
echo Podemos baixar e configurar uma versao local automaticamente.
echo (Isso sera feito apenas uma vez)
echo.
set /p INSTALAR="Deseja instalar o Maven agora? (S/N): "

if /i "%INSTALAR%" neq "S" (
    echo.
    echo [ERRO] O projeto nao pode ser iniciado sem o Maven.
    pause
    exit /b 1
)

echo.
echo [1/2] Iniciando script de configuracao...
if not exist "%TOOLS_DIR%" mkdir "%TOOLS_DIR%"

:: Executa o script PowerShell para baixar o Maven
powershell -NoProfile -ExecutionPolicy Bypass -File "%SETUP_SCRIPT%"

if %errorlevel% neq 0 (
    echo.
    echo [ERRO] Falha ao baixar o Maven. Verifique sua internet.
    pause
    exit /b 1
)

:: Verifica se a instalacao funcionou
if exist "%LOCAL_MVN%" (
    echo [SUCESSO] Maven instalado com sucesso!
    set "MAVEN_CMD=%LOCAL_MVN%"
    goto :START_PROJECT
) else (
    echo.
    echo [ERRO] O Maven nao foi encontrado apos a instalacao.
    pause
    exit /b 1
)

:: --- 4. INICIAR PROJETO ---
:START_PROJECT
color 07
cls
echo ==========================================
echo      INICIANDO O PROJETO MOTORJAVA
echo ==========================================
echo.
echo Compilando e executando a aplicacao...
echo Usando: %MAVEN_CMD%
echo.


cd /d "%~dp0.."
call "%MAVEN_CMD%" clean compile exec:java -Dexec.mainClass="com.motorjava.GuiApp"

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERRO] Ocorreu um erro ao executar o projeto.
    echo Verifique as mensagens acima.
    color 0c
    pause
) else (
    echo.
    echo [SUCESSO] Aplicacao finalizada.
    color 0a
    timeout /t 3 >nul
)
endlocal
