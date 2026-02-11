@echo off

rem Configuração de caminhos e JDK
echo Compilando GuiApp (Interface Grafica)...

"C:\Program Files\Java\jdk-25.0.2\bin\javac.exe" -d bin -cp "lib\*" -sourcepath src\main\java src\main\java\com\motorjava\*.java

if %errorlevel% neq 0 (
    echo Erro na compilacao!
    pause
    exit /b %errorlevel%
)

echo Executando Interface...
start "" "C:\Program Files\Java\jdk-25.0.2\bin\javaw.exe" -cp "bin;lib\*;src\main\resources" com.motorjava.GuiApp

exit
