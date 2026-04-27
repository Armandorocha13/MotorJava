@echo off
:: Recompilar o AXIS usando caminhos diretos

set JAVA_HOME=%~dp0ferramentas\jdk-17.0.10+7
cd /d "%~dp0"

echo [AXIS] Recompilando projeto...
call "%~dp0ferramentas\apache-maven-3.9.9\bin\mvn.cmd" clean install -DskipTests

echo [AXIS] Preparando bibliotecas...
call "%~dp0ferramentas\apache-maven-3.9.9\bin\mvn.cmd" dependency:copy-dependencies -pl motor-nucleo -DincludeScope=runtime

echo.
echo [SUCESSO] Projeto pronto. Use o INICIAR_SISTEMA para abrir rapido.
pause
