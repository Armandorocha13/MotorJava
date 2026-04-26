@echo off
chcp 65001 > nul
set "ROOT=%~dp0"
set "MAVEN_BIN=%ROOT%ferramentas\apache-maven-3.9.9\bin\mvn.cmd"
set "JAVA_HOME=%ROOT%ferramentas\jdk-17.0.10+7"
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo ==========================================
2: echo       MOTOR JAVA - MODO RECOMPILACAO
echo ==========================================
echo [MOTOR] Limpando e instalando todos os modulos...

cd /d "%ROOT%"
call "%MAVEN_BIN%" clean install -DskipTests

echo.
echo [SUCESSO] Projeto recompilado. Agora voce pode usar o INICIAR_SISTEMA.bat normalmente.
pause
