[CmdletBinding()]
param(
    [switch]$Publish,
    [switch]$Resume,
    [switch]$UpdateChangelogs,
    [string[]]$UpdateKeys = @(),
    [string[]]$PublishKeys = @(),
    [switch]$CurseForgeOnly,
    [ValidateSet('1.21.1','1.21.11','26.1','26.2','26.3')]
    [string]$MinecraftVersion = '1.21.1'
)

$ErrorActionPreference = 'Stop'
$workspace = Split-Path -Parent $PSScriptRoot
$stageRoot = Join-Path $workspace "release\$MinecraftVersion-publish"
$reportPath = Join-Path $stageRoot 'publish-report.json'
$userAgent = 'Raijin2312-BetterModsRelease/1.0'

function Get-Token([string]$name) {
    $value = [Environment]::GetEnvironmentVariable($name)
    if ([string]::IsNullOrWhiteSpace($value)) {
        $path = Join-Path $workspace $name
        if (Test-Path -LiteralPath $path) { $value = Get-Content -LiteralPath $path -Raw }
    }
    if ([string]::IsNullOrWhiteSpace($value)) { throw "Missing $name" }
    return $value.Trim()
}

function Invoke-MultipartPost(
    [string]$uri,
    [hashtable]$headers,
    [string]$jsonField,
    [string]$json,
    [System.IO.FileInfo]$file
) {
    Add-Type -AssemblyName System.Net.Http
    $handler = [System.Net.Http.HttpClientHandler]::new()
    $client = [System.Net.Http.HttpClient]::new($handler)
    $form = [System.Net.Http.MultipartFormDataContent]::new()
    $stream = $null
    try {
        $client.Timeout = [TimeSpan]::FromMinutes(20)
        $client.DefaultRequestHeaders.UserAgent.ParseAdd($userAgent)
        foreach ($entry in $headers.GetEnumerator()) {
            $null = $client.DefaultRequestHeaders.TryAddWithoutValidation($entry.Key, [string]$entry.Value)
        }
        $jsonContent = [System.Net.Http.StringContent]::new($json, [Text.Encoding]::UTF8, 'application/json')
        $form.Add($jsonContent, $jsonField)
        $stream = [System.IO.File]::OpenRead($file.FullName)
        $fileContent = [System.Net.Http.StreamContent]::new($stream)
        $fileContent.Headers.ContentType = [System.Net.Http.Headers.MediaTypeHeaderValue]::Parse('application/java-archive')
        $form.Add($fileContent, 'file', $file.Name)
        $response = $client.PostAsync($uri, $form).GetAwaiter().GetResult()
        $body = $response.Content.ReadAsStringAsync().GetAwaiter().GetResult()
        if (-not $response.IsSuccessStatusCode) {
            throw "HTTP $([int]$response.StatusCode) $($response.ReasonPhrase): $body"
        }
        if ([string]::IsNullOrWhiteSpace($body)) { return $null }
        return $body | ConvertFrom-Json
    } finally {
        if ($stream) { $stream.Dispose() }
        $form.Dispose()
        $client.Dispose()
        $handler.Dispose()
    }
}

function Invoke-MultipartMetadataPost([string]$uri, [hashtable]$headers, [string]$json) {
    Add-Type -AssemblyName System.Net.Http
    $handler = [System.Net.Http.HttpClientHandler]::new()
    $client = [System.Net.Http.HttpClient]::new($handler)
    $form = [System.Net.Http.MultipartFormDataContent]::new()
    try {
        $client.DefaultRequestHeaders.UserAgent.ParseAdd($userAgent)
        foreach ($entry in $headers.GetEnumerator()) {
            $null = $client.DefaultRequestHeaders.TryAddWithoutValidation($entry.Key, [string]$entry.Value)
        }
        $form.Add([System.Net.Http.StringContent]::new($json, [Text.Encoding]::UTF8, 'application/json'), 'metadata')
        $response = $client.PostAsync($uri, $form).GetAwaiter().GetResult()
        $body = $response.Content.ReadAsStringAsync().GetAwaiter().GetResult()
        if (-not $response.IsSuccessStatusCode) {
            throw "HTTP $([int]$response.StatusCode) $($response.ReasonPhrase): $body"
        }
    } finally {
        $form.Dispose()
        $client.Dispose()
        $handler.Dispose()
    }
}

function Invoke-JsonPatch([string]$uri, [hashtable]$headers, [string]$json) {
    Add-Type -AssemblyName System.Net.Http
    $handler = [System.Net.Http.HttpClientHandler]::new()
    $client = [System.Net.Http.HttpClient]::new($handler)
    try {
        $client.DefaultRequestHeaders.UserAgent.ParseAdd($userAgent)
        foreach ($entry in $headers.GetEnumerator()) {
            $null = $client.DefaultRequestHeaders.TryAddWithoutValidation($entry.Key, [string]$entry.Value)
        }
        $request = [System.Net.Http.HttpRequestMessage]::new([System.Net.Http.HttpMethod]::new('PATCH'), $uri)
        $request.Content = [System.Net.Http.StringContent]::new($json, [Text.Encoding]::UTF8, 'application/json')
        $response = $client.SendAsync($request).GetAwaiter().GetResult()
        $body = $response.Content.ReadAsStringAsync().GetAwaiter().GetResult()
        if (-not $response.IsSuccessStatusCode) {
            throw "HTTP $([int]$response.StatusCode) $($response.ReasonPhrase): $body"
        }
    } finally {
        if ($request) { $request.Dispose() }
        $client.Dispose()
        $handler.Dispose()
    }
}

function Get-CurseForgeVersionIds([string[]]$names, [string]$token) {
    $headers = @{ 'X-Api-Token' = $token; 'User-Agent' = $userAgent }
    $types = @(Invoke-RestMethod 'https://minecraft.curseforge.com/api/game/version-types' -Headers $headers)
    $allowed = @($types | Where-Object { $_.slug -like 'minecraft*' -or $_.slug -eq 'java' -or $_.slug -eq 'modloader' } | ForEach-Object { [int]$_.id })
    $versions = @(Invoke-RestMethod 'https://minecraft.curseforge.com/api/game/versions' -Headers $headers)
    $result = foreach ($name in $names) {
        $match = $versions | Where-Object { $_.name -eq $name -and [int]$_.gameVersionTypeID -in $allowed } | Select-Object -First 1
        if (-not $match) { throw "CurseForge does not recognize '$name'" }
        [int]$match.id
    }
    return @($result | Select-Object -Unique)
}

function Test-ModrinthVersion([string]$projectId, [string]$versionNumber) {
    $response = Invoke-RestMethod "https://api.modrinth.com/v2/project/$projectId/version" -Headers @{ 'User-Agent' = $userAgent }
    $versions = @($response.GetEnumerator())
    return $null -ne ($versions | Where-Object { $_.version_number -eq $versionNumber } | Select-Object -First 1)
}

$endChangelog = @'
- Limited Eternal Portal exit searches to prevent excessive synchronous chunk generation and server freezes
- Fixed Eternal Ritual crashes when a key is placed without a central pedestal
- Validated portal color IDs to prevent client crashes from corrupted data or Debug Stick changes
- Fixed concurrent wall and ceiling plant generation crashes
- Fixed the Respawn Obelisk sound position
- Fixed mountains, lakes and Painted Mountains being cut off after world reloads
- Kept Crashed Ships and End Lotus Leaves inside their permitted world generation area
- Fixed End paths reverting to dirt instead of their original End terrain
- Restored Umbrella Tree Cluster juice regeneration
- Fixed valid soils for Lacugrove and Mossy Glowshroom saplings
- Fixed sulphur crystal drops at young and fully grown stages
- Added normal furnace recipes for End Fish, End Berries, Chorus Mushrooms and Bolux Mushrooms
- Fixed waterlogged state handling for crystals, hydrothermal vents and sulphur cave features
- Restored missing vanilla and common tags for Thallasium and Terminite bars and chains
- Made generated biome identifiers independent of the system locale
- Added a config option for the guidebook reward when entering an End Gateway
'@

$netherChangelog = @'
- Added missing loot tables for doors, Transition Planks, Moss Cover, Soul Lily Saplings and Whispering Gourd Lanterns
- Fixed vanilla and common tags for logs, soils, flower pots, buttons and blocks washed away by liquids
- Fixed plant survival checks on Netherrack, Nylium and Sculk
- Fixed BetterNether sapling growth and bone meal behavior
- Fixed Statue Respawner behavior
- Fixed Eye Vine updates
- Restored natural Rubeus leaf decay
- Fixed duplicate drops from Black Vine, Blooming Vine and Gloomsculk Vine
- Fixed Feather Fern models
- Prevented Nether Cities from cutting through fortresses and bastions
- Prevented trees, vegetation, caves and forest debris from generating over city pieces
- Prevented city placement near bastions that could intersect it
'@

$worldWeaverChangelog = @'
- Fixed world generator preset serialization by using the active registry context
- Fixed the layer order of custom flat world presets
- Added recipe unlock advancements for recipes registered at runtime
'@

$bclibChangelog = @'
- Added configurable placement offsets to shared walkable plant properties
- Fixed the texture mapping used by custom chain block models
'@

$wunderLibChangelog = @'
- Updated the shared library metadata for the current BetterX upstream backports
'@

$artifacts = @(
    @{ Key='wunder-neo'; Project='WunderLib'; Modrinth='HZmhgdJk'; CurseForge=1422273; CurseSlug='wunderlib-neoforge'; Version='21.0.11'; Loader='NeoForge'; Source='WunderLib-1.21.1-Neoforge\build\libs\wunderlib-21.0.11.jar'; File='wunderlib-21.0.11.jar'; Prefix='wunderlib'; Dependencies=@(); Changelog=$wunderLibChangelog },
    @{ Key='wunder-fabric'; Project='WunderLib'; Modrinth='HZmhgdJk'; CurseForge=1422273; CurseSlug='wunderlib-neoforge'; Version='21.0.10-fabric'; Loader='Fabric'; Source='WunderLib-Fabric-1.21.1\build\libs\wunderlib-21.0.10.jar'; File='wunderlib-21.0.10-fabric.jar'; Prefix='wunderlib'; Dependencies=@(); Changelog=$wunderLibChangelog },
    @{ Key='wover-neo'; Project='WorldWeaver'; Modrinth='R8uGDQpB'; CurseForge=1422284; CurseSlug='worldweaver-neoforge'; Version='21.0.27'; Loader='NeoForge'; Source='WorldWeaver-1.21.1-Neoforge\build\libs\worldweaver-21.0.27.jar'; File='worldweaver-21.0.27.jar'; Prefix='worldweaver'; Dependencies=@('BCLib','WunderLib'); Changelog=$worldWeaverChangelog },
    @{ Key='wover-fabric'; Project='WorldWeaver'; Modrinth='R8uGDQpB'; CurseForge=1422284; CurseSlug='worldweaver-neoforge'; Version='21.0.20-fabric'; Loader='Fabric'; Source='WorldWeaver-1.21.1-Fabric\build\libs\worldweaver-21.0.20.jar'; File='worldweaver-21.0.20-fabric.jar'; Prefix='worldweaver'; Dependencies=@('BCLib','WunderLib'); Changelog=$worldWeaverChangelog },
    @{ Key='bclib-neo'; Project='BCLib'; Modrinth='7bdKEtKC'; CurseForge=1422283; CurseSlug='bclib-neoforge'; Version='21.0.27'; Loader='NeoForge'; Source='BCLib-1.21.1-Neoforge\build\libs\bclib-21.0.27.jar'; File='bclib-21.0.27.jar'; Prefix='bclib'; Dependencies=@('WorldWeaver','WunderLib'); Changelog=$bclibChangelog },
    @{ Key='bclib-fabric'; Project='BCLib'; Modrinth='7bdKEtKC'; CurseForge=1422283; CurseSlug='bclib-neoforge'; Version='21.0.20-fabric'; Loader='Fabric'; Source='BCLib-1.21.1-Fabric\build\libs\bclib-21.0.20.jar'; File='bclib-21.0.20-fabric.jar'; Prefix='bclib'; Dependencies=@('WorldWeaver','WunderLib'); Changelog=$bclibChangelog },
    @{ Key='end-neo'; Project='BetterEnd'; Modrinth='IcERKldh'; CurseForge=1422294; CurseSlug='betterend-neoforge'; Version='21.0.37'; Loader='NeoForge'; Source='BetterEnd\build\libs\BetterEnd-21.0.37.jar'; File='BetterEnd-21.0.37.jar'; Prefix='BetterEnd'; Dependencies=@('BCLib','WorldWeaver','WunderLib'); Changelog=$endChangelog },
    @{ Key='end-fabric'; Project='BetterEnd'; Modrinth='IcERKldh'; CurseForge=1422294; CurseSlug='betterend-neoforge'; Version='21.0.22-fabric'; Loader='Fabric'; Source='BetterEnd-Fabric-1.21.1\build\libs\better-end-21.0.22.jar'; File='better-end-21.0.22-fabric.jar'; Prefix='BetterEnd'; Dependencies=@('BCLib','WorldWeaver','WunderLib'); Changelog=$endChangelog },
    @{ Key='nether-neo'; Project='BetterNether'; Modrinth='fxX3RlL5'; CurseForge=1422293; CurseSlug='betternether-neoforge'; Version='21.0.28'; Loader='NeoForge'; Source='BetterNether\build\libs\BetterNether-21.0.28.jar'; File='BetterNether-21.0.28.jar'; Prefix='BetterNether'; Dependencies=@('BCLib','WorldWeaver','WunderLib'); Changelog=$netherChangelog },
    @{ Key='nether-fabric'; Project='BetterNether'; Modrinth='fxX3RlL5'; CurseForge=1422293; CurseSlug='betternether-neoforge'; Version='21.0.21-fabric'; Loader='Fabric'; Source='BetterNether-Fabric-1.21.1\build\libs\better-nether-21.0.21.jar'; File='better-nether-21.0.21-fabric.jar'; Prefix='BetterNether'; Dependencies=@('BCLib','WorldWeaver','WunderLib'); Changelog=$netherChangelog }
)

if ($MinecraftVersion -eq '1.21.11') {
    $endChangelog = @'
- Limited Eternal Portal exit searches to prevent excessive synchronous chunk generation and server freezes
- Fixed invalid portal IDs and Eternal Rituals without a central pedestal
- Fixed concurrent wall and ceiling plant generation crashes
- Fixed mountains, lakes, Crystal Mountains and Painted Mountains being cut off after world reloads
- Kept Crashed Ships and End Lotus Leaves inside their permitted world generation area
- Fixed End paths, Umbrella Tree Cluster regeneration and valid sapling soils
- Fixed sulphur crystal drops and normal furnace recipes for End food
- Fixed waterlogged state handling for crystals and related features
- Restored missing vanilla and common tags for Thallasium and Terminite bars and chains
- Made registry identifiers independent of the system locale
- Added a config option for the guidebook reward when entering an End Gateway
'@
    $netherChangelog = @'
- Added missing loot tables including Hook Mushroom
- Fixed vanilla and common tags for logs, buttons, soils, flower pots and blocks washed away by liquids
- Restored vanilla bone meal success rates for BetterNether saplings
- Fixed plant survival checks, Statue Respawner, Eye Vine and Whispering Gourd Vine
- Fixed Ruby Fire and firefly color packing
- Fixed Rubeus leaf decay and tree generation
- Fixed duplicate vine drops and Feather Fern models
- Prevented Nether Cities from intersecting fortresses, bastions, vegetation and caves
- Added Copper Golem support for modified wooden chests
- Fixed slim armor compatibility with 3D Skin Layers and other player rendering modifications
'@
    $worldWeaverChangelog = @'
- Fixed world generator preset serialization by using the active registry context
- Fixed the layer order of custom flat world presets
- Added recipe unlock advancements for recipes registered at runtime
- Fixed server-safe equipment and tool tier definitions used by backported content
- Added shared models for custom chiseled bookshelves
- Fixed generated block models and pottable registry initialization
'@
    $bclibChangelog = @'
- Added configurable placement offsets to shared walkable plant properties
- Fixed the texture mapping used by custom chain block models
- Added shared chiseled bookshelf and shelf support for custom wood sets
- Fixed late block entity registration used by custom signs and furniture
- Made feature saplings compostable
- Fixed vanilla shears loot matching
'@
    $bclibFabricChangelog = $bclibChangelog.Trim() + "`n- Fixed custom chest and boat rendering used by Fabric wood sets"
    $wunderLibChangelog = @'
- Fixed client-bound network payload handling on dedicated servers
'@
    $artifacts = @(
        @{ Key='wunder-neo'; Project='WunderLib'; Modrinth='HZmhgdJk'; CurseForge=1422273; CurseSlug='wunderlib-neoforge'; Version='21.11.3'; Loader='NeoForge'; Source='WunderLib-1.21.11\build\libs\wunderlib-21.11.3.jar'; File='wunderlib-21.11.3.jar'; Prefix='wunderlib'; Dependencies=@(); Changelog=$wunderLibChangelog },
        @{ Key='wunder-fabric'; Project='WunderLib'; Modrinth='HZmhgdJk'; CurseForge=1422273; CurseSlug='wunderlib-neoforge'; Version='21.11.3-fabric'; Loader='Fabric'; Source='WunderLib-Fabric-1.21.11\build\libs\wunderlib-21.11.3.jar'; File='wunderlib-21.11.3-fabric.jar'; Prefix='wunderlib'; Dependencies=@(); Changelog=$wunderLibChangelog },
        @{ Key='wover-neo'; Project='WorldWeaver'; Modrinth='R8uGDQpB'; CurseForge=1422284; CurseSlug='worldweaver-neoforge'; Version='21.11.8'; Loader='NeoForge'; Source='WorldWeaver-1.21.11\build\libs\worldweaver-21.11.8.jar'; File='worldweaver-21.11.8.jar'; Prefix='worldweaver'; Dependencies=@('BCLib','WunderLib'); Changelog=$worldWeaverChangelog },
        @{ Key='wover-fabric'; Project='WorldWeaver'; Modrinth='R8uGDQpB'; CurseForge=1422284; CurseSlug='worldweaver-neoforge'; Version='21.11.7-fabric'; Loader='Fabric'; Source='WorldWeaver-Fabric-1.21.11\build\libs\worldweaver-21.11.7.jar'; File='worldweaver-21.11.7-fabric.jar'; Prefix='worldweaver'; Dependencies=@('BCLib','WunderLib'); Changelog=$worldWeaverChangelog },
        @{ Key='bclib-neo'; Project='BCLib'; Modrinth='7bdKEtKC'; CurseForge=1422283; CurseSlug='bclib-neoforge'; Version='21.11.7'; Loader='NeoForge'; Source='BCLib-1.21.11\build\libs\bclib-21.11.7.jar'; File='bclib-21.11.7.jar'; Prefix='bclib'; Dependencies=@('WorldWeaver','WunderLib'); Changelog=$bclibChangelog },
        @{ Key='bclib-fabric'; Project='BCLib'; Modrinth='7bdKEtKC'; CurseForge=1422283; CurseSlug='bclib-neoforge'; Version='21.11.7-fabric'; Loader='Fabric'; Source='BCLib-Fabric-1.21.11\build\libs\bclib-21.11.7.jar'; File='bclib-21.11.7-fabric.jar'; Prefix='bclib'; Dependencies=@('WorldWeaver','WunderLib'); Changelog=$bclibFabricChangelog },
        @{ Key='end-neo'; Project='BetterEnd'; Modrinth='IcERKldh'; CurseForge=1422294; CurseSlug='betterend-neoforge'; Version='21.11.9'; Loader='NeoForge'; Source='BetterEnd-1.21.11\build\libs\BetterEnd-21.11.9.jar'; File='BetterEnd-21.11.9.jar'; Prefix='BetterEnd'; Dependencies=@('BCLib','WorldWeaver','WunderLib'); Changelog=$endChangelog },
        @{ Key='end-fabric'; Project='BetterEnd'; Modrinth='IcERKldh'; CurseForge=1422294; CurseSlug='betterend-neoforge'; Version='21.11.9-fabric'; Loader='Fabric'; Source='BetterEnd-Fabric-1.21.11\build\libs\better-end-21.11.9.jar'; File='better-end-21.11.9-fabric.jar'; Prefix='BetterEnd'; Dependencies=@('BCLib','WorldWeaver','WunderLib'); Changelog=$endChangelog },
        @{ Key='nether-neo'; Project='BetterNether'; Modrinth='fxX3RlL5'; CurseForge=1422293; CurseSlug='betternether-neoforge'; Version='21.11.8'; Loader='NeoForge'; Source='BetterNether-1.21.11\build\libs\BetterNether-21.11.8.jar'; File='BetterNether-21.11.8.jar'; Prefix='BetterNether'; Dependencies=@('BCLib','WorldWeaver','WunderLib'); Changelog=$netherChangelog },
        @{ Key='nether-fabric'; Project='BetterNether'; Modrinth='fxX3RlL5'; CurseForge=1422293; CurseSlug='betternether-neoforge'; Version='21.11.7-fabric'; Loader='Fabric'; Source='BetterNether-Fabric-1.21.11\build\libs\better-nether-21.11.7.jar'; File='better-nether-21.11.7-fabric.jar'; Prefix='BetterNether'; Dependencies=@('BCLib','WorldWeaver','WunderLib'); Changelog=$netherChangelog }
    )
}

if ($MinecraftVersion -eq '26.3') {
    $endChangelog = @'
- Limited Eternal Portal exit searches to prevent excessive synchronous chunk generation and server freezes
- Fixed invalid portal IDs and Eternal Rituals without a central pedestal
- Fixed concurrent wall and ceiling plant generation crashes
- Fixed mountains and lakes being cut off after world reloads
- Kept Crashed Ships and End Lotus Leaves inside their permitted world generation area
- Fixed End paths, Umbrella Tree Cluster regeneration and valid sapling soils
- Fixed sulphur crystal drops, normal furnace recipes and waterlogged feature states
- Restored missing vanilla and common tags for Thallasium and Terminite bars and chains
- Added flower pots to the vanilla flower pots tag and restored liquid wash-away behavior
- Made registry identifiers independent of the system locale
- Added a config option for the guidebook reward when entering an End Gateway
'@
    $netherChangelog = @'
- Added missing loot tables and corrected vanilla and common block tags
- Restored vanilla bone meal behavior and fixed plant survival checks
- Fixed Eye Vine, Whispering Gourd Vine, Statue Respawner and Ruby Fire behavior
- Fixed firefly color packing, Rubeus leaf decay and Rubeus tree generation
- Fixed duplicate vine drops and Feather Fern models
- Prevented Nether Cities from intersecting fortresses, bastions, caves, trees and vegetation
- Added Copper Golem support for modified wooden chests
- Fixed slim armor compatibility with 3D Skin Layers and vanilla player rendering
'@
    $worldWeaverChangelog = @'
- Fixed world generator preset serialization by using the active registry context
- Fixed the layer order of custom flat world presets
- Added recipe unlock advancements for recipes registered at runtime
- Fixed server-safe equipment and tool tier definitions used by backported content
- Added shared models for custom chiseled bookshelves
- Fixed generated block models and pottable registry initialization
'@
    $bclibChangelog = @'
- Restored bone meal growth for feature saplings
- Made feature saplings compostable
- Fixed vanilla shears loot matching
'@
    $wunderLibChangelog = @'
- Updated library metadata and composite build support for the current BetterX releases
'@
    $artifacts = @(
        @{ Key='wunder-neo'; Project='WunderLib'; Modrinth='HZmhgdJk'; CurseForge=1422273; CurseSlug='wunderlib-neoforge'; Version='26.3.1'; Loader='NeoForge'; Source='WunderLib-26.3\build\libs\wunderlib-26.3.1.jar'; File='wunderlib-26.3.1.jar'; Prefix='wunderlib'; Dependencies=@(); Changelog=$wunderLibChangelog },
        @{ Key='wunder-fabric'; Project='WunderLib'; Modrinth='HZmhgdJk'; CurseForge=1422273; CurseSlug='wunderlib-neoforge'; Version='26.3.1-fabric'; Loader='Fabric'; Source='WunderLib-Fabric-26.3\build\libs\wunderlib-26.3.1.jar'; File='wunderlib-26.3.1-fabric.jar'; Prefix='wunderlib'; Dependencies=@(); Changelog=$wunderLibChangelog },
        @{ Key='wover-neo'; Project='WorldWeaver'; Modrinth='R8uGDQpB'; CurseForge=1422284; CurseSlug='worldweaver-neoforge'; Version='26.3.4'; Loader='NeoForge'; Source='WorldWeaver-26.3\build\libs\worldweaver-26.3.4.jar'; File='worldweaver-26.3.4.jar'; Prefix='worldweaver'; Dependencies=@('BCLib','WunderLib'); Changelog=$worldWeaverChangelog },
        @{ Key='wover-fabric'; Project='WorldWeaver'; Modrinth='R8uGDQpB'; CurseForge=1422284; CurseSlug='worldweaver-neoforge'; Version='26.3.2-fabric'; Loader='Fabric'; Source='WorldWeaver-Fabric-26.3\build\libs\worldweaver-26.3.2.jar'; File='worldweaver-26.3.2-fabric.jar'; Prefix='worldweaver'; Dependencies=@('BCLib','WunderLib'); Changelog=$worldWeaverChangelog },
        @{ Key='bclib-neo'; Project='BCLib'; Modrinth='7bdKEtKC'; CurseForge=1422283; CurseSlug='bclib-neoforge'; Version='26.3.1'; Loader='NeoForge'; Source='BCLib-26.3\build\libs\bclib-26.3.1.jar'; File='bclib-26.3.1.jar'; Prefix='bclib'; Dependencies=@('WorldWeaver','WunderLib'); Changelog=$bclibChangelog },
        @{ Key='bclib-fabric'; Project='BCLib'; Modrinth='7bdKEtKC'; CurseForge=1422283; CurseSlug='bclib-neoforge'; Version='26.3.1-fabric'; Loader='Fabric'; Source='BCLib-Fabric-26.3\build\libs\bclib-26.3.1.jar'; File='bclib-26.3.1-fabric.jar'; Prefix='bclib'; Dependencies=@('WorldWeaver','WunderLib'); Changelog=$bclibChangelog },
        @{ Key='end-neo'; Project='BetterEnd'; Modrinth='IcERKldh'; CurseForge=1422294; CurseSlug='betterend-neoforge'; Version='26.3.6'; Loader='NeoForge'; Source='BetterEnd-26.3\build\libs\BetterEnd-26.3.6.jar'; File='BetterEnd-26.3.6.jar'; Prefix='BetterEnd'; Dependencies=@('BCLib','WorldWeaver','WunderLib'); Changelog=$endChangelog },
        @{ Key='end-fabric'; Project='BetterEnd'; Modrinth='IcERKldh'; CurseForge=1422294; CurseSlug='betterend-neoforge'; Version='26.3.6-fabric'; Loader='Fabric'; Source='BetterEnd-Fabric-26.3\build\libs\better-end-26.3.6.jar'; File='better-end-26.3.6-fabric.jar'; Prefix='BetterEnd'; Dependencies=@('BCLib','WorldWeaver','WunderLib'); Changelog=$endChangelog },
        @{ Key='nether-neo'; Project='BetterNether'; Modrinth='fxX3RlL5'; CurseForge=1422293; CurseSlug='betternether-neoforge'; Version='26.3.3'; Loader='NeoForge'; Source='BetterNether-26.3\build\libs\BetterNether-26.3.3.jar'; File='BetterNether-26.3.3.jar'; Prefix='BetterNether'; Dependencies=@('BCLib','WorldWeaver','WunderLib'); Changelog=$netherChangelog },
        @{ Key='nether-fabric'; Project='BetterNether'; Modrinth='fxX3RlL5'; CurseForge=1422293; CurseSlug='betternether-neoforge'; Version='26.3.3-fabric'; Loader='Fabric'; Source='BetterNether-Fabric-26.3\build\libs\better-nether-26.3.3.jar'; File='better-nether-26.3.3-fabric.jar'; Prefix='BetterNether'; Dependencies=@('BCLib','WorldWeaver','WunderLib'); Changelog=$netherChangelog }
    )
}

if ($MinecraftVersion -in @('26.1','26.2')) {
    $endChangelog = @'
- Limited Eternal Portal exit searches to prevent excessive synchronous chunk generation and server freezes
- Fixed invalid portal IDs and Eternal Rituals without a central pedestal
- Fixed concurrent wall and ceiling plant generation crashes
- Fixed mountains and lakes being cut off after world reloads
- Kept Crashed Ships and End Lotus Leaves inside their permitted world generation area
- Fixed End paths, Umbrella Tree Cluster regeneration and valid sapling soils
- Fixed sulphur crystal drops and normal furnace recipes for End food
- Fixed waterlogged state handling for crystals and related features
- Restored missing vanilla and common tags for Thallasium and Terminite bars and chains
- Made registry identifiers independent of the system locale
- Added a config option for the guidebook reward when entering an End Gateway
'@
    $netherChangelog = @'
- Added missing loot tables and corrected vanilla and common block tags
- Restored vanilla bone meal behavior and fixed plant survival checks
- Fixed Eye Vine, Whispering Gourd Vine, Statue Respawner and Ruby Fire behavior
- Fixed firefly color packing, Rubeus leaf decay and Rubeus tree generation
- Fixed duplicate vine drops and Feather Fern models
- Prevented Nether Cities from intersecting fortresses, bastions, caves, trees and vegetation
- Added Copper Golem support for modified wooden chests
- Fixed slim armor compatibility with 3D Skin Layers and vanilla player rendering
'@
    $worldWeaverChangelog = @'
- Fixed world generator preset serialization by using the active registry context
- Fixed the layer order of custom flat world presets
- Added recipe unlock advancements for recipes registered at runtime
- Fixed server-safe equipment and tool tier definitions used by backported content
- Added shared models for custom chiseled bookshelves
- Fixed generated block models and pottable registry initialization
'@
    $bclibChangelog = @'
- Restored bone meal growth for feature saplings
- Made feature saplings compostable
- Fixed vanilla shears loot matching
'@
    $wunderLibChangelog = @'
- Updated library metadata and composite build support for the current BetterX releases
'@
    if ($MinecraftVersion -eq '26.1') {
        $artifacts = @(
            @{ Key='wunder-neo'; Project='WunderLib'; Modrinth='HZmhgdJk'; CurseForge=1422273; CurseSlug='wunderlib-neoforge'; Version='26.1.3'; Loader='NeoForge'; Source='WunderLib-26.1\build\libs\wunderlib-26.1.3.jar'; File='wunderlib-26.1.3.jar'; Prefix='wunderlib'; Dependencies=@(); Changelog=$wunderLibChangelog },
            @{ Key='wunder-fabric'; Project='WunderLib'; Modrinth='HZmhgdJk'; CurseForge=1422273; CurseSlug='wunderlib-neoforge'; Version='26.1.3-fabric'; Loader='Fabric'; Source='WunderLib-Fabric-26.1\build\libs\wunderlib-26.1.3.jar'; File='wunderlib-26.1.3-fabric.jar'; Prefix='wunderlib'; Dependencies=@(); Changelog=$wunderLibChangelog },
            @{ Key='wover-neo'; Project='WorldWeaver'; Modrinth='R8uGDQpB'; CurseForge=1422284; CurseSlug='worldweaver-neoforge'; Version='26.1.10'; Loader='NeoForge'; Source='WorldWeaver-26.1\build\libs\worldweaver-26.1.10.jar'; File='worldweaver-26.1.10.jar'; Prefix='worldweaver'; Dependencies=@('BCLib','WunderLib'); Changelog=$worldWeaverChangelog },
            @{ Key='wover-fabric'; Project='WorldWeaver'; Modrinth='R8uGDQpB'; CurseForge=1422284; CurseSlug='worldweaver-neoforge'; Version='26.1.9-fabric'; Loader='Fabric'; Source='WorldWeaver-Fabric-26.1\build\libs\worldweaver-26.1.9.jar'; File='worldweaver-26.1.9-fabric.jar'; Prefix='worldweaver'; Dependencies=@('BCLib','WunderLib'); Changelog=$worldWeaverChangelog },
            @{ Key='bclib-neo'; Project='BCLib'; Modrinth='7bdKEtKC'; CurseForge=1422283; CurseSlug='bclib-neoforge'; Version='26.1.8'; Loader='NeoForge'; Source='BCLib-26.1\build\libs\bclib-26.1.8.jar'; File='bclib-26.1.8.jar'; Prefix='bclib'; Dependencies=@('WorldWeaver','WunderLib'); Changelog=$bclibChangelog },
            @{ Key='bclib-fabric'; Project='BCLib'; Modrinth='7bdKEtKC'; CurseForge=1422283; CurseSlug='bclib-neoforge'; Version='26.1.8-fabric'; Loader='Fabric'; Source='BCLib-Fabric-26.1\build\libs\bclib-26.1.8.jar'; File='bclib-26.1.8-fabric.jar'; Prefix='bclib'; Dependencies=@('WorldWeaver','WunderLib'); Changelog=$bclibChangelog },
            @{ Key='end-neo'; Project='BetterEnd'; Modrinth='IcERKldh'; CurseForge=1422294; CurseSlug='betterend-neoforge'; Version='26.1.10'; Loader='NeoForge'; Source='BetterEnd-26.1\build\libs\BetterEnd-26.1.10.jar'; File='BetterEnd-26.1.10.jar'; Prefix='BetterEnd'; Dependencies=@('BCLib','WorldWeaver','WunderLib'); Changelog=$endChangelog },
            @{ Key='end-fabric'; Project='BetterEnd'; Modrinth='IcERKldh'; CurseForge=1422294; CurseSlug='betterend-neoforge'; Version='26.1.10-fabric'; Loader='Fabric'; Source='BetterEnd-Fabric-26.1\build\libs\better-end-26.1.10.jar'; File='better-end-26.1.10-fabric.jar'; Prefix='BetterEnd'; Dependencies=@('BCLib','WorldWeaver','WunderLib'); Changelog=$endChangelog },
            @{ Key='nether-neo'; Project='BetterNether'; Modrinth='fxX3RlL5'; CurseForge=1422293; CurseSlug='betternether-neoforge'; Version='26.1.9'; Loader='NeoForge'; Source='BetterNether-26.1\build\libs\BetterNether-26.1.9.jar'; File='BetterNether-26.1.9.jar'; Prefix='BetterNether'; Dependencies=@('BCLib','WorldWeaver','WunderLib'); Changelog=$netherChangelog },
            @{ Key='nether-fabric'; Project='BetterNether'; Modrinth='fxX3RlL5'; CurseForge=1422293; CurseSlug='betternether-neoforge'; Version='26.1.8-fabric'; Loader='Fabric'; Source='BetterNether-Fabric-26.1\build\libs\better-nether-26.1.8.jar'; File='better-nether-26.1.8-fabric.jar'; Prefix='BetterNether'; Dependencies=@('BCLib','WorldWeaver','WunderLib'); Changelog=$netherChangelog }
        )
    } else {
        $artifacts = @(
            @{ Key='wunder-neo'; Project='WunderLib'; Modrinth='HZmhgdJk'; CurseForge=1422273; CurseSlug='wunderlib-neoforge'; Version='26.2.1'; Loader='NeoForge'; Source='WunderLib-26.2\build\libs\wunderlib-26.2.1.jar'; File='wunderlib-26.2.1.jar'; Prefix='wunderlib'; Dependencies=@(); Changelog=$wunderLibChangelog },
            @{ Key='wunder-fabric'; Project='WunderLib'; Modrinth='HZmhgdJk'; CurseForge=1422273; CurseSlug='wunderlib-neoforge'; Version='26.2.1-fabric'; Loader='Fabric'; Source='WunderLib-Fabric-26.2\build\libs\wunderlib-26.2.1.jar'; File='wunderlib-26.2.1-fabric.jar'; Prefix='wunderlib'; Dependencies=@(); Changelog=$wunderLibChangelog },
            @{ Key='wover-neo'; Project='WorldWeaver'; Modrinth='R8uGDQpB'; CurseForge=1422284; CurseSlug='worldweaver-neoforge'; Version='26.2.5'; Loader='NeoForge'; Source='WorldWeaver-26.2\build\libs\worldweaver-26.2.5.jar'; File='worldweaver-26.2.5.jar'; Prefix='worldweaver'; Dependencies=@('BCLib','WunderLib'); Changelog=$worldWeaverChangelog },
            @{ Key='wover-fabric'; Project='WorldWeaver'; Modrinth='R8uGDQpB'; CurseForge=1422284; CurseSlug='worldweaver-neoforge'; Version='26.2.4-fabric'; Loader='Fabric'; Source='WorldWeaver-Fabric-26.2\build\libs\worldweaver-26.2.4.jar'; File='worldweaver-26.2.4-fabric.jar'; Prefix='worldweaver'; Dependencies=@('BCLib','WunderLib'); Changelog=$worldWeaverChangelog },
            @{ Key='bclib-neo'; Project='BCLib'; Modrinth='7bdKEtKC'; CurseForge=1422283; CurseSlug='bclib-neoforge'; Version='26.2.4'; Loader='NeoForge'; Source='BCLib-26.2\build\libs\bclib-26.2.4.jar'; File='bclib-26.2.4.jar'; Prefix='bclib'; Dependencies=@('WorldWeaver','WunderLib'); Changelog=$bclibChangelog },
            @{ Key='bclib-fabric'; Project='BCLib'; Modrinth='7bdKEtKC'; CurseForge=1422283; CurseSlug='bclib-neoforge'; Version='26.2.3-fabric'; Loader='Fabric'; Source='BCLib-Fabric-26.2\build\libs\bclib-26.2.3.jar'; File='bclib-26.2.3-fabric.jar'; Prefix='bclib'; Dependencies=@('WorldWeaver','WunderLib'); Changelog=$bclibChangelog },
            @{ Key='end-neo'; Project='BetterEnd'; Modrinth='IcERKldh'; CurseForge=1422294; CurseSlug='betterend-neoforge'; Version='26.2.6'; Loader='NeoForge'; Source='BetterEnd-26.2\build\libs\BetterEnd-26.2.6.jar'; File='BetterEnd-26.2.6.jar'; Prefix='BetterEnd'; Dependencies=@('BCLib','WorldWeaver','WunderLib'); Changelog=$endChangelog },
            @{ Key='end-fabric'; Project='BetterEnd'; Modrinth='IcERKldh'; CurseForge=1422294; CurseSlug='betterend-neoforge'; Version='26.2.6-fabric'; Loader='Fabric'; Source='BetterEnd-Fabric-26.2\build\libs\better-end-26.2.6.jar'; File='better-end-26.2.6-fabric.jar'; Prefix='BetterEnd'; Dependencies=@('BCLib','WorldWeaver','WunderLib'); Changelog=$endChangelog },
            @{ Key='nether-neo'; Project='BetterNether'; Modrinth='fxX3RlL5'; CurseForge=1422293; CurseSlug='betternether-neoforge'; Version='26.2.3'; Loader='NeoForge'; Source='BetterNether-26.2\build\libs\BetterNether-26.2.3.jar'; File='BetterNether-26.2.3.jar'; Prefix='BetterNether'; Dependencies=@('BCLib','WorldWeaver','WunderLib'); Changelog=$netherChangelog },
            @{ Key='nether-fabric'; Project='BetterNether'; Modrinth='fxX3RlL5'; CurseForge=1422293; CurseSlug='betternether-neoforge'; Version='26.2.3-fabric'; Loader='Fabric'; Source='BetterNether-Fabric-26.2\build\libs\better-nether-26.2.3.jar'; File='better-nether-26.2.3-fabric.jar'; Prefix='BetterNether'; Dependencies=@('BCLib','WorldWeaver','WunderLib'); Changelog=$netherChangelog }
        )
    }
}

if ($PublishKeys.Count -gt 0) {
    $artifacts = @($artifacts | Where-Object { $_.Key -in $PublishKeys })
    if ($artifacts.Count -eq 0) { throw 'No publication artifacts selected' }
}

$dependencyProjects = @{
    WunderLib = @{ Modrinth='HZmhgdJk'; CurseForge='wunderlib-neoforge' }
    WorldWeaver = @{ Modrinth='R8uGDQpB'; CurseForge='worldweaver-neoforge' }
    BCLib = @{ Modrinth='7bdKEtKC'; CurseForge='bclib-neoforge' }
}

if ($UpdateChangelogs) {
    if (-not (Test-Path -LiteralPath $reportPath)) { throw "Missing publication report: $reportPath" }
    $published = @(Get-Content -LiteralPath $reportPath -Raw | ConvertFrom-Json)
    $modrinthToken = Get-Token 'MODRINTH_TOKEN'
    $curseForgeToken = Get-Token 'CURSEFORGE_TOKEN'
    $selectedArtifacts = if ($UpdateKeys.Count -gt 0) {
        @($artifacts | Where-Object { $_.Key -in $UpdateKeys })
    } else {
        @($artifacts)
    }
    if ($selectedArtifacts.Count -eq 0) { throw 'No changelog update artifacts selected' }
    if (-not $CurseForgeOnly) {
        foreach ($artifact in $selectedArtifacts) {
            $response = Invoke-RestMethod "https://api.modrinth.com/v2/project/$($artifact.Modrinth)/version" -Headers @{ 'User-Agent'=$userAgent }
            $versions = @($response.GetEnumerator())
            $version = $versions | Where-Object { $_.version_number -eq $artifact.Version } | Select-Object -First 1
            if (-not $version) { throw "Missing Modrinth version $($artifact.Version)" }
            $body = @{ changelog=$artifact.Changelog.Trim() } | ConvertTo-Json -Compress
            Invoke-JsonPatch "https://api.modrinth.com/v2/version/$($version.id)" @{ Authorization=$modrinthToken } $body
            Write-Host "[$($artifact.Project) $($artifact.Version)] Modrinth changelog updated" -ForegroundColor Green
        }
    }

    $curseFailures = [System.Collections.Generic.List[string]]::new()
    foreach ($artifact in $selectedArtifacts) {
        $row = $published | Where-Object { $_.Key -eq $artifact.Key } | Select-Object -First 1
        if (-not $row) { throw "No publication record for $($artifact.Key)" }
        $fileId = [regex]::Match([string]$row.CurseForge, '/files/(\d+)$').Groups[1].Value
        if (-not $fileId) { throw "Missing CurseForge file ID for $($artifact.Key)" }
        $metadata = @{ fileID=[int64]$fileId; changelog=$artifact.Changelog.Trim(); changelogType='markdown' } | ConvertTo-Json -Compress
        try {
            Invoke-MultipartMetadataPost "https://minecraft.curseforge.com/api/projects/$($artifact.CurseForge)/update-file" @{ 'X-Api-Token'=$curseForgeToken } $metadata
            Write-Host "[$($artifact.Project) $($artifact.Version)] CurseForge changelog updated" -ForegroundColor Green
        } catch {
            $curseFailures.Add("$($artifact.Key): $($_.Exception.Message)")
            Write-Host "[$($artifact.Project) $($artifact.Version)] CurseForge update pending" -ForegroundColor Yellow
        }
    }
    if ($curseFailures.Count -gt 0) { throw "CurseForge updates failed: $($curseFailures -join '; ')" }
    exit 0
}

$null = New-Item -ItemType Directory -Path $stageRoot -Force
$priorRows = @()
if ($Resume -and (Test-Path -LiteralPath $reportPath)) {
    $parsedPriorRows = Get-Content -LiteralPath $reportPath -Raw | ConvertFrom-Json
    $priorRows = @($parsedPriorRows | ForEach-Object { $_ })
}
$rows = [System.Collections.Generic.List[object]]::new()
foreach ($artifact in $artifacts) {
    $source = Join-Path $workspace $artifact.Source
    if (-not (Test-Path -LiteralPath $source -PathType Leaf)) { throw "Missing artifact: $source" }
    $target = Join-Path $stageRoot $artifact.File
    Copy-Item -LiteralPath $source -Destination $target -Force
    $existsOnModrinth = Test-ModrinthVersion $artifact.Modrinth $artifact.Version
    $prior = $priorRows | Where-Object { $_.Key -eq $artifact.Key } | Select-Object -First 1
    if ($existsOnModrinth -and -not $Resume) { throw "Modrinth version already exists: $($artifact.Project) $($artifact.Version)" }
    $rows.Add([pscustomobject]@{
        Key=$artifact.Key; Project=$artifact.Project; Loader=$artifact.Loader; Version=$artifact.Version
        File=$target; Bytes=(Get-Item -LiteralPath $target).Length; SHA512=(Get-FileHash -LiteralPath $target -Algorithm SHA512).Hash.ToLower()
        ExistsOnModrinth=$existsOnModrinth; Modrinth=$prior.Modrinth; CurseForge=$prior.CurseForge; CurseForgeManualRelease=$true
        Status=if ($prior.Status) { $prior.Status } else { 'validated' }; Error=$null
    })
}
$rows | ConvertTo-Json -Depth 6 | Set-Content -LiteralPath $reportPath -Encoding UTF8
$rows | Select-Object Project,Loader,Version,@{N='File';E={[IO.Path]::GetFileName($_.File)}},Bytes | Format-Table -AutoSize

if (-not $Publish) {
    Write-Host "Validated 10 unique release files; nothing uploaded" -ForegroundColor Yellow
    Write-Host "Report: $reportPath" -ForegroundColor Cyan
    exit 0
}

$modrinthToken = Get-Token 'MODRINTH_TOKEN'
$curseForgeToken = Get-Token 'CURSEFORGE_TOKEN'
# Exact IDs from CurseForge's authenticated game-version catalog
$curseIds = if ($MinecraftVersion -eq '26.1') {
    @{ NeoForge=@(15933,16021,16082,14454,10150,9638,9639); Fabric=@(15933,16021,16082,14454,7499,9153,9638,9639) }
} elseif ($MinecraftVersion -eq '26.2') {
    @{ NeoForge=@(16498,14454,10150,9638,9639); Fabric=@(16498,14454,7499,9153,9638,9639) }
} elseif ($MinecraftVersion -eq '26.3') {
    @{ NeoForge=@(17045,14454,10150,9638,9639); Fabric=@(17045,14454,7499,9153,9638,9639) }
} elseif ($MinecraftVersion -eq '1.21.11') {
    @{ NeoForge=@(14406,11135,10150,9638,9639); Fabric=@(14406,11135,7499,9153,9638,9639) }
} else {
    @{ NeoForge=@(11457,11779,11135,10150,9638,9639); Fabric=@(11457,11779,11135,7499,9153,9638,9639) }
}
$modrinthGameVersions = [string[]]@(
    if ($MinecraftVersion -eq '26.1') { '26.1'; '26.1.1'; '26.1.2' } elseif ($MinecraftVersion -eq '26.2') { '26.2' } elseif ($MinecraftVersion -eq '26.3') { '26.3' } elseif ($MinecraftVersion -eq '1.21.11') { '1.21.11' } else { '1.21'; '1.21.1' }
)

for ($index = 0; $index -lt $artifacts.Count; $index++) {
    $artifact = $artifacts[$index]
    $row = $rows[$index]
    try {
        [object[]]$modrinthDependencies = @($artifact.Dependencies | ForEach-Object {
            @{ project_id=$dependencyProjects[$_].Modrinth; dependency_type='required' }
        })
        [string[]]$loaders = if ($artifact.Loader -eq 'Fabric') { 'fabric','quilt' } else { ,'neoforge' }
        if ($row.ExistsOnModrinth -and $Resume) {
            $row.Modrinth = "https://modrinth.com/mod/$($artifact.Modrinth)/versions"
            Write-Host "[$($artifact.Project) $($artifact.Version)] Modrinth already published; resuming" -ForegroundColor DarkYellow
        } else {
            $modrinthMetadata = @{
                name=$artifact.Version; version_number=$artifact.Version; changelog=$artifact.Changelog.Trim()
                dependencies=[object[]]$modrinthDependencies; game_versions=$modrinthGameVersions; version_type='release'
                loaders=[string[]]$loaders; featured=$true; status='listed'; project_id=$artifact.Modrinth
                file_parts=@('file'); primary_file='file'; environment='client_and_server'
            } | ConvertTo-Json -Depth 10 -Compress
            $modrinthResponse = Invoke-MultipartPost 'https://api.modrinth.com/v2/version' @{ Authorization=$modrinthToken } 'data' $modrinthMetadata (Get-Item $row.File)
            $row.Modrinth = "https://modrinth.com/mod/$($artifact.Modrinth)/version/$($modrinthResponse.id)"
            Write-Host "[$($artifact.Project) $($artifact.Version)] Modrinth published" -ForegroundColor Green
        }
        $rows | ConvertTo-Json -Depth 6 | Set-Content -LiteralPath $reportPath -Encoding UTF8

        if ($Resume -and $row.CurseForge) {
            Write-Host "[$($artifact.Project) $($artifact.Version)] CurseForge already submitted; resuming" -ForegroundColor DarkYellow
            continue
        }
        [object[]]$curseRelations = @($artifact.Dependencies | ForEach-Object {
            @{ slug=$dependencyProjects[$_].CurseForge; type='requiredDependency' }
        })
        $curseMetadataObject = @{
            changelogType='markdown'; changelog=$artifact.Changelog.Trim()
            displayName="$($artifact.Prefix)-$($artifact.Version) [$MinecraftVersion]"
            releaseType='release'; gameVersions=[int[]]@($curseIds[$artifact.Loader])
            isMarkedForManualRelease=$true
        }
        if ($curseRelations.Count -gt 0) {
            $curseMetadataObject.relations = @{ projects=[object[]]$curseRelations }
        }
        $curseMetadata = $curseMetadataObject | ConvertTo-Json -Depth 10 -Compress
        $curseUri = "https://minecraft.curseforge.com/api/projects/$($artifact.CurseForge)/upload-file"
        $curseResponse = Invoke-MultipartPost $curseUri @{ 'X-Api-Token'=$curseForgeToken } 'metadata' $curseMetadata (Get-Item $row.File)
        $row.CurseForge = "https://www.curseforge.com/minecraft/mc-mods/$($artifact.CurseSlug)/files/$($curseResponse.id)"
        $row.Status = 'submitted'
        $rows | ConvertTo-Json -Depth 6 | Set-Content -LiteralPath $reportPath -Encoding UTF8
        Write-Host "[$($artifact.Project) $($artifact.Version)] CurseForge submitted for manual release" -ForegroundColor Green
    } catch {
        $row.Status = 'failed'
        $row.Error = $_.Exception.Message
        $rows | ConvertTo-Json -Depth 6 | Set-Content -LiteralPath $reportPath -Encoding UTF8
        throw
    }
}

Write-Host "Published to Modrinth and submitted to CurseForge in dependency order" -ForegroundColor Green
Write-Host "Report: $reportPath" -ForegroundColor Cyan
