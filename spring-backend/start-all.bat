@echo off
echo ========================================
echo  Starting Spring Boot Microservices
echo ========================================
echo.

set "BASE_DIR=C:\Users\ali.mellouk.ext\inventory_system\spring-backend"
set "JAVA_EXE=C:\Users\ali.mellouk.ext\AppData\Local\Programs\Eclipse Adoptium\jdk-17.0.18.8-hotspot\bin\java.exe"
set "LOG_DIR=%BASE_DIR%\logs"

REM Create logs directory
if not exist "%LOG_DIR%" mkdir "%LOG_DIR%"

REM Test Java
echo Testing Java...
"%JAVA_EXE%" -version
if errorlevel 1 (
    echo ERROR: Java not working!
    pause
    exit /b 1
)

echo.
echo [1/4] Starting Discovery Server on port 8761...
start "Eureka" cmd /k ""%JAVA_EXE%" -jar "%BASE_DIR%\discovery-server\target\discovery-server-1.0.0.jar""

echo Waiting 40 seconds for Eureka...
ping -n 41 127.0.0.1 > nul

echo [2/4] Starting Auth Service on port 8081...
start "Auth" cmd /k ""%JAVA_EXE%" -Dspring.profiles.active=h2 -jar "%BASE_DIR%\auth-service\target\auth-service-spring-1.0.0.jar""

ping -n 11 127.0.0.1 > nul

echo [3/4] Starting Inventory Service on port 8082...
start "Inventory" cmd /k ""%JAVA_EXE%" -Dspring.profiles.active=h2 -jar "%BASE_DIR%\inventory-service\target\inventory-service-1.0.0.jar""

ping -n 11 127.0.0.1 > nul

echo [4/4] Starting Analytics Service on port 8083...
start "Analytics" cmd /k ""%JAVA_EXE%" -jar "%BASE_DIR%\analytics-service\target\analytics-service-1.0.0.jar""

echo.
echo ========================================
echo  All services started!
echo ========================================
echo.
echo URLs:
echo   Eureka:    http://localhost:8761 (eureka/eureka123)
echo   Auth:      http://localhost:8081
echo   Inventory: http://localhost:8082
echo   Analytics: http://localhost:8083
echo.
echo Checking ports in 30 seconds...
ping -n 31 127.0.0.1 > nul

netstat -ano | findstr "LISTENING" | findstr "8761 8081 8082 8083"
echo.
pause

