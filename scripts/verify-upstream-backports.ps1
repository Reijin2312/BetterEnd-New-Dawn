param()

$ErrorActionPreference = 'Stop'
$workspace = Split-Path -Parent (Split-Path -Parent $PSScriptRoot)
$repos = @(
    'BetterEnd', 'BetterEnd-Fabric-1.21.1',
    'BetterEnd-1.21.11', 'BetterEnd-Fabric-1.21.11',
    'BetterEnd-26.1', 'BetterEnd-Fabric-26.1',
    'BetterEnd-26.2', 'BetterEnd-Fabric-26.2',
    'BetterEnd-26.3', 'BetterEnd-Fabric-26.3'
)
$failures = [System.Collections.Generic.List[string]]::new()
$checks = 0

function Assert-Check([bool]$condition, [string]$repo, [string]$name) {
    $script:checks++
    if (-not $condition) { $script:failures.Add("$repo`: $name") }
}

function Read-Relative([string]$root, [string]$path) {
    $full = Join-Path $root $path
    Assert-Check (Test-Path -LiteralPath $full) (Split-Path $root -Leaf) "missing $path"
    if (Test-Path -LiteralPath $full) { return Get-Content -LiteralPath $full -Raw }
    return ''
}

function Assert-Match([string]$text, [string]$pattern, [string]$repo, [string]$name) {
    Assert-Check ([regex]::IsMatch($text, $pattern, [Text.RegularExpressions.RegexOptions]::Singleline)) $repo $name
}

function Assert-NoMatch([string]$text, [string]$pattern, [string]$repo, [string]$name) {
    Assert-Check (-not [regex]::IsMatch($text, $pattern, [Text.RegularExpressions.RegexOptions]::Singleline)) $repo $name
}

function Assert-Json([string]$root, [string]$path, [string]$repo) {
    $text = Read-Relative $root $path
    if (-not $text) { return '' }
    try {
        $null = $text | ConvertFrom-Json
        Assert-Check $true $repo "valid JSON: $path"
    } catch {
        Assert-Check $false $repo "invalid JSON: $path ($($_.Exception.Message))"
    }
    return $text
}

$recipes = @('bolux_mushroom', 'chorus_mushroom', 'end_berry', 'end_fish')
$commonTags = @(
    'src/main/generated/data/minecraft/tags/block/bars.json',
    'src/main/generated/data/minecraft/tags/block/chains.json',
    'src/main/generated/data/minecraft/tags/item/bars.json',
    'src/main/generated/data/minecraft/tags/item/chains.json'
)

foreach ($repo in $repos) {
    Write-Host "[BetterEnd backports] $repo"
    $root = Join-Path $workspace $repo
    $hasFullWriteZone = $repo -match '(Fabric-26\.1|26\.2|26\.3)'
    Assert-Check (Test-Path -LiteralPath $root) $repo 'repository is missing'
    if (-not (Test-Path -LiteralPath $root)) { continue }

    $subjects = git -C $root log --format=%s -12
    Assert-Check ($subjects -contains 'Backport upstream stability and gameplay fixes') $repo 'upstream stability/gameplay commit is missing'
    Assert-Check ($subjects -contains 'Fix structures across chunk reloads') $repo 'structure reload commit is missing'
    Assert-Check ($subjects -contains 'Keep large End features inside worldgen bounds') $repo 'worldgen bounds commit is missing'
    Assert-Check ($subjects -contains 'Add guidebook reward configuration') $repo 'guidebook configuration commit is missing'

    $sulphurLoot = Assert-Json $root 'src/main/generated/data/betterend/loot_table/blocks/sulphur_crystal.json' $repo
    foreach ($age in 0..2) {
        Assert-Match $sulphurLoot ('"age"\s*:\s*"' + $age + '"') $repo "sulphur crystal age $age loot behavior"
    }
    Assert-Match $sulphurLoot 'betterend:crystalline_sulphur' $repo 'fully-grown sulphur crystal secondary drop'

    foreach ($food in $recipes) {
        $recipe = Assert-Json $root "src/main/generated/data/betterend/recipe/${food}_smelting.json" $repo
        Assert-Match $recipe 'minecraft:smelting' $repo "$food furnace recipe"
    }
    foreach ($tag in $commonTags) {
        $tagText = Assert-Json $root $tag $repo
        Assert-Match $tagText 'betterend:(terminite|thallasium)' $repo "BetterEnd entries in $tag"
    }

    $pathBlock = Read-Relative $root 'src/main/java/org/betterx/betterend/blocks/EndPathBlock.java'
    Assert-Match $pathBlock 'private\s+final\s+Block\s+(terrain|source|base)|BlockState\s+reverted' $repo 'path remembers/restores its End terrain block'
    Assert-NoMatch $pathBlock 'Blocks\.DIRT' $repo 'path must not revert to vanilla dirt'

    $blocks = Read-Relative $root 'src/main/java/org/betterx/betterend/registry/EndBlocks.java'
    Assert-Match $blocks 'new\s+EndPathBlock\s*\(' $repo 'End paths use EndPathBlock'

    $pedestal = Read-Relative $root 'src/main/java/org/betterx/betterend/blocks/EternalPedestal.java'
    Assert-Match $pedestal 'getCenter\(\)\s*!=\s*null' $repo 'key pedestal rejects a ritual without a center'
    Assert-Match $pedestal 'linkRitual\(null\)' $repo 'invalid pedestal ritual is unlinked'

    $ritual = Read-Relative $root 'src/main/java/org/betterx/betterend/rituals/EternalRitual.java'
    Assert-Match $ritual 'center\s*==\s*null' $repo 'EternalRitual null-center guard'

    $portals = Read-Relative $root 'src/main/java/org/betterx/betterend/registry/EndPortals.java'
    Assert-Match $portals 'state\s*<\s*0\s*\|\|\s*state\s*>=\s*portals\.length' $repo 'portal color ID bounds check'

    $portalBuilder = Read-Relative $root 'src/main/java/org/betterx/betterend/portal/PortalBuilder.java'
    Assert-Match $portalBuilder 'GENERATE_SEARCH_RADIUS' $repo 'bounded Eternal Portal generation radius'
    Assert-Match $portalBuilder 'ring\s*>\s*GENERATE_SEARCH_RADIUS.*isColumnLoaded' $repo 'portal search avoids generating the outer search area'
    Assert-Match $portalBuilder 'clampToBounds' $repo 'portal destination respects the world border'

    $biomes = Read-Relative $root 'src/main/java/org/betterx/betterend/world/biome/EndBiomeBuilder.java'
    Assert-Match $biomes 'toLowerCase\(Locale\.ROOT\)' $repo 'locale-stable biome IDs'
    Assert-NoMatch $biomes 'toLowerCase\(\)' $repo 'locale-dependent biome IDs must not return'

    foreach ($feature in @('WallScatterFeature', 'InvertedScatterFeature')) {
        $scatter = Read-Relative $root "src/main/java/org/betterx/betterend/world/features/$feature.java"
        Assert-Match $scatter 'synchronized\s+boolean\s+place' $repo "$feature concurrent worldgen guard"
        if ($hasFullWriteZone) {
            Assert-Match $scatter 'WriteZone' $repo "$feature write-zone clipping"
        }
    }

    $umbrella = Read-Relative $root 'src/main/java/org/betterx/betterend/blocks/UmbrellaTreeClusterEmptyBlock.java'
    Assert-Match $umbrella '\.randomTicks\(\)' $repo 'empty umbrella clusters receive random ticks'
    Assert-Match $umbrella 'NATURAL.*UMBRELLA_TREE_CLUSTER' $repo 'natural umbrella clusters refill'

    $lacugrove = Read-Relative $root 'src/main/java/org/betterx/betterend/world/features/trees/LacugroveFeature.java'
    Assert-Match $lacugrove 'ENDSTONE_DUST' $repo 'Lacugrove accepts endstone dust'
    if ($hasFullWriteZone) {
        Assert-Match $lacugrove 'WriteZone' $repo 'Lacugrove generation is clipped to its write zone'
    }

    $glowshroom = Read-Relative $root 'src/main/java/org/betterx/betterend/world/features/trees/MossyGlowshroomFeature.java'
    Assert-Match $glowshroom 'PALLIDIUM_FULL' $repo 'Mossy Glowshroom accepts pallidium'
    Assert-Match $glowshroom 'SANGNUM' $repo 'Mossy Glowshroom accepts sangnum'
    if ($hasFullWriteZone) {
        Assert-Match $glowshroom 'WriteZone' $repo 'Mossy Glowshroom generation is clipped to its write zone'
    }

    foreach ($waterlogged in @('HydrothermalVentBlock', 'SmaragdantCrystalShardBlock', 'SulphurCrystalBlock')) {
        $block = Read-Relative $root "src/main/java/org/betterx/betterend/blocks/$waterlogged.java"
        Assert-Match $block 'setValue\(WATERLOGGED,.*getFluidState' $repo "$waterlogged preserves water when placed/generated"
    }
    $cave = Read-Relative $root 'src/main/java/org/betterx/betterend/world/structures/piece/SulphuricCavePiece.java'
    Assert-Match $cave 'WATERLOGGED' $repo 'sulphuric cave crystals receive correct waterlogged state'

    $basePiece = Read-Relative $root 'src/main/java/org/betterx/betterend/world/structures/piece/BasePiece.java'
    Assert-Match $basePiece 'hasPrimedHeightmap' $repo 'reloaded structure pieces avoid unprimed heightmaps'
    Assert-Match $basePiece 'removeBlockEntity' $repo 'structure replacement removes stale block entities'
    foreach ($piece in @('MountainPiece', 'PaintedMountainPiece', 'CrystalMountainPiece', 'LakePiece', 'EndLakePiece')) {
        $pieceText = Read-Relative $root "src/main/java/org/betterx/betterend/world/structures/piece/$piece.java"
        Assert-Match $pieceText '(worldgenSurfaceHeight|getWorldgenSurface|setChunkBlock)' $repo "$piece uses reload-safe structure helpers"
    }

    $ship = Read-Relative $root 'src/main/java/org/betterx/betterend/world/features/CrashedShipFeature.java'
    Assert-Match $ship 'BlockFixer\.fixBlocks\(.*(WriteZone\.of\(world\)\.toBoundingBox\(\)|bounds)' $repo 'crashed-ship block fixer is bounded'
    $lotus = Read-Relative $root 'src/main/java/org/betterx/betterend/world/features/EndLotusLeafFeature.java'
    Assert-Match $lotus '(WriteZone.*contains|WorldGenRegion.*hasChunk)' $repo 'End Lotus avoids reading outside generated chunks'
    if ($hasFullWriteZone) {
        $geyser = Read-Relative $root 'src/main/java/org/betterx/betterend/world/features/terrain/GeyserFeature.java'
        Assert-Match $geyser 'WriteZone' $repo 'Geyser generation is clipped to its write zone'
    }

    $config = Read-Relative $root 'src/main/java/org/betterx/betterend/config/GeneratorConfig.java'
    $integration = Read-Relative $root 'src/main/java/org/betterx/betterend/integration/Integrations.java'
    Assert-Match $config 'giveGuideBook' $repo 'guidebook reward config option'
    Assert-Match $integration 'giveGuideBook' $repo 'guidebook reward respects config option'
}

if ($failures.Count) {
    Write-Host "`nFAILED: $($failures.Count) of $checks BetterEnd backport checks" -ForegroundColor Red
    $failures | ForEach-Object { Write-Host " - $_" -ForegroundColor Red }
    exit 1
}
Write-Host "PASSED: $checks BetterEnd upstream-backport checks across $($repos.Count) projects." -ForegroundColor Green
