@echo off
echo Starting Personal Finance Manager...
echo.

REM Check if Java is available
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo Error: Java is not installed or not in PATH
    echo Please install Java JDK 17+ and add it to your PATH
    pause
    exit /b 1
)

REM Check if Maven is available
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo Error: Maven is not installed or not in PATH
    echo Please install Maven and add it to your PATH
    pause
    exit /b 1
)

echo Building and running the application...
echo.

REM Compile and run with JavaFX
mvn clean compile javafx:run

if %errorlevel% neq 0 (
    echo.
    echo Error: Failed to run the application
    echo Make sure JavaFX is properly configured
    pause
    exit /b 1
)

echo.
echo Application closed.
pause
