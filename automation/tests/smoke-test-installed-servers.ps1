param(
    [switch]$Build,
    [int]$TimeoutSeconds = 300,
    [string[]]$Servers = @()
)

$ErrorActionPreference = 'Stop'
$workspace = Split-Path -Parent $PSScriptRoot
$desktop = [Environment]::GetFolderPath('Desktop')
$stamp = Get-Date -Format 'yyyyMMdd-HHmmss'
$failures = [System.Collections.Generic.List[string]]::new()

$matrix = @(
    @{ Name='1.21.1-neoforge'; Directory='neo 1.21.1 test'; Launcher='run.bat'; Projects=@('BCLib-1.21.1-Neoforge','WorldWeaver-1.21.1-Neoforge','BetterEnd','BetterNether') },
    @{ Name='26.1-neoforge'; Directory='26.1 neo'; Launcher='run.bat'; Projects=@('BCLib-26.1','WorldWeaver-26.1','BetterEnd-26.1','BetterNether-26.1') },
    @{ Name='26.1-fabric'; Directory='26.1 fab'; Launcher='start.bat'; Projects=@('BCLib-Fabric-26.1','WorldWeaver-Fabric-26.1','BetterEnd-Fabric-26.1','BetterNether-Fabric-26.1') },
    @{ Name='26.2-neoforge'; Directory='26.2 neo'; Launcher='run.bat'; Projects=@('BCLib-26.2','WorldWeaver-26.2','BetterEnd-26.2','BetterNether-26.2') },
    @{ Name='26.2-fabric'; Directory='26.2-serv-fabr'; Launcher='start.bat'; Projects=@('BCLib-Fabric-26.2','WorldWeaver-Fabric-26.2','BetterEnd-Fabric-26.2','BetterNether-Fabric-26.2') },
    @{ Name='26.3-neoforge'; Directory='26.3 neo'; Launcher='run.bat'; Projects=@('BCLib-26.3','WorldWeaver-26.3','BetterEnd-26.3','BetterNether-26.3') },
    @{ Name='26.3-fabric'; Directory='23.3 fab'; Launcher='start.bat'; Projects=@('BCLib-Fabric-26.3','WorldWeaver-Fabric-26.3','BetterEnd-Fabric-26.3','BetterNether-Fabric-26.3') }
)

if ($Servers.Count -gt 0) {
    $unknown = $Servers | Where-Object { $_ -notin $matrix.Name }
    if ($unknown) { throw "Unknown server name(s): $($unknown -join ', ')" }
    $matrix = $matrix | Where-Object { $_.Name -in $Servers }
}

function Stop-ProcessTree([int]$processId) {
    Get-CimInstance Win32_Process -Filter "ParentProcessId=$processId" -ErrorAction SilentlyContinue |
        ForEach-Object { Stop-ProcessTree ([int]$_.ProcessId) }
    Stop-Process -Id $processId -Force -ErrorAction SilentlyContinue
}

function Get-ReleaseJar([string]$project) {
    $libDir = Join-Path (Join-Path $workspace $project) 'build/libs'
    $jars = Get-ChildItem -LiteralPath $libDir -File -Filter '*.jar' -ErrorAction SilentlyContinue |
        Where-Object { $_.Name -notmatch '(?i)(sources|javadoc|dev|shadow|plain)' } |
        Sort-Object LastWriteTime -Descending
    if (-not $jars) { throw "$project has no release JAR in build/libs" }
    return $jars[0]
}

function Set-SmokeProperties([string]$path, [string]$worldName) {
    $lines = if (Test-Path -LiteralPath $path) { [System.IO.File]::ReadAllLines($path) } else { @() }
    $values = [ordered]@{ 'level-name'=$worldName; 'online-mode'='false'; 'enable-rcon'='false' }
    $result = [System.Collections.Generic.List[string]]::new()
    foreach ($line in $lines) {
        $key = if ($line -match '^([^#=]+)=') { $Matches[1] } else { $null }
        if ($key -and $values.Contains($key)) { continue }
        $result.Add($line)
    }
    foreach ($entry in $values.GetEnumerator()) { $result.Add("$($entry.Key)=$($entry.Value)") }
    [System.IO.File]::WriteAllLines($path, $result)
}

foreach ($server in $matrix) {
    $serverRoot = Join-Path $desktop $server.Directory
    $mods = Join-Path $serverRoot 'mods'
    Write-Host "`n[$($server.Name)] preparing $serverRoot" -ForegroundColor Cyan
    try {
        if (-not (Test-Path -LiteralPath (Join-Path $serverRoot $server.Launcher))) { throw 'launcher is missing' }
        if (-not (Test-Path -LiteralPath $mods)) { throw 'mods directory is missing' }

        if ($Build) {
            $parts = $server.Name -split '-'
            $version = $parts[0]
            $loader = if ($server.Name -like '*-fabric') { 'fabric' } else { 'neo' }
            foreach ($modSet in @('end', 'nether')) {
                & (Join-Path $PSScriptRoot 'quick-build-bettermods.ps1') -Set $modSet -Loader $loader -Versions $version
                if ($LASTEXITCODE -ne 0) { throw "$modSet $version $loader build failed with exit code $LASTEXITCODE" }
            }
        }

        $jars = @($server.Projects | ForEach-Object { Get-ReleaseJar $_ })
        $backup = Join-Path $serverRoot ".bettermods-backups\$stamp\mods"
        $null = New-Item -ItemType Directory -Path $backup -Force
        $oldMods = Get-ChildItem -LiteralPath $mods -File | Where-Object {
            $_.Name -match '(?i)^(better[-_ ]?end|better[-_ ]?nether|bclib|worldweaver).*\.jar$'
        }
        foreach ($old in $oldMods) { Move-Item -LiteralPath $old.FullName -Destination $backup }
        foreach ($jar in $jars) {
            Copy-Item -LiteralPath $jar.FullName -Destination (Join-Path $mods $jar.Name)
            Write-Host "  installed $($jar.Name)"
        }

        $properties = Join-Path $serverRoot 'server.properties'
        $oldProperties = if (Test-Path -LiteralPath $properties) { [System.IO.File]::ReadAllText($properties) } else { $null }
        $worldName = "bettermods-smoke-$stamp"
        $world = Join-Path $serverRoot $worldName
        Set-SmokeProperties $properties $worldName
        $stdout = Join-Path $serverRoot ".bettermods-smoke-$stamp.out.log"
        $stderr = Join-Path $serverRoot ".bettermods-smoke-$stamp.err.log"
        $process = Start-Process -FilePath 'cmd.exe' -ArgumentList @('/d','/c',$server.Launcher,'nogui') `
            -WorkingDirectory $serverRoot -RedirectStandardOutput $stdout -RedirectStandardError $stderr -PassThru
        $deadline = [DateTime]::UtcNow.AddSeconds($TimeoutSeconds)
        $started = $false
        try {
            while (-not $process.HasExited -and [DateTime]::UtcNow -lt $deadline) {
                Start-Sleep -Milliseconds 500
                $latestLog = Join-Path $serverRoot 'logs/latest.log'
                $text = (Get-Content -LiteralPath $stdout -Raw -ErrorAction SilentlyContinue) + "`n" +
                        (Get-Content -LiteralPath $stderr -Raw -ErrorAction SilentlyContinue) + "`n" +
                        (Get-Content -LiteralPath $latestLog -Raw -ErrorAction SilentlyContinue)
                if ($text -match '(?m)Done \([0-9.,]+s\)!|For help, type "help"') { $started = $true; break }
                if ($text -match '(?m)Failed to start the minecraft server|BUILD FAILED|Mod Loading has failed|Could not execute entrypoint') { break }
                $process.Refresh()
            }
        } finally {
            if (-not $process.HasExited) { Stop-ProcessTree $process.Id }
            if ($null -eq $oldProperties) { Remove-Item -LiteralPath $properties -Force -ErrorAction SilentlyContinue }
            else { [System.IO.File]::WriteAllText($properties, $oldProperties) }
            $resolvedRoot = [System.IO.Path]::GetFullPath($serverRoot).TrimEnd('\') + '\'
            $resolvedWorld = [System.IO.Path]::GetFullPath($world)
            if ($resolvedWorld.StartsWith($resolvedRoot, [System.StringComparison]::OrdinalIgnoreCase)) {
                Remove-Item -LiteralPath $resolvedWorld -Recurse -Force -ErrorAction SilentlyContinue
            }
        }

        if ($started) {
            Write-Host "  PASS: dedicated server reached Done" -ForegroundColor Green
        } else {
            $tail = @(
                Get-Content -LiteralPath $stdout -Tail 60 -ErrorAction SilentlyContinue
                Get-Content -LiteralPath $stderr -Tail 60 -ErrorAction SilentlyContinue
                Get-Content -LiteralPath (Join-Path $serverRoot 'logs/latest.log') -Tail 60 -ErrorAction SilentlyContinue
            ) -join "`n"
            throw "server did not reach Done within $TimeoutSeconds seconds`n$tail"
        }
    } catch {
        $failures.Add("$($server.Name): $($_.Exception.Message)")
        Write-Host "  FAIL: $($_.Exception.Message)" -ForegroundColor Red
    }
}

if ($failures.Count -gt 0) {
    Write-Host "`n$($failures.Count) server smoke test(s) failed:" -ForegroundColor Red
    $failures | ForEach-Object { Write-Host " - $_" -ForegroundColor Red }
    exit 1
}

Write-Host "`nAll $($matrix.Count) installed dedicated servers started successfully." -ForegroundColor Green
