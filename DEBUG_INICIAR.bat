@echo off
set "JAVA_EXE=%~dp0ferramentas\jdk-17.0.10+7\bin\java.exe"

echo ==========================================
echo       MODO DEBUG - INICIANDO AXIS
echo ==========================================
echo Java: "%JAVA_EXE%"
echo Pasta: "%~dp0motor-nucleo"
echo.

cd /d "%~dp0motor-nucleo"

:: Tentar rodar e manter a janela aberta para ver o erro
"%JAVA_EXE%" -cp "target\classes;target\dependency\*" com.motorjava.NucleoMotor

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERRO] O Java retornou um erro: %ERRORLEVEL%
    pause
)
pause
