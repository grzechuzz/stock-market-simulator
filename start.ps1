param(
    [Parameter(Mandatory = $true)]
    [string]$Port
)

if ($Port -notmatch '^\d+$') {
    Write-Error "Port must be a number."
    exit 1
}

$PortNumber = [int]$Port
if ($PortNumber -lt 1 -or $PortNumber -gt 65535) {
    Write-Error "Port must be between 1 and 65535."
    exit 1
}

if (-not (Test-Path ".env")) {
    if (-not (Test-Path ".env.example")) {
        Write-Error ".env.example not found."
        exit 1
    }

    Copy-Item ".env.example" ".env"
    Write-Host "Created .env from .env.example."
}

$env:APP_PORT = $Port

$Docker = Get-Command docker -ErrorAction SilentlyContinue
if ($Docker) {
    docker compose version *> $null
    if ($LASTEXITCODE -eq 0) {
        docker compose up --build
        exit $LASTEXITCODE
    }
}

$DockerCompose = Get-Command docker-compose -ErrorAction SilentlyContinue
if ($DockerCompose) {
    docker-compose version *> $null
    if ($LASTEXITCODE -eq 0) {
        docker-compose up --build
        exit $LASTEXITCODE
    }
}

Write-Error "Docker Compose is not available."
exit 1
