@echo off
set "LIB_DIR=..\lib"
set "BIN_DIR=..\bin"

if not exist "%BIN_DIR%" mkdir "%BIN_DIR%"

echo Compiling shared and client classes...
javac -cp "%LIB_DIR%\*;%BIN_DIR%" -d "%BIN_DIR%" com\quizapp\shared\*.java com\quizapp\client\*.java
if %ERRORLEVEL% neq 0 (
    echo Compilation failed!
    pause
    exit /b %ERRORLEVEL%
)

echo Compilation successful!
echo Use 'start java -cp "%LIB_DIR%\*;%BIN_DIR%" com.quizapp.client.AdminClient' or 'com.quizapp.client.UserClient' to run them.
