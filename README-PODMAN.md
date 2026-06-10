# Podman support for TalentConnect

This repository includes a `Containerfile` and Podman helper scripts to build and run the root Spring Boot application.

## Prerequisites

- Podman installed on Windows
- Windows Subsystem for Linux (WSL) installed and configured
- Podman machine running

## Build with Podman

From the repository root:

```powershell
.\podman-build.ps1
```

## Run with Podman

```powershell
.\podman-run.ps1
```

The app is exposed on `http://localhost:8081`.

## Troubleshooting

If Podman cannot connect, run:

```powershell
wsl.exe --install
podman machine init --now
```

Then retry the build and run scripts.
