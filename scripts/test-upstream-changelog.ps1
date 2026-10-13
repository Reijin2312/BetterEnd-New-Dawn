param(
    [switch]$SkipGameTests,
    [switch]$SkipStatic,
    [ValidateSet('1.21.1', '1.21.11', '26.1', '26.2', '26.3')]
    [string[]]$Versions = @('1.21.1', '1.21.11', '26.1', '26.2', '26.3')
)

$ErrorActionPreference = 'Stop'
$workspace = Split-Path -Parent (Split-Path -Parent $PSScriptRoot)
$manifest = Get-Content -LiteralPath (Join-Path $PSScriptRoot 'upstream-changelog-coverage.json') -Raw | ConvertFrom-Json
$entries = @($manifest.projects.BetterEnd) + @($manifest.projects.BetterNether)
if ($entries.Count -ne 33 -or ($entries | Where-Object { -not $_.id -or $_.coverage.Count -eq 0 })) {
    throw 'The complete 33-item semantic upstream diff must have declared automated coverage.'
}

function Invoke-Checked([string]$name, [string]$workingDirectory, [string[]]$arguments) {
    Write-Host "`n== $name ==" -ForegroundColor Cyan
    Push-Location -LiteralPath $workingDirectory
    try {
        & $arguments[0] @($arguments[1..($arguments.Count - 1)])
        if ($LASTEXITCODE -ne 0) { throw "$name failed with exit code $LASTEXITCODE" }
    } finally {
        Pop-Location
    }
}

if (-not $SkipStatic) {
    Invoke-Checked 'BetterEnd static contracts (10 projects)' (Join-Path $workspace 'BetterEnd') @(
        'powershell', '-NoProfile', '-ExecutionPolicy', 'Bypass', '-File',
        (Join-Path $workspace 'BetterEnd/scripts/verify-upstream-backports.ps1')
    )
    Invoke-Checked 'BetterNether static contracts (10 projects)' (Join-Path $workspace 'BetterNether') @(
        'powershell', '-NoProfile', '-ExecutionPolicy', 'Bypass', '-File',
        (Join-Path $workspace 'BetterNether/scripts/verify-upstream-backports.ps1')
    )
}

if (-not $SkipGameTests) {
    $families = @(
        @{ Mod = 'BetterEnd'; LegacyNeo = 'BetterEnd'; LegacyFabric = 'BetterEnd-Fabric-1.21.1' },
        @{ Mod = 'BetterNether'; LegacyNeo = 'BetterNether'; LegacyFabric = 'BetterNether-Fabric-1.21.1' }
    )
    foreach ($version in $Versions) {
        foreach ($family in $families) {
            $neo = if ($version -eq '1.21.1') { $family.LegacyNeo } else { "$($family.Mod)-$version" }
            $fabric = if ($version -eq '1.21.1') { $family.LegacyFabric } else { "$($family.Mod)-Fabric-$version" }
            Invoke-Checked "$($family.Mod) $version NeoForge GameTests" (Join-Path $workspace $neo) @(
                (Join-Path $workspace "$neo/gradlew.bat"), 'runGameTestServer', '--build-cache', '--max-workers=2', '--console=plain'
            )
            Invoke-Checked "$($family.Mod) $version Fabric GameTests" (Join-Path $workspace $fabric) @(
                (Join-Path $workspace "$fabric/gradlew.bat"), 'runGametest', '--build-cache', '--max-workers=2', '--console=plain'
            )
        }
    }
}

Write-Host "`nPASSED: 33 semantic changelog items verified for $($Versions -join ', ') on both loaders." -ForegroundColor Green

