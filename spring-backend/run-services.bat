@echo off
setlocal enabledelayedexpansion

echo ========================================
echo  Starting Spring Boot Microservices
echo ========================================
echo.

set "BASE_DIR=C:\Users\ali.mellouk.ext\inventory_system\spring-backend"
set "JAVA_EXE=C:\Users\ali.mellouk.ext\AppData\Local\Programs\Eclipse Adoptium\jdk-17.0.18.8-hotspot\bin\java.exe"

REM Check if Java exists
if not exist "%JAVA_EXE%" (
    echo Java not found at: %JAVA_EXE%
    echo Searching for Java...
    for /f "delims=" %%i in ('where java 2^>nul') do set "JAVA_EXE=%%i"
    if not exist "!JAVA_EXE!" (
        echo ERROR: Java not found! Please install Java 17.
        pause
        exit /b 1
    )
)

echo Using Java: %JAVA_EXE%
echo.

REM Check if JARs exist
if not exist "%BASE_DIR%\discovery-server\target\discovery-server-1.0.0.jar" (
    echo ERROR: discovery-server-1.0.0.jar not found!
    echo Please run: mvn clean install -DskipTests
    pause
    exit /b 1
)

echo [1/4] Starting Discovery Server (Eureka) on port 8761...
start "Eureka-Server" "%JAVA_EXE%" -jar "%BASE_DIR%\discovery-server\target\discovery-server-1.0.0.jar"

echo Waiting 30 seconds for Eureka to initialize...
timeout /t 30 /nobreak > nul

echo [2/4] Starting Auth Service on port 8081...
start "Auth-Service" "%JAVA_EXE%" -Dspring.profiles.active=h2 -jar "%BASE_DIR%\auth-service\target\auth-service-spring-1.0.0.jar"

timeout /t 10 /nobreak > nul

echo [3/4] Starting Inventory Service on port 8082...
start "Inventory-Service" "%JAVA_EXE%" -Dspring.profiles.active=h2 -jar "%BASE_DIR%\inventory-service\target\inventory-service-1.0.0.jar"

timeout /t 10 /nobreak > nul

echo [4/4] Starting Analytics Service on port 8083...
start "Analytics-Service" "%JAVA_EXE%" -jar "%BASE_DIR%\analytics-service\target\analytics-service-1.0.0.jar"

echo.
echo ========================================
echo  All services are starting!
echo ========================================
echo.
echo Services URLs:
echo   - Eureka Dashboard: http://localhost:8761
echo   - Auth Service:     http://localhost:8081
echo   - Inventory Service: http://localhost:8082
echo   - Analytics Service: http://localhost:8083
echo.
echo Eureka credentials: eureka / eureka123
echo.
echo Waiting 20 seconds then checking services...
timeout /t 20 /nobreak > nul

echo.
echo Checking active ports...
netstat -ano | findstr "8761 8081 8082 8083"

echo.
pause

