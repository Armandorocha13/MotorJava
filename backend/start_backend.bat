@echo off
set "MAVEN_PATH=C:\Users\user\Desktop\Motor Java\MotorJava\tools\apache-maven-3.9.6\bin\mvn.cmd"
echo [MOTOR] Compilando e Iniciando Engine...
call "%MAVEN_PATH%" compile
call "%MAVEN_PATH%" exec:java -Dexec.mainClass="com.motorjava.MainApp"
pause
