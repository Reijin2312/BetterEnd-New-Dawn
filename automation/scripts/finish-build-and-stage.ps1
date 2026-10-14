[CmdletBinding()]
param(
    [switch]$SkipFabric261
)

$ErrorActionPreference = 'Stop'
$workspace = Split-Path -Parent $PSScriptRoot
$stateDirectory = Join-Path $workspace 'release\background-build'
$statusFile = Join-Path $stateDirectory 'status.json'
$null = New-Item -ItemType Directory -Path $stateDirectory -Force

function Write-Status([string]$state, [string]$step, [string]$message = '') {
    [ordered]@{
        state = $state
        step = $step
        message = $message
        updated = (Get-Date).ToString('o')
        processId = $PID
    } | ConvertTo-Json | Set-Content -LiteralPath $statusFile -Encoding UTF8
}

function Invoke-BuildStep([string]$label, [hashtable]$arguments) {
    Write-Status 'running' $label
    Write-Host "=== $label ===" -ForegroundColor Cyan
    & (Join-Path $PSScriptRoot 'quick-build-bettermods.ps1') @arguments
    if ($LASTEXITCODE -ne 0) { throw "$label failed with exit code $LASTEXITCODE" }
}

try {
    if (-not $SkipFabric261) {
        Invoke-BuildStep 'Fabric 26.1' @{ Loader = 'fabric'; Versions = @('26.1') }
    }
    Invoke-BuildStep 'All 26.2' @{ Versions = @('26.2') }
    Invoke-BuildStep 'All 26.3' @{ Versions = @('26.3') }
    Write-Status 'running' 'Validate and stage profiles'
    & (Join-Path $PSScriptRoot 'stage-bettermods-profiles.ps1')
    if ($LASTEXITCODE -ne 0) { throw "Profile staging failed with exit code $LASTEXITCODE" }
    Write-Status 'complete' 'Done' 'All builds validated, bundled, and installed into 10 profiles.'
    exit 0
} catch {
    Write-Status 'failed' 'Stopped' $_.Exception.Message
    Write-Error $_
    exit 1
}
