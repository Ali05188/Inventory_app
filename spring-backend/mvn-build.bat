@echo off
REM Script pour lancer le build Maven Spring Boot
REM Utilise le truststore Windows pour résoudre les problèmes SSL

set PATH=%PATH%;C:\temp\apache-maven-3.9.13\bin

echo Building Spring Boot Microservices...
echo.

mvn clean install -DskipTests -Djavax.net.ssl.trustStoreType=Windows-ROOT

echo.
if %ERRORLEVEL% EQU 0 (
    echo BUILD SUCCESS!
) else (
    echo BUILD FAILED!
)

pause

