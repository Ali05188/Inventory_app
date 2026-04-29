@echo off
echo ==========================================
echo   Java and Maven Portable Setup Script
echo ==========================================
echo.

set INSTALL_DIR=C:\dev-tools
set JAVA_DIR=%INSTALL_DIR%\jdk-17
set MAVEN_DIR=%INSTALL_DIR%\maven

echo Creating installation directory: %INSTALL_DIR%
if not exist "%INSTALL_DIR%" mkdir "%INSTALL_DIR%"

echo.
echo Downloading OpenJDK 17...
echo This may take a few minutes...
powershell -Command "& { [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri 'https://download.java.net/java/GA/jdk17.0.2/dfd4a8d0985749f896bed50d7138ee7f/8/GPL/openjdk-17.0.2_windows-x64_bin.zip' -OutFile '%INSTALL_DIR%\jdk17.zip' }"

if exist "%INSTALL_DIR%\jdk17.zip" (
    echo Extracting Java...
    powershell -Command "Expand-Archive -Path '%INSTALL_DIR%\jdk17.zip' -DestinationPath '%INSTALL_DIR%' -Force"
    if exist "%INSTALL_DIR%\jdk-17.0.2" (
        rename "%INSTALL_DIR%\jdk-17.0.2" "jdk-17"
    )
    del "%INSTALL_DIR%\jdk17.zip"
    echo [OK] Java 17 installed!
) else (
    echo [ERROR] Failed to download Java
    pause
    exit /b 1
)

echo.
echo Downloading Apache Maven...
powershell -Command "& { [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri 'https://dlcdn.apache.org/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.zip' -OutFile '%INSTALL_DIR%\maven.zip' }"

if exist "%INSTALL_DIR%\maven.zip" (
    echo Extracting Maven...
    powershell -Command "Expand-Archive -Path '%INSTALL_DIR%\maven.zip' -DestinationPath '%INSTALL_DIR%' -Force"
    if exist "%INSTALL_DIR%\apache-maven-3.9.6" (
        rename "%INSTALL_DIR%\apache-maven-3.9.6" "maven"
    )
    del "%INSTALL_DIR%\maven.zip"
    echo [OK] Maven installed!
) else (
    echo [ERROR] Failed to download Maven
    pause
    exit /b 1
)

echo.
echo ==========================================
echo Creating launcher scripts...
echo ==========================================

:: Create run-auth-service.bat
echo @echo off > "C:\Users\ALI\Downloads\Inventory_app-main\run-auth-service.bat"
echo set JAVA_HOME=%JAVA_DIR% >> "C:\Users\ALI\Downloads\Inventory_app-main\run-auth-service.bat"
echo set PATH=%%JAVA_HOME%%\bin;%MAVEN_DIR%\bin;%%PATH%% >> "C:\Users\ALI\Downloads\Inventory_app-main\run-auth-service.bat"
echo cd /d "C:\Users\ALI\Downloads\Inventory_app-main\auth-service-spring" >> "C:\Users\ALI\Downloads\Inventory_app-main\run-auth-service.bat"
echo echo Starting Auth Service on port 8081... >> "C:\Users\ALI\Downloads\Inventory_app-main\run-auth-service.bat"
echo mvn spring-boot:run >> "C:\Users\ALI\Downloads\Inventory_app-main\run-auth-service.bat"
echo pause >> "C:\Users\ALI\Downloads\Inventory_app-main\run-auth-service.bat"

:: Create run-inventory-service.bat
echo @echo off > "C:\Users\ALI\Downloads\Inventory_app-main\run-inventory-service.bat"
echo set JAVA_HOME=%JAVA_DIR% >> "C:\Users\ALI\Downloads\Inventory_app-main\run-inventory-service.bat"
echo set PATH=%%JAVA_HOME%%\bin;%MAVEN_DIR%\bin;%%PATH%% >> "C:\Users\ALI\Downloads\Inventory_app-main\run-inventory-service.bat"
echo cd /d "C:\Users\ALI\Downloads\Inventory_app-main\inventory-service-spring" >> "C:\Users\ALI\Downloads\Inventory_app-main\run-inventory-service.bat"
echo echo Starting Inventory Service on port 8082... >> "C:\Users\ALI\Downloads\Inventory_app-main\run-inventory-service.bat"
echo mvn spring-boot:run >> "C:\Users\ALI\Downloads\Inventory_app-main\run-inventory-service.bat"
echo pause >> "C:\Users\ALI\Downloads\Inventory_app-main\run-inventory-service.bat"

echo.
echo ==========================================
echo   SETUP COMPLETE!
echo ==========================================
echo.
echo Java installed to: %JAVA_DIR%
echo Maven installed to: %MAVEN_DIR%
echo.
echo To run the services, double-click:
echo   - run-auth-service.bat     (Port 8081)
echo   - run-inventory-service.bat (Port 8082)
echo.
echo After services start, open in browser:
echo   - http://localhost:8081/swagger-ui.html
echo   - http://localhost:8082/swagger-ui.html
echo.
echo Login: admin@inventory.com / password123
echo.
pause

