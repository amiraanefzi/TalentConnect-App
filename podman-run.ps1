param(
    [string]$ImageName = "talent-connect:podman",
    [string]$ContainerName = "talent-connect-podman",
    [int]$HostPort = 8081,
    [int]$ContainerPort = 8080
)

$ErrorActionPreference = "Stop"

if (-not (Get-Command podman -ErrorAction SilentlyContinue)) {
    Write-Error "Podman is not installed or not available in PATH. Install Podman and WSL, then retry."
    exit 1
}

Write-Host "Removing any existing container named '$ContainerName'..."
try {
    if (podman ps --all --format "{{.Names}}" | Select-String -Pattern "^$ContainerName$") {
        podman rm -f $ContainerName | Out-Null
    }
} catch {
    # ignore when container does not exist
}

Write-Host "Starting container '$ContainerName' mapping host port $HostPort -> container port $ContainerPort..."
podman run -d --name $ContainerName -p "$HostPort:$ContainerPort" $ImageName | Out-Null
Write-Host "Container '$ContainerName' started. Access the app at http://localhost:$HostPort"
