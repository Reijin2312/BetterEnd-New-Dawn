param(
    [ValidateSet('all','end','nether','libraries','wunder')]
    [string]$Set = 'all',
    [ValidateSet('all','neo','fabric')]
    [string]$Loader = 'all',
    [string[]]$Versions = @('1.21.1','1.21.11','26.1','26.2','26.3'),
    [switch]$Online,
    [switch]$RefreshMinecraftArtifacts,
    [switch]$RefreshResources
)

$ErrorActionPreference = 'Stop'
$workspace = Split-Path -Parent $PSScriptRoot

$projects = @()
foreach ($version in $Versions) {
    switch ($version) {
        '1.21.1' {
            $endNeo='BetterEnd'; $netherNeo='BetterNether'
            $endFabric='BetterEnd-Fabric-1.21.1'; $netherFabric='BetterNether-Fabric-1.21.1'
            $bclNeo='BCLib-1.21.1-Neoforge'; $bclFabric='BCLib-1.21.1-Fabric'
            $woverNeo='WorldWeaver-1.21.1-Neoforge'; $woverFabric='WorldWeaver-1.21.1-Fabric'
            $wunderNeo='WunderLib-1.21.1-Neoforge'; $wunderFabric='WunderLib-Fabric-1.21.1'
        }
        default {
            $endNeo="BetterEnd-$version"; $netherNeo="BetterNether-$version"
            $endFabric="BetterEnd-Fabric-$version"; $netherFabric="BetterNether-Fabric-$version"
            $bclNeo="BCLib-$version"; $bclFabric="BCLib-Fabric-$version"
            $woverNeo="WorldWeaver-$version"; $woverFabric="WorldWeaver-Fabric-$version"
            $wunderNeo="WunderLib-$version"; $wunderFabric="WunderLib-Fabric-$version"
        }
    }

    if ($Set -in @('all','libraries')) { $projects += $bclNeo,$bclFabric,$woverNeo,$woverFabric,$wunderNeo,$wunderFabric }
    if ($Set -eq 'wunder') { $projects += $wunderNeo,$wunderFabric }
    if ($Set -in @('all','end')) { $projects += $endNeo,$endFabric }
    if ($Set -in @('all','nether')) { $projects += $netherNeo,$netherFabric }
}

$projects = $projects | Select-Object -Unique
if ($Loader -eq 'neo') { $projects = $projects | Where-Object { $_ -notmatch '-Fabric(?:-|$)' } }
if ($Loader -eq 'fabric') { $projects = $projects | Where-Object { $_ -match '-Fabric(?:-|$)' } }
$failed = [System.Collections.Generic.List[string]]::new()
foreach ($project in $projects) {
    $root = Join-Path $workspace $project
    if (-not (Test-Path -LiteralPath (Join-Path $root 'gradlew.bat'))) {
        $failed.Add("$project (missing project/wrapper)")
        continue
    }
    # Loom's plain jar is in the development namespace. Only remapJar is safe
    # to install into a normal Fabric profile; NeoForge's production artifact
    # is produced by jar.
    # The 1.21.x Fabric branches use Loom and publish remapJar. Starting with
    # Minecraft 26.1 these projects use Fabric ModDevGradle; its installable
    # production artifact is produced directly by jar and no remapJar task
    # exists.
    $isFabric = $project -match '-Fabric(?:-|$)'
    $isFabricModDev = $isFabric -and $project -match '-26\.[123]$'
    $task = if ($isFabric -and -not $isFabricModDev) { 'remapJar' } else { 'jar' }
    $timer = [System.Diagnostics.Stopwatch]::StartNew()
    Write-Host "[$project] $task" -ForegroundColor Cyan
    Push-Location $root
    try {
        $gradleArgs = @($task, '--build-cache', '--max-workers=2', '--console=plain', '--quiet')
        if (-not $Online) { $gradleArgs += '--offline' }
        $isNeoForge = -not $isFabric
        if ($isNeoForge) {
            # NeoForge's moddev task is deliberately marked doNotTrackState in these
            # projects. Without this exclusion it downloads the same Mojang manifest
            # once per included WorldWeaver module on every incremental build.
            if (-not $RefreshMinecraftArtifacts) {
                $gradleArgs += @('-x', 'createMinecraftArtifacts')
            }
        }
        if ($isNeoForge -and -not $RefreshResources) {
            $latestJar = Get-ChildItem -LiteralPath (Join-Path $root 'build/libs') -Filter '*.jar' -File -ErrorAction SilentlyContinue |
                Where-Object { $_.Name -notmatch '(sources|javadoc|dev|shadow)' } |
                Sort-Object LastWriteTimeUtc -Descending |
                Select-Object -First 1
            $resourceInputs = @()
            foreach ($resourceDir in @('src/main/resources', 'src/main/generated')) {
                $path = Join-Path $root $resourceDir
                if (Test-Path -LiteralPath $path) {
                    $resourceInputs += Get-ChildItem -LiteralPath $path -File -Recurse
                }
            }
            foreach ($buildInput in @('gradle.properties', 'build.gradle')) {
                $path = Join-Path $root $buildInput
                if (Test-Path -LiteralPath $path) { $resourceInputs += Get-Item -LiteralPath $path }
            }
            $latestResource = $resourceInputs | Sort-Object LastWriteTimeUtc -Descending | Select-Object -First 1
            if ($latestJar -and (-not $latestResource -or $latestResource.LastWriteTimeUtc -le $latestJar.LastWriteTimeUtc)) {
                $gradleArgs += @('-x', 'processResources')
            }
        }
        & cmd.exe /d /q /c gradlew.bat @gradleArgs
        if ($LASTEXITCODE -ne 0) { $failed.Add("$project (exit $LASTEXITCODE)") }
    } finally {
        Pop-Location
        $timer.Stop()
    }
    Write-Host "  $([Math]::Round($timer.Elapsed.TotalSeconds, 1))s" -ForegroundColor DarkGray
}

if ($failed.Count -gt 0) {
    Write-Host "`nFailed: $($failed -join ', ')" -ForegroundColor Red
    exit 1
}
Write-Host "`nBuilt $($projects.Count) production JARs without clean/test/javadoc/datagen." -ForegroundColor Green
