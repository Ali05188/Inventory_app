@echo off
echo ========================================
echo  Starting Spring Boot Microservices
echo ========================================
echo.

set BASE_DIR=C:\Users\ali.mellouk.ext\inventory_system\spring-backend
set JAVA_HOME=C:\Users\ali.mellouk.ext\AppData\Local\Programs\Eclipse Adoptium\jdk-17.0.18.8-hotspot
set PATH=%JAVA_HOME%\bin;%PATH%

REM Verify Java
java -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Java not found! Please install Java 17.
    pause
    exit /b 1
)

echo Java found. Starting services...
echo.

echo [1/4] Starting Discovery Server (Eureka) on port 8761...
start "Discovery-Server" cmd /k "set JAVA_HOME=%JAVA_HOME% && set PATH=%JAVA_HOME%\bin;%PATH% && cd /d %BASE_DIR%\discovery-server\target && java -jar discovery-server-1.0.0.jar"

echo Waiting 25 seconds for Eureka to start...
timeout /t 25 /nobreak > nul

echo [2/4] Starting Auth Service on port 8081 (H2 database)...
start "Auth-Service" cmd /k "set JAVA_HOME=%JAVA_HOME% && set PATH=%JAVA_HOME%\bin;%PATH% && cd /d %BASE_DIR%\auth-service\target && java -jar auth-service-spring-1.0.0.jar"

timeout /t 10 /nobreak > nul

echo [3/4] Starting Inventory Service on port 8082 (H2 database)...
start "Inventory-Service" cmd /k "set JAVA_HOME=%JAVA_HOME% && set PATH=%JAVA_HOME%\bin;%PATH% && cd /d %BASE_DIR%\inventory-service\target && java -jar inventory-service-1.0.0.jar"

timeout /t 10 /nobreak > nul

echo [4/4] Starting Analytics Service on port 8083...
start "Analytics-Service" cmd /k "set JAVA_HOME=%JAVA_HOME% && set PATH=%JAVA_HOME%\bin;%PATH% && cd /d %BASE_DIR%\analytics-service\target && java -jar analytics-service-1.0.0.jar"

timeout /t 10 /nobreak > nul

echo [5/5] Starting API Gateway on port 8080...
start "API-Gateway" cmd /k "set JAVA_HOME=%JAVA_HOME% && set PATH=%JAVA_HOME%\bin;%PATH% && cd /d %BASE_DIR%\api-gateway\target && java -jar api-gateway-1.0.0.jar"

echo.
echo ========================================
echo  All services are starting!
echo ========================================
echo.
echo Services URLs:
echo   - Eureka Dashboard: http://localhost:8761
echo   - API Gateway:      http://localhost:8080
echo   - Auth Service:     http://localhost:8081
echo   - Inventory Service: http://localhost:8082
echo   - Analytics Service: http://localhost:8083
echo.
echo Eureka credentials: eureka / eureka123
echo.
echo Press any key to exit this window...
pause > nul
