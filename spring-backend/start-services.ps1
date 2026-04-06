# Script PowerShell pour démarrer les microservices Spring Boot
# Exécuter en tant qu'administrateur si nécessaire

$BASE_DIR = "C:\Users\ali.mellouk.ext\inventory_system\spring-backend"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Starting Spring Boot Microservices" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Arrêter tous les processus Java existants
Write-Host "Arrêt des processus Java existants..." -ForegroundColor Yellow
Get-Process -Name java -ErrorAction SilentlyContinue | Stop-Process -Force
Start-Sleep -Seconds 2

# 1. Démarrer Discovery Server
Write-Host "[1/4] Démarrage de Discovery Server (Eureka) sur le port 8761..." -ForegroundColor Green
Start-Process -FilePath "java" -ArgumentList "-jar", "$BASE_DIR\discovery-server\target\discovery-server-1.0.0.jar" -WorkingDirectory "$BASE_DIR\discovery-server\target"
Write-Host "Attente de 30 secondes pour le démarrage de Eureka..." -ForegroundColor Yellow
Start-Sleep -Seconds 30

# Vérifier si Eureka est démarré
$eurekaRunning = $false
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8761" -UseBasicParsing -TimeoutSec 5 -Credential (New-Object System.Management.Automation.PSCredential("eureka", (ConvertTo-SecureString "eureka123" -AsPlainText -Force)))
    $eurekaRunning = $true
    Write-Host "✓ Eureka démarré avec succès!" -ForegroundColor Green
} catch {
    Write-Host "⚠ Eureka pas encore prêt, on continue..." -ForegroundColor Yellow
}

# 2. Démarrer Auth Service
Write-Host "[2/4] Démarrage de Auth Service sur le port 8081 (H2 database)..." -ForegroundColor Green
Start-Process -FilePath "java" -ArgumentList "-Dspring.profiles.active=h2", "-jar", "$BASE_DIR\auth-service\target\auth-service-spring-1.0.0.jar" -WorkingDirectory "$BASE_DIR\auth-service\target"
Start-Sleep -Seconds 10

# 3. Démarrer Inventory Service
Write-Host "[3/4] Démarrage de Inventory Service sur le port 8082 (H2 database)..." -ForegroundColor Green
Start-Process -FilePath "java" -ArgumentList "-Dspring.profiles.active=h2", "-jar", "$BASE_DIR\inventory-service\target\inventory-service-1.0.0.jar" -WorkingDirectory "$BASE_DIR\inventory-service\target"
Start-Sleep -Seconds 10

# 4. Démarrer Analytics Service
Write-Host "[4/4] Démarrage de Analytics Service sur le port 8083..." -ForegroundColor Green
Start-Process -FilePath "java" -ArgumentList "-jar", "$BASE_DIR\analytics-service\target\analytics-service-1.0.0.jar" -WorkingDirectory "$BASE_DIR\analytics-service\target"

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Tous les services sont en cours de démarrage!" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Services URLs:" -ForegroundColor White
Write-Host "  - Eureka Dashboard: http://localhost:8761" -ForegroundColor White
Write-Host "  - Auth Service:     http://localhost:8081" -ForegroundColor White
Write-Host "  - Inventory Service: http://localhost:8082" -ForegroundColor White
Write-Host "  - Analytics Service: http://localhost:8083" -ForegroundColor White
Write-Host ""
Write-Host "Eureka credentials: eureka / eureka123" -ForegroundColor Yellow
Write-Host ""
Write-Host "Pour vérifier les services enregistrés dans Eureka:" -ForegroundColor Gray
Write-Host "  http://localhost:8761" -ForegroundColor Gray

