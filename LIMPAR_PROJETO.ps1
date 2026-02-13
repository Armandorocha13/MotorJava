# ========================================
# SCRIPT DE LIMPEZA DO PROJETO
# Remove arquivos desnecessários
# ========================================

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  LIMPEZA DO PROJETO MOTORJAVA" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$raiz = "C:\Users\user\Desktop\MotorJava"

# Arquivos a remover da raiz
$arquivosRemover = @(
    "20260210_StockTecnicos.csv",
    "Força VIVO SP.xlsx",
    "FORCA_TRABALHO_README.md",
    "setup_and_run.ps1"
)

# Pastas a remover da raiz
$pastasRemover = @(
    ".maven_portable",
    ".vscode",
    "bin",
    "target",
    ".agent"
)

# Arquivos a remover da pasta powerbi
$arquivosPowerBIRemover = @(
    "ATUALIZAR_VIEW.sql",
    "ATUALIZAR_VIEW_PAINEL.sql",
    "verificar_view.sql",
    "vivo_aging.dsn"
)

Write-Host "Arquivos que serão removidos:" -ForegroundColor Yellow
Write-Host ""

# Listar arquivos da raiz
Write-Host "RAIZ DO PROJETO:" -ForegroundColor White
foreach ($arquivo in $arquivosRemover) {
    $caminho = Join-Path $raiz $arquivo
    if (Test-Path $caminho) {
        $tamanho = (Get-Item $caminho).Length / 1MB
        Write-Host "  - $arquivo ($([math]::Round($tamanho, 2)) MB)" -ForegroundColor Gray
    }
}

# Listar pastas da raiz
foreach ($pasta in $pastasRemover) {
    $caminho = Join-Path $raiz $pasta
    if (Test-Path $caminho) {
        Write-Host "  - $pasta/ (pasta)" -ForegroundColor Gray
    }
}

# Listar arquivos do powerbi
Write-Host ""
Write-Host "PASTA POWERBI:" -ForegroundColor White
foreach ($arquivo in $arquivosPowerBIRemover) {
    $caminho = Join-Path "$raiz\powerbi" $arquivo
    if (Test-Path $caminho) {
        Write-Host "  - powerbi\$arquivo" -ForegroundColor Gray
    }
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
$confirmacao = Read-Host "Deseja continuar com a limpeza? (S/N)"

if ($confirmacao -ne "S" -and $confirmacao -ne "s") {
    Write-Host "Limpeza cancelada." -ForegroundColor Yellow
    exit
}

Write-Host ""
Write-Host "Removendo arquivos..." -ForegroundColor Yellow

$totalRemovido = 0

# Remover arquivos da raiz
foreach ($arquivo in $arquivosRemover) {
    $caminho = Join-Path $raiz $arquivo
    if (Test-Path $caminho) {
        $tamanho = (Get-Item $caminho).Length
        Remove-Item $caminho -Force
        $totalRemovido += $tamanho
        Write-Host "✓ Removido: $arquivo" -ForegroundColor Green
    }
}

# Remover pastas da raiz
foreach ($pasta in $pastasRemover) {
    $caminho = Join-Path $raiz $pasta
    if (Test-Path $caminho) {
        Remove-Item $caminho -Recurse -Force
        Write-Host "✓ Removido: $pasta/" -ForegroundColor Green
    }
}

# Remover arquivos do powerbi
foreach ($arquivo in $arquivosPowerBIRemover) {
    $caminho = Join-Path "$raiz\powerbi" $arquivo
    if (Test-Path $caminho) {
        Remove-Item $caminho -Force
        Write-Host "✓ Removido: powerbi\$arquivo" -ForegroundColor Green
    }
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "  LIMPEZA CONCLUÍDA!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
Write-Host "Espaço liberado: $([math]::Round($totalRemovido / 1MB, 2)) MB" -ForegroundColor Cyan
Write-Host ""

# Listar arquivos restantes
Write-Host "Arquivos mantidos na raiz:" -ForegroundColor White
Get-ChildItem $raiz -File | Select-Object Name, @{Name = "Tamanho"; Expression = { "{0:N2} KB" -f ($_.Length / 1KB) } } | Format-Table -AutoSize

Write-Host ""
Write-Host "Arquivos mantidos em powerbi/:" -ForegroundColor White
Get-ChildItem "$raiz\powerbi" -File | Select-Object Name, @{Name = "Tamanho"; Expression = { "{0:N2} KB" -f ($_.Length / 1KB) } } | Format-Table -AutoSize

Write-Host ""
Read-Host "Pressione Enter para sair"
