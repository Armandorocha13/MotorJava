@echo off
echo ==========================================
echo       MOTOR JAVA - STARTUP ENGINE
echo ==========================================

:: 1. Iniciar Ponte Excel Plus (Python)
start "AXIS - EXCEL BRIDGE" /D python cmd /c "venv\Scripts\python.exe excel_server.py"

:: 2. Iniciar Backend Java
start "AXIS - JAVA ENGINE" /D backend cmd /c "mvn exec:java"

:: 3. Iniciar Frontend Vite
start "AXIS - FRONTEND" /D frontend cmd /c "npm run dev"

echo Axis está subindo em 3 instâncias separadas.
echo Verifique as janelas de comando abertas.
pause
