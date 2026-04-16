@echo off
REM Personal Budget Manager - Quick Start Script
REM This script builds and runs the application

echo.
echo ========================================
echo Personal Budget Manager - Quick Start
echo ========================================
echo.

REM Check if Maven is available
where mvnw >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Maven wrapper not found!
    echo Please run this script from the project root directory.
    pause
    exit /b 1
)

REM Clean and build
echo Cleaning and building the project...
call mvnw clean install -DskipTests
if %errorlevel% neq 0 (
    echo.
    echo ERROR: Build failed!
    echo Please check the errors above and refer to README.md for troubleshooting.
    pause
    exit /b 1
)

echo.
echo ========================================
echo Build Successful!
echo ========================================
echo.
echo Starting the application...
echo The app will be available at: http://localhost:8080
echo Press Ctrl+C to stop the application.
echo.

REM Run the application
call mvnw spring-boot:run

pause
