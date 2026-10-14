param(
    [switch]$Compile,
    [switch]$ServerSmoke,
    [int]$ServerTimeoutSeconds = 240,
    [string[]]$ServerProjects = @()
)

$ErrorActionPreference = 'Stop'
$workspace = Split-Path -Parent $PSScriptRoot

$endRepos = @(
    'BetterEnd', 'BetterEnd-Fabric-1.21.1',
    'BetterEnd-1.21.11', 'BetterEnd-Fabric-1.21.11',
    'BetterEnd-26.1', 'BetterEnd-Fabric-26.1',
    'BetterEnd-26.2', 'BetterEnd-Fabric-26.2',
    'BetterEnd-26.3', 'BetterEnd-Fabric-26.3'
)

$netherRepos = @(
    'BetterNether', 'BetterNether-Fabric-1.21.1',
    'BetterNether-1.21.11', 'BetterNether-Fabric-1.21.11',
    'BetterNether-26.1', 'BetterNether-Fabric-26.1',
    'BetterNether-26.2', 'BetterNether-Fabric-26.2',
    'BetterNether-26.3', 'BetterNether-Fabric-26.3'
)

$failures = [System.Collections.Generic.List[string]]::new()
$checks = 0

function Assert-True([bool]$condition, [string]$message) {
    $script:checks++
    if (-not $condition) {
        $script:failures.Add($message)
    }
}

function Read-Tree([string]$repo, [string]$filter = '*.java') {
    $root = Join-Path $workspace $repo
    $files = Get-ChildItem -LiteralPath (Join-Path $root 'src') -Recurse -File -Filter $filter -ErrorAction SilentlyContinue
    return ($files | ForEach-Object { Get-Content -LiteralPath $_.FullName -Raw }) -join "`n"
}

function Assert-Contains([string]$text, [string]$needle, [string]$repo, [string]$feature) {
    Assert-True ($text.Contains($needle)) "$repo`: missing regression guard for $feature ($needle)"
}

function Stop-ProcessTree([int]$processId) {
    $children = Get-CimInstance Win32_Process -Filter "ParentProcessId=$processId" -ErrorAction SilentlyContinue
    foreach ($child in $children) { Stop-ProcessTree ([int]$child.ProcessId) }
    Stop-Process -Id $processId -Force -ErrorAction SilentlyContinue
}

function Test-ServerStart([string]$repo) {
    $root = Join-Path $workspace $repo
    $runDirectory = Join-Path $root 'run'
    $eulaFile = Join-Path $runDirectory 'eula.txt'
    $propertiesFile = Join-Path $runDirectory 'server.properties'
    $worldName = 'bettermods-server-smoke-world'
    $worldDirectory = Join-Path $runDirectory $worldName
    $oldEula = if (Test-Path -LiteralPath $eulaFile) { [System.IO.File]::ReadAllText($eulaFile) } else { $null }
    $oldProperties = if (Test-Path -LiteralPath $propertiesFile) { [System.IO.File]::ReadAllText($propertiesFile) } else { $null }
    $null = New-Item -ItemType Directory -Path $runDirectory -Force
    [System.IO.File]::WriteAllText($eulaFile, "eula=true`r`n")
    [System.IO.File]::WriteAllText($propertiesFile, "level-name=$worldName`r`nonline-mode=false`r`n")
    $stdout = Join-Path ([System.IO.Path]::GetTempPath()) ("bettermods-$repo-server-out.log")
    $stderr = Join-Path ([System.IO.Path]::GetTempPath()) ("bettermods-$repo-server-err.log")
    Remove-Item -LiteralPath $stdout, $stderr -Force -ErrorAction SilentlyContinue

    Write-Host "[server] $repo"
    $process = Start-Process -FilePath (Join-Path $root 'gradlew.bat') `
        -ArgumentList @('runServer', '--no-daemon', '--console=plain') `
        -WorkingDirectory $root -RedirectStandardOutput $stdout -RedirectStandardError $stderr -PassThru
    $deadline = [DateTime]::UtcNow.AddSeconds($ServerTimeoutSeconds)
    $started = $false
    $failed = $false

    try {
        while (-not $process.HasExited -and [DateTime]::UtcNow -lt $deadline) {
            Start-Sleep -Milliseconds 500
            $log = ((Get-Content -LiteralPath $stdout -Raw -ErrorAction SilentlyContinue) + "`n" +
                    (Get-Content -LiteralPath $stderr -Raw -ErrorAction SilentlyContinue))
            if ($log -match '(?m)Done \([0-9.,]+s\)!|For help, type "help"') { $started = $true; break }
            if ($log -match '(?m)Failed to start the minecraft server|BUILD FAILED|Mod Loading has failed|Could not execute entrypoint') {
                $failed = $true
                break
            }
            $process.Refresh()
        }
    } finally {
        if (-not $process.HasExited) { Stop-ProcessTree $process.Id }
        if ($null -eq $oldEula) { Remove-Item -LiteralPath $eulaFile -Force -ErrorAction SilentlyContinue }
        else { [System.IO.File]::WriteAllText($eulaFile, $oldEula) }
        if ($null -eq $oldProperties) { Remove-Item -LiteralPath $propertiesFile -Force -ErrorAction SilentlyContinue }
        else { [System.IO.File]::WriteAllText($propertiesFile, $oldProperties) }
        $resolvedRun = [System.IO.Path]::GetFullPath($runDirectory).TrimEnd('\') + '\'
        $resolvedWorld = [System.IO.Path]::GetFullPath($worldDirectory)
        if ($resolvedWorld.StartsWith($resolvedRun, [System.StringComparison]::OrdinalIgnoreCase)) {
            Remove-Item -LiteralPath $resolvedWorld -Recurse -Force -ErrorAction SilentlyContinue
        }
    }

    if (-not $started) {
        $tail = @(
            Get-Content -LiteralPath $stdout -Tail 40 -ErrorAction SilentlyContinue
            Get-Content -LiteralPath $stderr -Tail 40 -ErrorAction SilentlyContinue
        ) -join "`n"
        $reason = if ($failed) { 'reported a startup failure' } elseif ($process.HasExited) { "exited with code $($process.ExitCode) before startup" } else { "timed out after $ServerTimeoutSeconds seconds" }
        $script:failures.Add("$repo`: server $reason`n$tail")
    }
    $script:checks++
}

foreach ($runner in @(
    'BetterEnd/scripts/verify-upstream-backports.ps1',
    'BetterNether/scripts/verify-upstream-backports.ps1'
)) {
    $runnerPath = Join-Path $workspace $runner
    Write-Host "[upstream contracts] $runner"
    & $runnerPath
    Assert-True ($LASTEXITCODE -eq 0) "$runner`: upstream-backport contract tests failed"
}

foreach ($repo in $endRepos) {
    Write-Host "[audit] $repo"
    $root = Join-Path $workspace $repo
    Assert-True (Test-Path -LiteralPath $root) "$repo`: repository is missing"
    if (-not (Test-Path -LiteralPath $root)) { continue }

    $java = Read-Tree $repo
    Assert-Contains $java 'giveGuideBook' $repo 'optional guidebook reward'
    Assert-Contains $java 'Locale.ROOT' $repo 'locale-stable registry identifiers'
    Assert-Contains $java 'state < 0 || state >= portals.length' $repo 'safe Eternal Portal color lookup'
    $ritual = Get-ChildItem -LiteralPath (Join-Path $root 'src/main/java') -Recurse -File -Filter 'EternalRitual.java' | Select-Object -First 1
    if ($ritual) {
        $ritualText = Get-Content -LiteralPath $ritual.FullName -Raw
        Assert-True (($ritualText -match 'center\s*==\s*null') -or ($ritualText -match 'center\s*!=\s*null')) "$repo`: EternalRitual has no null-center guard"
    } else {
        Assert-True $false "$repo`: EternalRitual.java is missing"
    }

    $portalBuilder = Get-ChildItem -LiteralPath (Join-Path $root 'src/main/java') -Recurse -File -Filter 'PortalBuilder.java' | Select-Object -First 1
    if ($portalBuilder) {
        $portalText = Get-Content -LiteralPath $portalBuilder.FullName -Raw
        Assert-True (($portalText -match 'MAX_SEARCH') -or ($portalText -match 'findPortalAround') -or ($portalText -match 'searchRadius')) "$repo`: bounded portal lookup implementation is missing"
    } else {
        Assert-True $false "$repo`: PortalBuilder.java is missing"
    }

    $mountainPiece = Get-ChildItem -LiteralPath (Join-Path $root 'src/main/java') -Recurse -File -Filter 'MountainPiece.java' | Select-Object -First 1
    if ($mountainPiece) {
        $mountainText = Get-Content -LiteralPath $mountainPiece.FullName -Raw
        Assert-Contains $mountainText 'mut.setY(center.getY());' $repo 'Painted Mountains biome lookup at structure height'
    } else {
        Assert-True $false "$repo`: MountainPiece.java is missing"
    }

    Assert-True (-not ($java -match 'toLowerCase\(\s*\)')) "$repo`: locale-dependent toLowerCase() returned"
}

# Portal search can run long enough for ordinary block-entity updates to expose an empty
# final pedestal on the client. The ritual packet must therefore carry both the key and the exact
# occupied-pedestal mask, just like the proven 1.20.1 implementation.
foreach ($repo in $endRepos) {
    $root = Join-Path $workspace $repo
    $ritualUpdate = Get-ChildItem -LiteralPath (Join-Path $root 'src/main/java') -Recurse -File -Filter 'RitualUpdate.java' | Select-Object -First 1
    $pedestalEntity = Get-ChildItem -LiteralPath (Join-Path $root 'src/main/java') -Recurse -File -Filter 'PedestalBlockEntity.java' | Select-Object -First 1
    $ritualText = Get-Content -LiteralPath $ritualUpdate.FullName -Raw
    $pedestalText = Get-Content -LiteralPath $pedestalEntity.FullName -Raw
    $pedestalRenderer = Get-Item -LiteralPath (Join-Path $root 'src/main/java/org/betterx/betterend/client/render/PedestalItemRenderer.java')
    $pedestalRendererText = Get-Content -LiteralPath $pedestalRenderer.FullName -Raw
    Assert-Contains $ritualText 'keyItemId' $repo 'Eternal Portal activation key synchronization'
    Assert-Contains $ritualText 'pedestalItemMask' $repo 'Eternal Portal pedestal occupancy synchronization'
    Assert-Contains $pedestalText 'setClientItem' $repo 'Eternal Portal client pedestal item synchronization'
    Assert-Contains $pedestalText 'sendBlockUpdated' $repo 'pedestal removal block-entity synchronization'
    Assert-Contains $pedestalRendererText 'PedestalBlock.HAS_ITEM' $repo 'pedestal renderer state guard'
}

# Eternal Portal activation has a deterministic three-second server-side charge phase.
# Portal lookup remains bounded, but can no longer swallow the visual phase when it completes instantly.
foreach ($repo in @('BetterEnd-1.20.1', 'BetterEnd-Fabric-1.20.1') + $endRepos) {
    $root = Join-Path $workspace $repo
    $ritualText = Get-Content -LiteralPath (Join-Path $root 'src/main/java/org/betterx/betterend/rituals/EternalRitual.java') -Raw
    $pedestalText = Get-Content -LiteralPath (Join-Path $root 'src/main/java/org/betterx/betterend/blocks/EternalPedestal.java') -Raw
    Assert-Contains $ritualText 'ACTIVATION_CHARGE_TICKS = 60' $repo 'Eternal Portal three-second charge duration'
    Assert-Contains $ritualText 'scheduleTick(getActivationPedestalPos()' $repo 'Eternal Portal deferred activation'
    Assert-Contains $ritualText 'hasValidStructureFor' $repo 'Eternal Portal pre-activation revalidation'
    Assert-Contains $pedestalText 'finishActivation()' $repo 'Eternal Portal scheduled activation callback'
    Assert-Contains $pedestalText 'cancelActivation()' $repo 'Eternal Portal charge cancellation'
    Assert-Contains $pedestalText 'ritual.willActivate()' $repo 'Eternal Portal charging particles'
    Assert-Contains $pedestalText '!level.getBlockState(ritual.getCenter()).is(EndBlocks.END_PORTAL_BLOCK)' $repo 'Eternal Portal particle cutoff'
    Assert-Contains $pedestalText 'dispatchChargingParticles' $repo 'Eternal Portal all-pedestal charge dispatcher'
    Assert-Contains $pedestalText 'EternalRitual.pedestalOffsets()' $repo 'Eternal Portal charge particles from every pedestal'
    Assert-Contains $pedestalText 'CHARGE_PARTICLE_TICKS' $repo 'Eternal Portal charge particle deduplication'
    Assert-True (-not ($pedestalText -match 'ritual\.isActive\(\)\s*\|\|\s*ritual\.willActivate\(\)')) "$repo`: active portals still emit charging particles"
}

foreach ($repo in $netherRepos) {
    Write-Host "[audit] $repo"
    $root = Join-Path $workspace $repo
    Assert-True (Test-Path -LiteralPath $root) "$repo`: repository is missing"
    if (-not (Test-Path -LiteralPath $root)) { continue }

    $java = Read-Tree $repo
    Assert-Contains $java 'FLUID_SPRINGS' $repo 'Nether City generation order'
    Assert-Contains $java 'StructureAvoidingProcessor' $repo 'city/fortress and bastion conflict protection'
    Assert-Contains $java 'buildingsWithin' $repo 'city-aware vegetation placement'
    Assert-Contains $java 'BehaviourSapling' $repo 'sapling composting behavior'
    Assert-Contains $java 'ColorBlue' $repo 'legacy firefly color migration'
    Assert-True ($java -match 'ColorUtil(?:ARGB32)?\.color\(\(int\) \(r \* 0xFF\), \(int\) \(g \* 0xFF\), \(int\) \(b \* 0xFF\)\)') "$repo`: legacy firefly blue channel is not read from b"
    Assert-True (-not ($java -match 'ColorUtil(?:ARGB32)?\.color\(\(int\) \(r \* 0xFF\), \(int\) \(g \* 0xFF\), \(int\) \(g \* 0xFF\)\)')) "$repo`: legacy firefly blue=green bug returned"
    Assert-True (-not ($java -match '\(byte\) \([^\r\n]+\* 255F')) "$repo`: signed-byte firefly color packing returned"
}

# The entity predicate codec changed in Minecraft 26.3. Validate the expected schema for each branch,
# rather than merely banning one spelling globally. Dedicated-server startup below remains the final codec test.
foreach ($repo in $endRepos) {
    $root = Join-Path $workspace $repo
    $slimeLoot = Join-Path $root 'src/main/resources/data/betterend/loot_table/entities/end_slime.json'
    Assert-True (Test-Path -LiteralPath $slimeLoot) "$repo`: end_slime loot table is missing"
    if (Test-Path -LiteralPath $slimeLoot) {
        try {
            $text = Get-Content -LiteralPath $slimeLoot -Raw
            $null = $text | ConvertFrom-Json
            $cubePredicates = ([regex]::Matches($text, '"minecraft:type_specific/cube_mob"')).Count
            $legacyPredicates = ([regex]::Matches($text, '"type_specific"\s*:\s*\{\s*"type"\s*:\s*"minecraft:slime"')).Count
            if ($repo -like '*26.3*') {
                Assert-True ($cubePredicates -eq 3 -and $legacyPredicates -eq 0) "$repo`: Minecraft 26.3 needs exactly three minecraft:type_specific/cube_mob predicates"
            } else {
                Assert-True ($legacyPredicates -eq 3 -and $cubePredicates -eq 0) "$repo`: this Minecraft version needs exactly three legacy slime type_specific predicates"
            }
        } catch {
            Assert-True $false "$repo`: end_slime loot table is not valid JSON: $($_.Exception.Message)"
        }
    }
}

if ($Compile) {
    foreach ($repo in @($endRepos + $netherRepos)) {
        $root = Join-Path $workspace $repo
        Write-Host "[compile] $repo"
        Push-Location $root
        try {
            & .\gradlew.bat compileJava --no-daemon
            Assert-True ($LASTEXITCODE -eq 0) "$repo`: compileJava failed with exit code $LASTEXITCODE"
        } finally {
            Pop-Location
        }
    }
}

if ($ServerSmoke) {
    $servers = if ($ServerProjects.Count -gt 0) { $ServerProjects } else { @($endRepos + $netherRepos) }
    foreach ($repo in $servers) {
        Assert-True (@($endRepos + $netherRepos) -contains $repo) "$repo`: unknown server smoke-test project"
        if (-not (@($endRepos + $netherRepos) -contains $repo)) { continue }
        Test-ServerStart $repo
    }
}

if ($failures.Count -gt 0) {
    Write-Host "`nFAILED: $($failures.Count) of $checks checks" -ForegroundColor Red
    $failures | ForEach-Object { Write-Host " - $_" -ForegroundColor Red }
    exit 1
}

Write-Host "PASSED: $checks regression checks across 20 BetterEnd/BetterNether projects." -ForegroundColor Green
