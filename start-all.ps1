# start-all.ps1 — Script de démarrage TalentConnect
# Usage: .\start-all.ps1
# Attendre ~60 secondes après le lancement

$b = Split-Path -Parent $MyInvocation.MyCommand.Path
$jwt = "dev-local-only-secret-change-in-prod-32ch"

Write-Host "🚀 Démarrage de TalentConnect Microservices..." -ForegroundColor Cyan

# Auth Service (8081)
Start-Process java -ArgumentList `
    "-DAPP_JWT_SECRET=$jwt", `
    "-DAPP_BOOTSTRAP_ADMIN_PASSWORD=Admin@Dev123", `
    "-DAPP_BOOTSTRAP_ADMIN_ENABLED=true", `
    "-DAPP_BOOTSTRAP_ADMIN_EMAIL=admin@talentconnect.tn", `
    "-jar", "auth-service\target\auth-service-1.0.0-SNAPSHOT.jar" `
    -WorkingDirectory $b `
    -RedirectStandardOutput "logs\auth-service.log" `
    -RedirectStandardError "logs\auth-service-err.log" `
    -WindowStyle Hidden
Write-Host "  ▶ auth-service        (port 8081)" -ForegroundColor Green

# Job Service (8085)
Start-Process java -ArgumentList `
    "-DAPP_JWT_SECRET=$jwt", `
    "-jar", "job-service\target\job-service-1.0.0-SNAPSHOT.jar" `
    -WorkingDirectory $b `
    -RedirectStandardOutput "logs\job-service.log" `
    -RedirectStandardError "logs\job-service-err.log" `
    -WindowStyle Hidden
Write-Host "  ▶ job-service         (port 8085)" -ForegroundColor Green

# Candidatures Service (8084)
Start-Process java -ArgumentList `
    "-jar", "candidatures-service\target\candidatures-service-1.0.0-SNAPSHOT.jar" `
    -WorkingDirectory $b `
    -RedirectStandardOutput "logs\candidatures-service.log" `
    -RedirectStandardError "logs\candidatures-service-err.log" `
    -WindowStyle Hidden
Write-Host "  ▶ candidatures-service (port 8084)" -ForegroundColor Green

# File Service (8082)
Start-Process java -ArgumentList `
    "-jar", "file-service\target\file-service-1.0.0-SNAPSHOT.jar" `
    -WorkingDirectory $b `
    -RedirectStandardOutput "logs\file-service.log" `
    -RedirectStandardError "logs\file-service-err.log" `
    -WindowStyle Hidden
Write-Host "  ▶ file-service        (port 8082)" -ForegroundColor Green

# Chatbot Service (8083)
Start-Process java -ArgumentList `
    "-jar", "chatbot-service\target\chatbot-service-1.0.0-SNAPSHOT.jar" `
    -WorkingDirectory $b `
    -RedirectStandardOutput "logs\chatbot-service.log" `
    -RedirectStandardError "logs\chatbot-service-err.log" `
    -WindowStyle Hidden
Write-Host "  ▶ chatbot-service     (port 8083)" -ForegroundColor Green

Write-Host ""
Write-Host "⏳ Attendre ~60 secondes pour le démarrage complet..." -ForegroundColor Yellow
Write-Host ""
Write-Host "🔐 Credentials Admin: admin@talentconnect.tn / Admin@Dev123" -ForegroundColor Cyan
Write-Host ""
Write-Host "📋 Swagger UIs:" -ForegroundColor Cyan
Write-Host "   auth-service    → http://localhost:8081/swagger-ui.html"
Write-Host "   job-service     → http://localhost:8085/swagger-ui.html"
Write-Host "   candidatures    → http://localhost:8084/swagger-ui.html"
Write-Host "   file-service    → http://localhost:8082/swagger-ui.html"
Write-Host "   chatbot-service → http://localhost:8083/swagger-ui.html"

