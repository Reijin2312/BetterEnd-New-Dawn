$ErrorActionPreference = 'Continue'
$workspace = Split-Path -Parent $PSScriptRoot
$statusPath = Join-Path $workspace 'release\1.21.1-publish\changelog-retry-status.json'
$remaining = [Collections.Generic.List[string]]::new()
@('bclib-neo', 'end-neo') | ForEach-Object { $remaining.Add($_) }

for ($attempt = 1; $attempt -le 180 -and $remaining.Count -gt 0; $attempt++) {
    foreach ($key in @($remaining)) {
        $null = & powershell.exe -NoProfile -ExecutionPolicy Bypass -File `
            (Join-Path $PSScriptRoot 'publish-1211-release.ps1') `
            -UpdateChangelogs -CurseForgeOnly -UpdateKeys $key 2>&1
        if ($LASTEXITCODE -eq 0) { $null = $remaining.Remove($key) }
    }

    $state = if ($remaining.Count -eq 0) { 'complete' } else { 'waiting' }
    [pscustomobject]@{
        state = $state
        attempt = $attempt
        remaining = @($remaining)
        updated = (Get-Date).ToString('o')
        processId = $PID
    } | ConvertTo-Json -Depth 4 | Set-Content -LiteralPath $statusPath -Encoding UTF8

    if ($remaining.Count -gt 0) { Start-Sleep -Seconds 120 }
}

if ($remaining.Count -gt 0) { exit 1 }
