$ErrorActionPreference = "Stop"
[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12

$jdkVersion = "17.0.10_7"
$jdkUrl = "https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.10%2B7/OpenJDK17U-jdk_x64_windows_hotspot_17.0.10_7.zip"
$toolsDir = Join-Path $PSScriptRoot "..\tools"
$zipPath = Join-Path $toolsDir "jdk.zip"
$extractPath = $toolsDir

Write-Host "Verificando ambiente para Java..." -ForegroundColor Cyan

if (-not (Test-Path $toolsDir)) {
    New-Item -ItemType Directory -Path $toolsDir -Force | Out-Null
}

$jdkHome = Join-Path $toolsDir "jdk-17.0.10+7"
if (Test-Path $jdkHome) {
    Write-Host "Java ja esta configurado em: $jdkHome" -ForegroundColor Green
    exit 0
}

Write-Host "Baixando OpenJDK $jdkVersion..." -ForegroundColor Yellow
try {
    Invoke-WebRequest -Uri $jdkUrl -OutFile $zipPath -UseBasicParsing
}
catch {
    Write-Error "Falha ao baixar Java. Erro: $($_.Exception.Message)"
    exit 1
}

Write-Host "Extraindo arquivos..." -ForegroundColor Yellow
try {
    Expand-Archive -Path $zipPath -DestinationPath $extractPath -Force
}
catch {
    Write-Error "Falha ao extrair Java: $($_.Exception.Message)"
    exit 1
}

if (Test-Path $zipPath) {
    Remove-Item $zipPath -Force
}

Write-Host "Java configurado com sucesso!" -ForegroundColor Green
