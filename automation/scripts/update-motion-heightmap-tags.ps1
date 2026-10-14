[CmdletBinding()]
param()

$ErrorActionPreference = 'Stop'
$workspace = Split-Path -Parent $PSScriptRoot

$colors = '(?:black|blue|brown|cyan|gray|green|light_blue|light_gray|lime|magenta|orange|pink|purple|red|white|yellow)'
$solidFamilyPattern = @(
    '(?:^|_)(?:barrel|bookshelf|chest|composter|crafting_table|furnace)$',
    '(?:^|_)chiseled_bookshelf$',
    '(?:^|_)(?:bricks?|tiles?|pillar|polished|path|roof)$',
    "_block(?:_$colors)?$",
    "_lantern(?:_$colors)?$",
    '(?:^|_)chandelier$',
    '(?:^|_)pedestal$',
    '^quartz_glass(?:_framed)?(?:_[a-z_]+)?$'
) -join '|'

$projects = @(
    @{
        Repository = 'BetterEnd-26.3'
        Namespace = 'betterend'
        BaseTags = @('#wover:surfaces/end/stones')
        ExtraBlocks = @(
            'amaranita_cap',
            'amaranita_hymenophore',
            'amaranita_hyphae',
            'ancient_emerald_ice',
            'budding_smaragdant_crystal',
            'dense_emerald_ice',
            'dense_snow',
            'end_stone_brick_cracked',
            'end_stone_brick_weathered',
            'end_stone_stalactite',
            'end_stone_stalactite_cavemoss',
            'glowing_pillar_luminophor',
            'smaragdant_crystal',
            'terminite_tile',
            'thallasium_tile'
        )
    },
    @{
        Repository = 'BetterNether-26.3'
        Namespace = 'betternether'
        BaseTags = @('#wover:surfaces/nether/terrain')
        ExtraBlocks = @(
            'basalt_bricks'
        )
    }
)

function Get-LeafIds {
    param(
        [Parameter(Mandatory)] [string] $Repository,
        [Parameter(Mandatory)] [string] $Namespace
    )

    $result = [System.Collections.Generic.HashSet[string]]::new([System.StringComparer]::Ordinal)
    foreach ($sourceRoot in @('src/main/resources', 'src/main/generated')) {
        $path = Join-Path $Repository "$sourceRoot/data/minecraft/tags/block/leaves.json"
        if (-not (Test-Path -LiteralPath $path)) {
            continue
        }

        $tag = Get-Content -LiteralPath $path -Raw | ConvertFrom-Json
        foreach ($entry in $tag.values) {
            $id = if ($entry -is [string]) { $entry } else { $entry.id }
            if ($id -and $id.StartsWith("${Namespace}:", [System.StringComparison]::Ordinal)) {
                [void] $result.Add($id)
            }
        }
    }
    return $result
}

foreach ($project in $projects) {
    $repository = Join-Path $workspace $project.Repository
    $namespace = $project.Namespace
    $blockstateRoot = Join-Path $repository "src/main/generated/assets/$namespace/blockstates"
    if (-not (Test-Path -LiteralPath $blockstateRoot)) {
        throw "Blockstate resources not found: $blockstateRoot"
    }

    $knownBlocks = [System.Collections.Generic.HashSet[string]]::new([System.StringComparer]::Ordinal)
    Get-ChildItem -LiteralPath $blockstateRoot -File -Filter '*.json' | ForEach-Object {
        [void] $knownBlocks.Add($_.BaseName)
    }

    $leaves = Get-LeafIds -Repository $repository -Namespace $namespace
    $selected = [System.Collections.Generic.SortedSet[string]]::new([System.StringComparer]::Ordinal)

    foreach ($blockName in $knownBlocks) {
        if ($blockName -match $solidFamilyPattern) {
            [void] $selected.Add("${namespace}:$blockName")
        }
    }
    foreach ($blockName in $project.ExtraBlocks) {
        if (-not $knownBlocks.Contains($blockName)) {
            throw "Configured block does not exist: ${namespace}:$blockName"
        }
        [void] $selected.Add("${namespace}:$blockName")
    }

    foreach ($leaf in $leaves) {
        [void] $selected.Remove($leaf)
    }

    $values = [System.Collections.Generic.List[string]]::new()
    foreach ($tag in $project.BaseTags) {
        $values.Add($tag)
    }
    foreach ($id in $selected) {
        $values.Add($id)
    }

    $outputPath = Join-Path $repository 'src/main/resources/data/minecraft/tags/block/blocks_motion_no_leaves.json'
    $outputDirectory = Split-Path -Parent $outputPath
    [System.IO.Directory]::CreateDirectory($outputDirectory) | Out-Null

    $json = [ordered]@{
        replace = $false
        values = $values
    } | ConvertTo-Json -Depth 4
    [System.IO.File]::WriteAllText($outputPath, "$json`n", [System.Text.UTF8Encoding]::new($false))

    Write-Host "$($project.Repository): wrote $($values.Count) entries ($($leaves.Count) leaves excluded)"
}
