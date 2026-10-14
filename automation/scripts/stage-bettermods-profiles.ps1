[CmdletBinding()]
param(
    [string]$ProfilesRoot = 'C:\Users\Redmi\AppData\Roaming\ModrinthApp\profiles',
    [string]$BundleRoot = '',
    [string]$BackupRoot = ''
)

$ErrorActionPreference = 'Stop'
$workspace = Split-Path -Parent $PSScriptRoot
if (-not $BundleRoot) { $BundleRoot = Join-Path $workspace 'release\bundles' }
$stamp = Get-Date -Format 'yyyyMMdd-HHmmss'
if (-not $BackupRoot) { $BackupRoot = Join-Path $workspace "release\profile-backups\$stamp" }

$versions = @('1.21.1', '1.21.11', '26.1', '26.2', '26.3')
$mods = @('BCLib', 'WorldWeaver', 'WunderLib', 'BetterEnd', 'BetterNether')
$ownJarPattern = '(?i)^(better[-_ ]?end|better[-_ ]?nether|bclib|worldweaver|wunderlib|wover)(?:[-_ ].*)?\.jar$'

function Get-PropertyValue([string]$path, [string]$name) {
    $line = Select-String -LiteralPath $path -Pattern "^$([regex]::Escape($name))\s*=" | Select-Object -First 1
    if (-not $line) { throw "Property '$name' is missing from $path" }
    return ($line.Line -split '=', 2)[1].Trim()
}

function Get-ProjectDirectory([string]$mod, [string]$version, [string]$loader) {
    if ($version -eq '1.21.1') {
        if ($loader -eq 'Fabric') {
            switch ($mod) {
                'BCLib' { return Join-Path $workspace 'BCLib-1.21.1-Fabric' }
                'WorldWeaver' { return Join-Path $workspace 'WorldWeaver-1.21.1-Fabric' }
                'WunderLib' { return Join-Path $workspace 'WunderLib-Fabric-1.21.1' }
                default { return Join-Path $workspace "$mod-Fabric-1.21.1" }
            }
        }
        switch ($mod) {
            'BCLib' { return Join-Path $workspace 'BCLib-1.21.1-Neoforge' }
            'WorldWeaver' { return Join-Path $workspace 'WorldWeaver-1.21.1-Neoforge' }
            'WunderLib' { return Join-Path $workspace 'WunderLib-1.21.1-Neoforge' }
            default { return Join-Path $workspace $mod }
        }
    }
    if ($loader -eq 'Fabric') { return Join-Path $workspace "$mod-Fabric-$version" }
    return Join-Path $workspace "$mod-$version"
}

function Get-ProductionJar([string]$projectDirectory) {
    $properties = Join-Path $projectDirectory 'gradle.properties'
    $modVersion = Get-PropertyValue $properties 'mod_version'
    $libDirectory = Join-Path $projectDirectory 'build\libs'
    $jars = @(Get-ChildItem -LiteralPath $libDirectory -File -Filter '*.jar' -ErrorAction SilentlyContinue |
        Where-Object {
            $_.Name -notmatch '(?i)(sources|javadoc|dev|shadow|plain)' -and
            $_.BaseName -match "(^|[-_])$([regex]::Escape($modVersion))($|[-_])"
        } | Sort-Object LastWriteTimeUtc -Descending)
    if ($jars.Count -eq 0) { throw "No production JAR $modVersion in $libDirectory" }
    return $jars[0]
}

function Test-JarMetadata([System.IO.FileInfo]$jar, [string]$loader) {
    Add-Type -AssemblyName System.IO.Compression.FileSystem
    $zip = [System.IO.Compression.ZipFile]::OpenRead($jar.FullName)
    try {
        $metadataNames = if ($loader -eq 'Fabric') {
            @('fabric.mod.json')
        } else {
            @('META-INF/neoforge.mods.toml', 'META-INF/mods.toml')
        }
        $entry = $zip.Entries | Where-Object { $_.FullName -in $metadataNames } | Select-Object -First 1
        if (-not $entry) { throw "$($jar.Name) has no $loader metadata" }
        $reader = [IO.StreamReader]::new($entry.Open())
        try { $metadata = $reader.ReadToEnd() } finally { $reader.Dispose() }
        if ($metadata -notmatch [regex]::Escape('Raijin2312')) {
            throw "$($jar.Name) metadata does not contain Raijin2312"
        }
    } finally {
        $zip.Dispose()
    }
}

$plan = [System.Collections.Generic.List[object]]::new()
foreach ($version in $versions) {
    foreach ($loader in @('NeoForge', 'Fabric')) {
        $profileName = "$loader $version"
        $profile = Join-Path $ProfilesRoot $profileName
        $modsDirectory = Join-Path $profile 'mods'
        if (-not (Test-Path -LiteralPath $modsDirectory -PathType Container)) {
            throw "Profile mods directory is missing: $modsDirectory"
        }
        foreach ($mod in $mods) {
            $project = Get-ProjectDirectory $mod $version $loader
            $jar = Get-ProductionJar $project
            Test-JarMetadata $jar $loader
            $plan.Add([pscustomobject]@{
                Version = $version
                Loader = $loader
                Profile = $profileName
                ModsDirectory = $modsDirectory
                Mod = $mod
                Jar = $jar.FullName
                FileName = $jar.Name
                Bytes = $jar.Length
            })
        }
    }
}

# Validate all 50 artifacts and all targets before changing any profile.
if ($plan.Count -ne 50) { throw "Expected 50 artifacts, got $($plan.Count)" }

foreach ($group in $plan | Group-Object Profile) {
    $profileRows = @($group.Group)
    $bundle = Join-Path $BundleRoot $group.Name
    $backup = Join-Path $BackupRoot $group.Name
    $null = New-Item -ItemType Directory -Path $bundle -Force
    $oldJars = @(Get-ChildItem -LiteralPath $profileRows[0].ModsDirectory -File -Filter '*.jar' |
        Where-Object { $_.Name -match $ownJarPattern })
    if ($oldJars.Count -gt 0) {
        $null = New-Item -ItemType Directory -Path $backup -Force
        foreach ($oldJar in $oldJars) {
            Move-Item -LiteralPath $oldJar.FullName -Destination (Join-Path $backup $oldJar.Name)
        }
    }
    foreach ($row in $profileRows) {
        Copy-Item -LiteralPath $row.Jar -Destination (Join-Path $bundle $row.FileName) -Force
        Copy-Item -LiteralPath $row.Jar -Destination (Join-Path $row.ModsDirectory $row.FileName) -Force
    }
}

$report = Join-Path $BundleRoot 'installed-artifacts.json'
$plan | ConvertTo-Json -Depth 5 | Set-Content -LiteralPath $report -Encoding UTF8
$plan | Select-Object Profile, Mod, FileName, Bytes | Format-Table -AutoSize
Write-Host "Staged $($plan.Count) verified artifacts in $BundleRoot" -ForegroundColor Green
Write-Host "Previous profile JARs were moved to $BackupRoot" -ForegroundColor Green
Write-Host "Report: $report" -ForegroundColor Cyan
