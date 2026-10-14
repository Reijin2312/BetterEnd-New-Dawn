[CmdletBinding()]
param(
    [string]$ProfilesRoot = 'C:\Users\Redmi\AppData\Roaming\ModrinthApp\profiles'
)

$ErrorActionPreference = 'Stop'
$workspace = Split-Path -Parent $PSScriptRoot
$stateDirectory = Join-Path $workspace 'release\background-build'
$statusFile = Join-Path $stateDirectory 'status.json'
$bundleRoot = Join-Path $workspace 'release\bundles'
$backupRoot = Join-Path $workspace ("release\profile-backups\betterend-" + (Get-Date -Format 'yyyyMMdd-HHmmss'))
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

function Get-PropertyValue([string]$path, [string]$name) {
    $line = Select-String -LiteralPath $path -Pattern "^$([regex]::Escape($name))\s*=" | Select-Object -First 1
    if (-not $line) { throw "Property '$name' is missing from $path" }
    return ($line.Line -split '=', 2)[1].Trim()
}

function Get-ProjectDirectory([string]$version, [string]$loader) {
    if ($version -eq '1.21.1') {
        if ($loader -eq 'Fabric') { return Join-Path $workspace 'BetterEnd-Fabric-1.21.1' }
        return Join-Path $workspace 'BetterEnd'
    }
    if ($loader -eq 'Fabric') { return Join-Path $workspace "BetterEnd-Fabric-$version" }
    return Join-Path $workspace "BetterEnd-$version"
}

function Get-ProductionJar([string]$projectDirectory) {
    $modVersion = Get-PropertyValue (Join-Path $projectDirectory 'gradle.properties') 'mod_version'
    $jars = @(Get-ChildItem -LiteralPath (Join-Path $projectDirectory 'build\libs') -File -Filter '*.jar' |
        Where-Object {
            $_.Name -notmatch '(?i)(sources|javadoc|dev|shadow|plain)' -and
            $_.BaseName -match "(^|[-_])$([regex]::Escape($modVersion))($|[-_])"
        } | Sort-Object LastWriteTimeUtc -Descending)
    if ($jars.Count -eq 0) { throw "No BetterEnd production JAR $modVersion in $projectDirectory\build\libs" }
    return $jars[0]
}

try {
    $versions = @('1.21.1', '1.21.11', '26.1', '26.2', '26.3')
    $loaders = @('NeoForge', 'Fabric')
    $total = $versions.Count * $loaders.Count
    $index = 0

    foreach ($version in $versions) {
        foreach ($loader in $loaders) {
            $index++
            Write-Status 'running' "Build BetterEnd $loader $version ($index/$total)" 'Fast production build; publishing is disabled.'
            $loaderArgument = if ($loader -eq 'Fabric') { 'fabric' } else { 'neo' }
            & (Join-Path $PSScriptRoot 'quick-build-bettermods.ps1') -Set end -Loader $loaderArgument -Versions @($version)
            if ($LASTEXITCODE -ne 0) { throw "BetterEnd $loader $version failed with exit code $LASTEXITCODE" }
        }
    }

    Write-Status 'running' 'Stage BetterEnd artifacts' 'Replacing only BetterEnd JARs in the 10 test profiles.'
    foreach ($version in $versions) {
        foreach ($loader in $loaders) {
            $project = Get-ProjectDirectory $version $loader
            $jar = Get-ProductionJar $project
            $profileName = "$loader $version"
            $modsDirectory = Join-Path (Join-Path $ProfilesRoot $profileName) 'mods'
            if (-not (Test-Path -LiteralPath $modsDirectory -PathType Container)) {
                throw "Profile mods directory is missing: $modsDirectory"
            }

            $bundleDirectory = Join-Path $bundleRoot $profileName
            $backupDirectory = Join-Path $backupRoot $profileName
            $null = New-Item -ItemType Directory -Path $bundleDirectory -Force
            $oldJars = @(Get-ChildItem -LiteralPath $modsDirectory -File -Filter '*.jar' |
                Where-Object { $_.Name -match '(?i)^better[-_ ]?end(?:[-_ ].*)?\.jar$' })
            if ($oldJars.Count -gt 0) {
                $null = New-Item -ItemType Directory -Path $backupDirectory -Force
                foreach ($oldJar in $oldJars) {
                    Move-Item -LiteralPath $oldJar.FullName -Destination (Join-Path $backupDirectory $oldJar.Name) -Force
                }
            }
            Copy-Item -LiteralPath $jar.FullName -Destination (Join-Path $modsDirectory $jar.Name) -Force
            Copy-Item -LiteralPath $jar.FullName -Destination (Join-Path $bundleDirectory $jar.Name) -Force
        }
    }

    Write-Status 'complete' 'Done' 'All 10 BetterEnd builds completed and were installed into their test profiles. Nothing was published.'
    exit 0
} catch {
    Write-Status 'failed' 'Stopped' $_.Exception.Message
    Write-Error $_
    exit 1
}
