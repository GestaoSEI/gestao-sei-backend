param(
    [string]$OutputDir,
    [string]$ContainerName = "gestao-sei-backend-db",
    [string]$DatabaseName = "gestaosei",
    [string]$DatabaseUser = "postgres",
    [string]$Timestamp = $(Get-Date -Format "yyyy-MM-dd_HHmmss")
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$repoRoot = Split-Path -Parent $PSScriptRoot
if ([string]::IsNullOrWhiteSpace($OutputDir)) {
    $OutputDir = Join-Path $repoRoot "backups\automaticos"
}

New-Item -ItemType Directory -Force -Path $OutputDir | Out-Null

$outputFile = Join-Path $OutputDir "backup_completo_bd_$Timestamp.sql"

& docker exec $ContainerName pg_dump -U $DatabaseUser -d $DatabaseName | Out-File -FilePath $outputFile -Encoding utf8
if ($LASTEXITCODE -ne 0) {
    throw "Falha ao gerar backup SQL do banco."
}

Write-Host "Backup SQL gerado com sucesso:"
Write-Host $outputFile
