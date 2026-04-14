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

$outputFile = Join-Path $OutputDir "historico_processos_$Timestamp.csv"
$query = "\copy (SELECT hp.id, hp.processo_id, p.numero_processo, hp.usuario_id, u.login AS usuario_login, hp.data_atualizacao, hp.status_anterior, hp.status_novo, hp.unidade_anterior, hp.unidade_nova, hp.observacao_da_mudanca FROM historico_processos hp LEFT JOIN processos p ON p.id = hp.processo_id LEFT JOIN usuarios u ON u.id = hp.usuario_id ORDER BY hp.data_atualizacao DESC, hp.id DESC) TO STDOUT WITH CSV HEADER"

& docker exec $ContainerName psql -U $DatabaseUser -d $DatabaseName -c $query | Out-File -FilePath $outputFile -Encoding utf8
if ($LASTEXITCODE -ne 0) {
    throw "Falha ao exportar o historico."
}

Write-Host "CSV de historico gerado com sucesso:"
Write-Host $outputFile
