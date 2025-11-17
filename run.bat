@echo off
chcp 65001 >nul
echo ========================================
echo   SQL Injection Demo - Starting Service
echo ========================================
echo.

REM Check if Maven is available
where mvn >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Maven not found in PATH!
    echo Make sure Maven is installed and added to the PATH environment variable.
    echo.
    pause
    exit /b 1
)

REM Check if Java is available
where java >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Java not found in PATH!
    echo Make sure Java is installed and added to the PATH environment variable.
    echo.
    pause
    exit /b 1
)

echo [INFO] Checking Java version...
java -version
echo.

echo [INFO] Starting Spring Boot application...
echo [INFO] Service will be available at: http://localhost:8080
echo [INFO] H2 Console will be available at: http://localhost:8080/h2-console
echo.
echo Press Ctrl+C to stop the service
echo.

mvn spring-boot:run

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Failed to start the application!
    pause
    exit /b 1
)

pause

