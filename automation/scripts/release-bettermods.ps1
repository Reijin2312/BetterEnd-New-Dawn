[CmdletBinding()]
param(
    [ValidateSet('BetterEnd', 'BetterNether')]
    [string[]]$Mods = @('BetterEnd', 'BetterNether'),

    [ValidateSet('1.21.1', '1.21.11', '26.1', '26.2', '26.3')]
    [string[]]$Versions = @('1.21.1', '1.21.11', '26.1', '26.2', '26.3'),

    [ValidateSet('Fabric', 'NeoForge')]
    [string[]]$Loaders = @('NeoForge', 'Fabric'),

    [ValidateSet('Both', 'Modrinth', 'CurseForge')]
    [string]$Platform = 'Both',

    [ValidateSet('Release', 'Beta', 'Alpha')]
    [string]$ReleaseType = 'Release',

    [string]$Manifest = '',
    [string]$ChangelogDirectory = '',
    [switch]$Build,
    [switch]$OnlineBuild,
    [switch]$Publish,
    [switch]$Yes,
    [switch]$SkipExisting
)

$ErrorActionPreference = 'Stop'
$workspace = Split-Path -Parent $PSScriptRoot
if (-not $Manifest) { $Manifest = Join-Path $workspace 'release\bettermods-release.json' }
if (-not $ChangelogDirectory) { $ChangelogDirectory = Join-Path $workspace 'release\changelogs' }
$outDirectory = Join-Path $workspace 'release\out'
$null = New-Item -ItemType Directory -Path $outDirectory -Force
$stamp = Get-Date -Format 'yyyyMMdd-HHmmss'
$reportPath = Join-Path $outDirectory "release-$stamp.json"
$config = Get-Content -LiteralPath $Manifest -Raw -Encoding UTF8 | ConvertFrom-Json

function Get-PropertyValue([string]$path, [string]$name) {
    $line = Select-String -LiteralPath $path -Pattern "^$([regex]::Escape($name))\s*=" | Select-Object -First 1
    if (-not $line) { throw "Property '$name' is missing from $path" }
    return ($line.Line -split '=', 2)[1].Trim()
}

function Get-ProjectDirectory([string]$mod, [string]$version, [string]$loader) {
    if ($version -eq '1.21.1' -and $loader -eq 'NeoForge') { return Join-Path $workspace $mod }
    if ($loader -eq 'Fabric') { return Join-Path $workspace "$mod-Fabric-$version" }
    return Join-Path $workspace "$mod-$version"
}

function Get-Changelog([string]$mod, [string]$version) {
    $specific = Join-Path (Join-Path $ChangelogDirectory $mod) "$version.md"
    $shared = Join-Path $ChangelogDirectory "$($mod.ToLowerInvariant()).md"
    $path = if (Test-Path -LiteralPath $specific) { $specific } else { $shared }
    if (-not (Test-Path -LiteralPath $path)) { throw "Changelog is missing: $specific or $shared" }
    $text = Get-Content -LiteralPath $path -Raw -Encoding UTF8
    if ([string]::IsNullOrWhiteSpace($text)) { throw "Changelog is empty: $path" }
    return @{ Path = $path; Text = $text.Trim() }
}

function Get-ReleaseJar([string]$projectDirectory, [string]$version) {
    $libDirectory = Join-Path $projectDirectory 'build\libs'
    $jars = @(Get-ChildItem -LiteralPath $libDirectory -File -Filter '*.jar' -ErrorAction SilentlyContinue |
        Where-Object {
            $_.Name -notmatch '(?i)(sources|javadoc|dev|shadow|plain)' -and
            $_.BaseName -match "(^|[-_])$([regex]::Escape($version))($|[-_])"
        } |
        Sort-Object LastWriteTimeUtc -Descending)
    if ($jars.Count -eq 0) { throw "No production JAR for version $version in $libDirectory. Run with -Build." }
    if ($jars.Count -gt 1 -and $jars[0].LastWriteTimeUtc -eq $jars[1].LastWriteTimeUtc) {
        throw "Ambiguous production JARs in $libDirectory"
    }
    $inputs = @()
    foreach ($path in @('src\main', 'build.gradle', 'gradle.properties', 'settings.gradle')) {
        $candidate = Join-Path $projectDirectory $path
        if (Test-Path -LiteralPath $candidate -PathType Container) {
            $inputs += Get-ChildItem -LiteralPath $candidate -File -Recurse
        } elseif (Test-Path -LiteralPath $candidate -PathType Leaf) {
            $inputs += Get-Item -LiteralPath $candidate
        }
    }
    $newestInput = $inputs | Sort-Object LastWriteTimeUtc -Descending | Select-Object -First 1
    if ($newestInput -and $newestInput.LastWriteTimeUtc -gt $jars[0].LastWriteTimeUtc) {
        throw "Production JAR is older than $($newestInput.FullName). Run with -Build."
    }
    return $jars[0]
}

function Get-Token([string]$environmentName) {
    $value = [Environment]::GetEnvironmentVariable($environmentName)
    if (-not [string]::IsNullOrWhiteSpace($value)) { return $value.Trim() }
    $tokenFile = Join-Path $workspace $environmentName
    if (Test-Path -LiteralPath $tokenFile) { return (Get-Content -LiteralPath $tokenFile -Raw).Trim() }
    throw "$environmentName is not set and $tokenFile does not exist"
}

function Invoke-JsonGet([string]$uri, [hashtable]$headers) {
    return Invoke-RestMethod -Method Get -Uri $uri -Headers $headers -UseBasicParsing
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
        $client.DefaultRequestHeaders.UserAgent.ParseAdd('Raijin2312-BetterModsRelease/1.0')
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

function Get-CurseForgeVersionIds([string[]]$names, [string]$token) {
    $headers = @{ 'X-Api-Token' = $token; 'User-Agent' = 'Raijin2312-BetterModsRelease/1.0' }
    $types = @(Invoke-JsonGet 'https://minecraft.curseforge.com/api/game/version-types' $headers)
    $validTypeIds = @($types | Where-Object {
        $_.slug -like 'minecraft*' -or $_.slug -eq 'java' -or $_.slug -eq 'modloader'
    } | ForEach-Object { [int]$_.id })
    $available = @(Invoke-JsonGet 'https://minecraft.curseforge.com/api/game/versions' $headers)
    $ids = [System.Collections.Generic.List[int]]::new()
    foreach ($name in $names) {
        $match = $available | Where-Object {
            $_.name -eq $name -and [int]$_.gameVersionTypeID -in $validTypeIds
        } | Select-Object -First 1
        if (-not $match) { throw "CurseForge does not recognize game/loader version '$name'" }
        $ids.Add([int]$match.id)
    }
    return @($ids | Select-Object -Unique)
}

function Test-ModrinthVersionExists([string]$projectId, [string]$versionNumber) {
    $headers = @{ 'User-Agent' = 'Raijin2312-BetterModsRelease/1.0' }
    $versions = @(Invoke-JsonGet "https://api.modrinth.com/v2/project/$projectId/version" $headers)
    return $null -ne ($versions | Where-Object { $_.version_number -eq $versionNumber } | Select-Object -First 1)
}

if ($Build) {
    foreach ($mod in $Mods) {
        $set = if ($mod -eq 'BetterEnd') { 'end' } else { 'nether' }
        foreach ($loader in $Loaders) {
            $loaderArgument = if ($loader -eq 'Fabric') { 'fabric' } else { 'neo' }
            $arguments = @{ Set = $set; Loader = $loaderArgument; Versions = $Versions }
            if ($OnlineBuild) { $arguments.Online = $true }
            & (Join-Path $PSScriptRoot 'quick-build-bettermods.ps1') @arguments
            if ($LASTEXITCODE -ne 0) { throw "Build failed for $mod $loader" }
        }
    }
}

$plan = [System.Collections.Generic.List[object]]::new()
foreach ($mod in $Mods) {
    $project = $config.projects.$mod
    if (-not $project) { throw "Project '$mod' is missing from $Manifest" }
    foreach ($versionKey in $Versions) {
        $versionConfig = $config.versions.$versionKey
        if (-not $versionConfig) { throw "Version '$versionKey' is missing from $Manifest" }
        foreach ($loader in $Loaders) {
            $directory = Get-ProjectDirectory $mod $versionKey $loader
            if (-not (Test-Path -LiteralPath $directory)) { throw "Project directory is missing: $directory" }
            $modVersion = Get-PropertyValue (Join-Path $directory 'gradle.properties') 'mod_version'
            $jar = Get-ReleaseJar $directory $modVersion
            $suffix = if ($loader -eq 'Fabric') { '-fabric' } else { '' }
            $versionNumber = "$modVersion$suffix"
            $displayName = "$($project.displayName)-$versionNumber [$($versionConfig.displayLabel)]"
            $gameVersions = @($versionConfig.gameVersions)
            $platformLoaders = if ($loader -eq 'Fabric') { @('fabric', 'quilt') } else { @('neoforge') }
            $curseLoaders = if ($loader -eq 'Fabric') { @('Fabric', 'Quilt') } else { @('NeoForge') }
            $changelog = Get-Changelog $mod $versionKey
            $existsOnModrinth = $false
            if ($Platform -in @('Both', 'Modrinth')) {
                $existsOnModrinth = Test-ModrinthVersionExists $project.modrinthProjectId $versionNumber
            }
            $plan.Add([pscustomobject]@{
                Mod = $mod
                Minecraft = $versionKey
                Loader = $loader
                ModVersion = $modVersion
                VersionNumber = $versionNumber
                DisplayName = $displayName
                GameVersions = $gameVersions
                ModrinthLoaders = $platformLoaders
                CurseForgeLoaders = $curseLoaders
                Jar = $jar.FullName
                JarBytes = $jar.Length
                Changelog = $changelog.Path
                ExistsOnModrinth = $existsOnModrinth
                Status = 'planned'
                ModrinthUrl = $null
                CurseForgeUrl = $null
                Error = $null
            })
        }
    }
}

$plan | ConvertTo-Json -Depth 8 | Set-Content -LiteralPath $reportPath -Encoding UTF8
$plan | Select-Object Mod, Minecraft, Loader, ModVersion, DisplayName,
    @{N='GameVersions';E={$_.GameVersions -join ', '}},
    @{N='JAR';E={[IO.Path]::GetFileName($_.Jar)}}, ExistsOnModrinth | Format-Table -AutoSize
Write-Host "Release plan: $reportPath" -ForegroundColor Cyan

if (-not $Publish) {
    Write-Host 'DRY RUN: nothing was uploaded. Add -Publish after reviewing the plan.' -ForegroundColor Yellow
    exit 0
}

$duplicates = @($plan | Where-Object { $_.ExistsOnModrinth })
if ($duplicates.Count -gt 0 -and -not $SkipExisting) {
    throw "Modrinth already contains: $($duplicates.VersionNumber -join ', '). Use -SkipExisting only when resuming a partial publication."
}
if (-not $Yes) {
    $answer = Read-Host "Publish $($plan.Count) JARs to $Platform? Type PUBLISH"
    if ($answer -cne 'PUBLISH') { throw 'Publication cancelled' }
}

$modrinthToken = if ($Platform -in @('Both', 'Modrinth')) { Get-Token 'MODRINTH_TOKEN' } else { $null }
$curseForgeToken = if ($Platform -in @('Both', 'CurseForge')) { Get-Token 'CURSEFORGE_TOKEN' } else { $null }
$releaseTypeValue = $ReleaseType.ToLowerInvariant()

foreach ($item in $plan) {
    $project = $config.projects.($item.Mod)
    $changelog = Get-Content -LiteralPath $item.Changelog -Raw -Encoding UTF8
    try {
        if ($Platform -in @('Both', 'Modrinth')) {
            if ($item.ExistsOnModrinth -and $SkipExisting) {
                Write-Host "[$($item.Mod) $($item.VersionNumber)] Modrinth already exists; skipped" -ForegroundColor DarkYellow
            } else {
                $dependencies = @($config.dependencies | ForEach-Object {
                    @{ project_id = $_.modrinthProjectId; dependency_type = 'required' }
                })
                $metadata = @{
                    name = $item.VersionNumber
                    version_number = $item.VersionNumber
                    changelog = $changelog.Trim()
                    dependencies = $dependencies
                    game_versions = @($item.GameVersions)
                    version_type = $releaseTypeValue
                    loaders = @($item.ModrinthLoaders)
                    featured = $true
                    status = 'listed'
                    project_id = $project.modrinthProjectId
                    file_parts = @('file')
                    primary_file = 'file'
                    environment = 'client_and_server'
                } | ConvertTo-Json -Depth 10 -Compress
                $response = Invoke-MultipartPost 'https://api.modrinth.com/v2/version' `
                    @{ Authorization = $modrinthToken } 'data' $metadata (Get-Item -LiteralPath $item.Jar)
                $item.ModrinthUrl = "https://modrinth.com/mod/$($project.modrinthProjectId)/version/$($response.id)"
                Write-Host "[$($item.Mod) $($item.VersionNumber)] Modrinth: $($item.ModrinthUrl)" -ForegroundColor Green
            }
        }

        if ($Platform -in @('Both', 'CurseForge')) {
            $curseNames = @($item.GameVersions) + @($item.CurseForgeLoaders)
            $versionIds = Get-CurseForgeVersionIds $curseNames $curseForgeToken
            $relations = @($config.dependencies | ForEach-Object {
                @{ slug = $_.curseForgeSlug; type = 'requiredDependency' }
            })
            $metadata = @{
                changelogType = 'markdown'
                changelog = $changelog.Trim()
                displayName = $item.DisplayName
                releaseType = $releaseTypeValue
                gameVersions = @($versionIds)
                relations = @{ projects = $relations }
            } | ConvertTo-Json -Depth 10 -Compress
            $uri = "https://minecraft.curseforge.com/api/projects/$($project.curseForgeProjectId)/upload-file"
            $response = Invoke-MultipartPost $uri @{ 'X-Api-Token' = $curseForgeToken } `
                'metadata' $metadata (Get-Item -LiteralPath $item.Jar)
            $item.CurseForgeUrl = "https://www.curseforge.com/minecraft/mc-mods/$($project.curseForgeSlug)/files/$($response.id)"
            Write-Host "[$($item.Mod) $($item.VersionNumber)] CurseForge: $($item.CurseForgeUrl)" -ForegroundColor Green
        }
        $item.Status = 'published'
    } catch {
        $item.Status = 'failed'
        $item.Error = $_.Exception.Message
        $plan | ConvertTo-Json -Depth 8 | Set-Content -LiteralPath $reportPath -Encoding UTF8
        throw "Publication failed for $($item.Mod) $($item.VersionNumber): $($_.Exception.Message). Resume with -Publish -SkipExisting after correcting the problem."
    }
    $plan | ConvertTo-Json -Depth 8 | Set-Content -LiteralPath $reportPath -Encoding UTF8
}

Write-Host "Published $($plan.Count) release files successfully. Report: $reportPath" -ForegroundColor Green
