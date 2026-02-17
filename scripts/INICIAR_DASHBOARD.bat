@echo off

rem Muda para o diretório raiz do projeto (um nível acima da pasta scripts)
pushd "%~dp0.."

rem Configuração de caminhos e JDK
echo Compilando GuiApp (Interface Grafica)...

if not exist bin mkdir bin

"C:\Program Files\Java\jdk-25.0.2\bin\javac.exe" -d bin -cp "lib\*" -sourcepath src\main\java src\main\java\com\motorjava\*.java src\main\java\com\motorjava\config\*.java

if %errorlevel% neq 0 (
    echo Erro na compilacao!
    pause
    popd
    exit /b %errorlevel%
)

echo Executando Interface...
start "" "C:\Program Files\Java\jdk-25.0.2\bin\javaw.exe" -cp "bin;lib\*;src\main\resources" com.motorjava.GuiApp

popd
exit
