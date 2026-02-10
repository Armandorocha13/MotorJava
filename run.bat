@echo off
echo Compilando...
"C:\Program Files\Java\jdk-25.0.2\bin\javac.exe" -d bin -cp "lib\*" -sourcepath src\main\java src\main\java\com\motorjava\ImportadorArquivo.java

if %errorlevel% neq 0 (
    echo Erro na compilacao!
    pause
    exit /b %errorlevel%
)

echo Executando Importador...
"C:\Program Files\Java\jdk-25.0.2\bin\java.exe" -cp "bin;lib\*;src\main\resources" com.motorjava.ImportadorArquivo

echo.
echo Execucao finalizada.
pause
