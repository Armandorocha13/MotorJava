@echo off
set "MAVEN_PATH=C:\Users\user\Desktop\MotorJava\tools\apache-maven-3.9.9\bin\mvn.cmd"
echo Compilando e Iniciando o Projeto...
call "%MAVEN_PATH%" compile
if %ERRORLEVEL% NEQ 0 (
    echo Erro na compilacao!
    pause
    exit /b %ERRORLEVEL%
)
call "%MAVEN_PATH%" exec:java -e
pause
