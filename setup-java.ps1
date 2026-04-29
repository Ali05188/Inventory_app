# Spring Boot Project Setup Script for Windows
# This script will help you install Java and run the Spring Boot applications

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Spring Boot Project Setup Script" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Check if running as Administrator
$isAdmin = ([Security.Principal.WindowsPrincipal] [Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)

if (-not $isAdmin) {
    Write-Host "NOTE: Some installations may require Administrator privileges." -ForegroundColor Yellow
    Write-Host ""
}

# Check for winget
$hasWinget = Get-Command winget -ErrorAction SilentlyContinue

if ($hasWinget) {
    Write-Host "[OK] Windows Package Manager (winget) is available" -ForegroundColor Green

    # Check for Java
    $javaPath = Get-Command java -ErrorAction SilentlyContinue

    if (-not $javaPath) {
        Write-Host ""
        Write-Host "Java is not installed. Installing OpenJDK 17..." -ForegroundColor Yellow
        Write-Host "Running: winget install Microsoft.OpenJDK.17" -ForegroundColor Gray
        winget install Microsoft.OpenJDK.17 --accept-package-agreements --accept-source-agreements

        # Refresh PATH
        $env:Path = [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path","User")
        Write-Host "[OK] Java 17 installed!" -ForegroundColor Green
    } else {
        Write-Host "[OK] Java is already installed" -ForegroundColor Green
        java -version
    }

    # Check for Maven
    $mavenPath = Get-Command mvn -ErrorAction SilentlyContinue

    if (-not $mavenPath) {
        Write-Host ""
        Write-Host "Maven is not installed. Installing Maven..." -ForegroundColor Yellow
        Write-Host "Running: winget install Apache.Maven" -ForegroundColor Gray
        winget install Apache.Maven --accept-package-agreements --accept-source-agreements

        # Refresh PATH
        $env:Path = [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path","User")
        Write-Host "[OK] Maven installed!" -ForegroundColor Green
    } else {
        Write-Host "[OK] Maven is already installed" -ForegroundColor Green
        mvn -version
    }

} else {
    Write-Host "[!] winget is not available. Please install manually:" -ForegroundColor Red
    Write-Host ""
    Write-Host "1. Download Java 17 from: https://adoptium.net/temurin/releases/" -ForegroundColor White
    Write-Host "2. Download Maven from: https://maven.apache.org/download.cgi" -ForegroundColor White
    Write-Host "3. Add both to your system PATH" -ForegroundColor White
    exit 1
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Setup Complete!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Please CLOSE this terminal and open a NEW terminal," -ForegroundColor Yellow
Write-Host "then run the following commands:" -ForegroundColor Yellow
Write-Host ""
Write-Host "To start Auth Service:" -ForegroundColor White
Write-Host "  cd C:\Users\ALI\Downloads\Inventory_app-main\auth-service-spring" -ForegroundColor Gray
Write-Host "  mvn spring-boot:run" -ForegroundColor Gray
Write-Host ""
Write-Host "To start Inventory Service (in another terminal):" -ForegroundColor White
Write-Host "  cd C:\Users\ALI\Downloads\Inventory_app-main\inventory-service-spring" -ForegroundColor Gray
Write-Host "  mvn spring-boot:run" -ForegroundColor Gray
Write-Host ""

