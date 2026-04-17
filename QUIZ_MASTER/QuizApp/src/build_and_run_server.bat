@echo off
set "LIB_DIR=..\lib"
set "SRC_DIR=."
set "BIN_DIR=..\bin"

if not exist "%BIN_DIR%" mkdir "%BIN_DIR%"

echo Compiling shared and server classes...
javac -cp "%LIB_DIR%\*;%BIN_DIR%" -d "%BIN_DIR%" com\quizapp\shared\*.java com\quizapp\server\*.java
if %ERRORLEVEL% neq 0 (
    echo Compilation failed!
    exit /b %ERRORLEVEL%
)

echo Compilation successful!
echo Running MainServer...
java -cp "%LIB_DIR%\*;%BIN_DIR%" com.quizapp.server.MainServer
