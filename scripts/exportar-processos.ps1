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

$outputFile = Join-Path $OutputDir "processos_$Timestamp.csv"
$query = "\copy (SELECT id, numero_processo, tipo_processo, origem, unidade_atual, status, data_prazo_final, observacao, duplicata FROM processos ORDER BY numero_processo) TO STDOUT WITH CSV HEADER"

& docker exec $ContainerName psql -U $DatabaseUser -d $DatabaseName -c $query | Out-File -FilePath $outputFile -Encoding utf8
if ($LASTEXITCODE -ne 0) {
    throw "Falha ao exportar os processos."
}

Write-Host "CSV de processos gerado com sucesso:"
Write-Host $outputFile
