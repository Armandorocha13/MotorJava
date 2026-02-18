$ErrorActionPreference = "Stop"

# Forcar TLS 1.2 (Necessario para downloads seguros em sistemas antigos)
[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12

$mavenVersion = "3.9.9"
# URL alternativa mais direta do repositorio central se o CDN falhar
$mavenUrl = "https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/$mavenVersion/apache-maven-$mavenVersion-bin.zip"
$toolsDir = Join-Path $PSScriptRoot "..\tools"
$zipPath = Join-Path $toolsDir "maven.zip"
$extractPath = $toolsDir

Write-Host "Verificando ambiente..." -ForegroundColor Cyan

if (-not (Test-Path $toolsDir)) {
    New-Item -ItemType Directory -Path $toolsDir -Force | Out-Null
}

$mavenHome = Join-Path $toolsDir "apache-maven-$mavenVersion"
if (Test-Path $mavenHome) {
    Write-Host "Maven ja esta configurado em: $mavenHome" -ForegroundColor Green
    exit 0
}

Write-Host "Baixando Apache Maven $mavenVersion..." -ForegroundColor Yellow
Write-Host "URL: $mavenUrl" -ForegroundColor Gray

try {
    Invoke-WebRequest -Uri $mavenUrl -OutFile $zipPath -UseBasicParsing
}
catch {
    Write-Error "ERRO DETALHADO: $($_.Exception.Message)"
    if ($_.Exception.InnerException) {
        Write-Error "CAUSA RAIZ: $($_.Exception.InnerException.Message)"
    }
    Write-Error "Falha ao baixar Maven. Verifique se o firewall bloqueia: $mavenUrl"
    exit 1
}

if (-not (Test-Path $zipPath)) {
    Write-Error "O arquivo nao foi baixado corretamente."
    exit 1
}

Write-Host "Extraindo arquivos..." -ForegroundColor Yellow
try {
    Expand-Archive -Path $zipPath -DestinationPath $extractPath -Force
}
catch {
    Write-Error "Falha ao extrair o arquivo: $($_.Exception.Message)"
    exit 1
}

if (Test-Path $zipPath) {
    Remove-Item $zipPath -Force
}

Write-Host "Maven configurado com sucesso!" -ForegroundColor Green
