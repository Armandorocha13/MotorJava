@echo off
echo ==========================================
echo       MOTOR JAVA - STARTUP ENGINE
echo ==========================================

:: 1. Iniciar Backend Java
start "AXIS - JAVA ENGINE" /D backend cmd /c "mvn exec:java"

:: 2. Iniciar Frontend Vite
start "AXIS - FRONTEND" /D frontend cmd /c "npm run dev"

echo Axis está subindo em instâncias separadas.
echo Verifique as janelas de comando abertas.
pause
