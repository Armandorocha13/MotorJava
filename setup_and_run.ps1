$ErrorActionPreference = "Stop"
[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12


# 1. Configurar JAVA_HOME e PATH
Write-Host "Configurando ambiente Java..."
$jdkPath = "C:\Program Files\Java\jdk-25.0.2"
if (Test-Path $jdkPath) {
    $env:JAVA_HOME = $jdkPath
    $env:PATH = "$jdkPath\bin;$env:PATH"
    Write-Host "JAVA_HOME definido para: $jdkPath"
}
else {
    Write-Host "AVISO: JDK não encontrado em $jdkPath. Tentando encontrar outra versão..."
    $javaRoot = "C:\Program Files\Java"
    if (Test-Path $javaRoot) {
        $possibleJdk = Get-ChildItem $javaRoot | Where-Object { $_.Name -like "jdk*" } | Select-Object -First 1
        if ($possibleJdk) {
            $env:JAVA_HOME = $possibleJdk.FullName
            $env:PATH = "$($possibleJdk.FullName)\bin;$env:PATH"
            Write-Host "JAVA_HOME definido para: $($possibleJdk.FullName)"
        }
    }
}

# Verifica java
try {
    java -version
    javac -version
}
catch {
    Write-Error "Java ão encontrado ou não configurado corretamente. Instale o JDK."
}

# 2. Verificar/Baixar Maven
$mavenVersion = "3.9.6"
$mavenUrl = "https://archive.apache.org/dist/maven/maven-3/$mavenVersion/binaries/apache-maven-$mavenVersion-bin.zip"
$localMavenDir = "$PSScriptRoot\.maven_portable"
$mavenBin = "$localMavenDir\apache-maven-$mavenVersion\bin\mvn.cmd"

if (-not (Test-Path $mavenBin)) {
    Write-Host "Maven não encontrado localmente. Baixando..."
    if (-not (Test-Path $localMavenDir)) { New-Item -ItemType Directory -Path $localMavenDir | Out-Null }
    
    $zipPath = "$localMavenDir\maven.zip"
    Invoke-WebRequest -Uri $mavenUrl -OutFile $zipPath
    
    Write-Host "Extraindo Maven..."
    Expand-Archive -Path $zipPath -DestinationPath $localMavenDir -Force
    Remove-Item $zipPath
}

Write-Host "Usando Maven em: $mavenBin"

# 3. Compilar e Executar
Write-Host "Compilando projeto..."
& $mavenBin clean package -DskipTests

if ($LASTEXITCODE -eq 0) {
    Write-Host "Executando ImportadorArquivo..."
    # O comando exec:java usa o classpath configurado pelo plugin exec-maven-plugin, mas se não tiver configurado no pom, usamos -cp
    # Vamos usar exec:java se o pom permitir, ou java -cp
    
    # Tentativa com exec:java (precisa configurar mainClass no pom ou passar argumento)
    & $mavenBin exec:java -Dexec.mainClass="com.motorjava.ImportadorArquivo"
}
else {
    Write-Error "Falha na compilação."
}
