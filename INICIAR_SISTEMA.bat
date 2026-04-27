@echo off
:: Iniciar o AXIS usando caminhos diretos para evitar erros de leitura do CMD

:: Modo Turbo: Tenta iniciar direto se ja estiver compilado
if exist "%~dp0motor-nucleo\target\classes" (
    if exist "%~dp0motor-nucleo\target\dependency" (
        cd /d "%~dp0motor-nucleo"
        start "" /b "%~dp0ferramentas\jdk-17.0.10+7\bin\javaw.exe" -cp "target\classes;target\dependency\*" com.motorjava.NucleoMotor
        exit
    )
)

:: Caso precise preparar (primeira vez)
echo [AXIS] Preparando ambiente...
cd /d "%~dp0"
call "%~dp0ferramentas\apache-maven-3.9.9\bin\mvn.cmd" install -DskipTests -am -pl motor-nucleo
cd /d "%~dp0motor-nucleo"
call "%~dp0ferramentas\apache-maven-3.9.9\bin\mvn.cmd" dependency:copy-dependencies -DincludeScope=runtime
start "" /b "%~dp0ferramentas\jdk-17.0.10+7\bin\javaw.exe" -cp "target\classes;target\dependency\*" com.motorjava.NucleoMotor
exit
