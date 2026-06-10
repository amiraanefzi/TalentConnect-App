param(
    [string]$ImageName = "talent-connect:podman",
    [string]$ContextPath = ".",
    [string]$Containerfile = "Containerfile"
)

$ErrorActionPreference = "Stop"

if (-not (Get-Command podman -ErrorAction SilentlyContinue)) {
    Write-Error "Podman is not installed or not available in PATH. Install Podman and WSL, then retry."
    exit 1
}

Write-Host "Building Podman image '$ImageName' from $Containerfile..."
podman build -f $Containerfile -t $ImageName $ContextPath
Write-Host "Podman image '$ImageName' built successfully."
