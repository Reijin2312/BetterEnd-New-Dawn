param(
    [switch]$SkipGameTests,
    [switch]$SkipStatic
)

$ErrorActionPreference = 'Stop'
$workspace = Split-Path -Parent $PSScriptRoot
$manifestPath = Join-Path $PSScriptRoot 'upstream-changelog-coverage-1.21.1.json'
$manifest = Get-Content -LiteralPath $manifestPath -Raw | ConvertFrom-Json
$entries = @($manifest.projects.BetterEnd) + @($manifest.projects.BetterNether)

if ($manifest.minecraft -ne '1.21.1') {
    throw "Coverage manifest targets '$($manifest.minecraft)', expected 1.21.1"
}
if ($entries.Count -eq 0 -or ($entries | Where-Object { -not $_.id -or $_.coverage.Count -eq 0 })) {
    throw 'Every upstream changelog entry must have an id and at least one automated coverage layer.'
}
$duplicates = $entries | Group-Object id | Where-Object Count -gt 2
if ($duplicates) {
    throw "Unexpected duplicate coverage ids: $($duplicates.Name -join ', ')"
}

function Invoke-Checked([string]$name, [string]$workingDirectory, [string[]]$arguments) {
    Write-Host "`n== $name ==" -ForegroundColor Cyan
    Push-Location -LiteralPath $workingDirectory
    try {
        & $arguments[0] @($arguments[1..($arguments.Count - 1)])
        if ($LASTEXITCODE -ne 0) {
            throw "$name failed with exit code $LASTEXITCODE"
        }
    } finally {
        Pop-Location
    }
}

if (-not $SkipStatic) {
    Invoke-Checked 'BetterEnd static backport contracts' (Join-Path $workspace 'BetterEnd') @(
        'powershell', '-NoProfile', '-ExecutionPolicy', 'Bypass', '-File',
        (Join-Path $workspace 'BetterEnd/scripts/verify-upstream-backports.ps1'), '-Only1211'
    )
    Invoke-Checked 'BetterNether static backport contracts' (Join-Path $workspace 'BetterNether') @(
        'powershell', '-NoProfile', '-ExecutionPolicy', 'Bypass', '-File',
        (Join-Path $workspace 'BetterNether/scripts/verify-upstream-backports.ps1'), '-Only1211'
    )
}

if (-not $SkipGameTests) {
    $runs = @(
        @{ Name = 'BetterEnd NeoForge GameTests'; Repo = 'BetterEnd'; Task = 'runGameTestServer' },
        @{ Name = 'BetterEnd Fabric GameTests'; Repo = 'BetterEnd-Fabric-1.21.1'; Task = 'runGametest' },
        @{ Name = 'BetterNether NeoForge GameTests'; Repo = 'BetterNether'; Task = 'runGameTestServer' },
        @{ Name = 'BetterNether Fabric GameTests'; Repo = 'BetterNether-Fabric-1.21.1'; Task = 'runGametest' }
    )
    foreach ($run in $runs) {
        $repo = Join-Path $workspace $run.Repo
        Invoke-Checked $run.Name $repo @(
            (Join-Path $repo 'gradlew.bat'), $run.Task, '--build-cache', '--max-workers=2', '--console=plain'
        )
    }
}

Write-Host "`nPASSED: $($entries.Count) upstream changelog entries have declared automated coverage for both 1.21.1 loaders." -ForegroundColor Green
