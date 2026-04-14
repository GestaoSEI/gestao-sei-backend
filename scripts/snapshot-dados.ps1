param(
    [string]$OutputDir,
    [string]$ContainerName = "gestao-sei-backend-db",
    [string]$DatabaseName = "gestaosei",
    [string]$DatabaseUser = "postgres"
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$repoRoot = Split-Path -Parent $PSScriptRoot
if ([string]::IsNullOrWhiteSpace($OutputDir)) {
    $OutputDir = Join-Path $repoRoot "backups\automaticos"
}

$timestamp = Get-Date -Format "yyyy-MM-dd_HHmmss"

& (Join-Path $PSScriptRoot "backup-bd.ps1") -OutputDir $OutputDir -ContainerName $ContainerName -DatabaseName $DatabaseName -DatabaseUser $DatabaseUser -Timestamp $timestamp
& (Join-Path $PSScriptRoot "exportar-processos.ps1") -OutputDir $OutputDir -ContainerName $ContainerName -DatabaseName $DatabaseName -DatabaseUser $DatabaseUser -Timestamp $timestamp
& (Join-Path $PSScriptRoot "exportar-historico.ps1") -OutputDir $OutputDir -ContainerName $ContainerName -DatabaseName $DatabaseName -DatabaseUser $DatabaseUser -Timestamp $timestamp

Write-Host ""
Write-Host "Snapshot concluido com sucesso."
Write-Host "Pasta de saida: $OutputDir"
