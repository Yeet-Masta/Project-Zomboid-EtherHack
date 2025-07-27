@echo off
setlocal enabledelayedexpansion

echo EtherHack Installer
echo ==================

:: Set variables
set "JAVA_URL=https://download.oracle.com/java/20/archive/jdk-20.0.2_windows-x64_bin.exe"
set "JAVA_INSTALLER=%TEMP%\jdk_installer.exe"
set "JAVA_PATH=C:\Program Files\Java\jdk-20.0.2\bin"
set "REPO_URL=https://github.com/Yeet-Masta/Project-Zomboid-EtherHack.git"
set "ZOMBOID_PATH=C:\Steam\steamapps\common\ProjectZomboid"
set "VERSION=1.1"

:: Check if running as administrator
net session >nul 2>&1
if %errorlevel% neq 0 (
    echo This script requires administrator privileges.
    echo Please right-click on the script and select "Run as administrator".
    pause
    exit /b 1
)

:: Install Java if not already installed
if not exist "%JAVA_PATH%\java.exe" (
    echo Installing Java...
    echo Downloading Java installer...
    powershell -Command "(New-Object Net.WebClient).DownloadFile('%JAVA_URL%', '%JAVA_INSTALLER%')"
    echo Running Java installer...
    start /wait %JAVA_INSTALLER% /s
    del %JAVA_INSTALLER%
) else (
    echo Java is already installed.
)

:: Add Java to PATH if not already there
echo Checking Java in PATH...
set "PATH_UPDATED=0"
echo !PATH! | findstr /C:"%JAVA_PATH%" >nul
if %errorlevel% neq 0 (
    echo Adding Java to PATH...
    setx PATH "%PATH%;%JAVA_PATH%" /M
    set "PATH_UPDATED=1"
) else (
    echo Java is already in PATH.
)

:: Create a temporary folder for cloning
set "TEMP_DIR=%TEMP%\EtherHack_build"
if exist "%TEMP_DIR%" rd /s /q "%TEMP_DIR%"
mkdir "%TEMP_DIR%"
cd /d "%TEMP_DIR%"

:: Clone the repository
echo Cloning EtherHack repository...
git clone %REPO_URL% .
if %errorlevel% neq 0 (
    echo Failed to clone repository.
    echo Please make sure Git is installed and the repository URL is correct.
    pause
    exit /b 1
)

:: Build the JAR using Gradle
echo Building JAR file with Gradle...
call gradlew build
if %errorlevel% neq 0 (
    echo Failed to build the JAR file.
    pause
    exit /b 1
)

:: Find the built JAR file
echo Locating the built JAR file...
for /r %%i in (*EtherHack*.jar) do (
    set "JAR_FILE=%%i"
)

if not defined JAR_FILE (
    echo Could not find the built JAR file.
    pause
    exit /b 1
)

:: Check if Project Zomboid folder exists
if not exist "%ZOMBOID_PATH%" (
    echo Project Zomboid folder not found at %ZOMBOID_PATH%
    set /p ZOMBOID_PATH="Please enter the path to your Project Zomboid installation: "
)

:: Copy the JAR to the Project Zomboid folder
echo Copying JAR file to Project Zomboid folder...
copy "!JAR_FILE!" "%ZOMBOID_PATH%\EtherHack-%VERSION%.jar"

:: Install EtherHack
echo Installing EtherHack...
cd /d "%ZOMBOID_PATH%"
java -jar ./EtherHack-%VERSION%.jar --install

:: Clean up
rd /s /q "%TEMP_DIR%"

echo.
echo Installation completed!
if %PATH_UPDATED% equ 1 (
    echo NOTE: Java was added to PATH. You may need to restart your computer for changes to take effect.
)
echo You can now launch Project Zomboid with EtherHack installed.
echo.
pause 