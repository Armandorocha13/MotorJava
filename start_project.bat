@echo off
echo ============================================================
echo      INICIANDO SISTEMA MOTOR JAVA + REACT DASHBOARD
echo ============================================================

start cmd /k "cd backend && start_backend.bat"
start cmd /k "cd frontend && npm run dev"

echo.
echo Tudo certo! 
echo Backend rodando na porta 8080
echo Frontend rodando em: http://localhost:5173
echo ============================================================
