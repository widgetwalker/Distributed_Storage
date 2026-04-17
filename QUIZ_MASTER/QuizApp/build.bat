@echo off
REM Build script for Quiz Master Application
REM Compiles all Java files and runs the application

echo.
echo ======================================
echo   Champion's Quiz Master Build Script
echo ======================================
echo.

REM Create bin directory if it doesn't exist
if not exist bin mkdir bin

echo [1/3] Compiling model classes...
javac -d bin src\com\quizapp\model\*.java
if errorlevel 1 goto error

echo [2/3] Compiling database layer...
javac -d bin -cp bin src\com\quizapp\db\*.java
if errorlevel 1 goto error

echo [3/3] Compiling controller and GUI...
javac -d bin -cp bin src\com\quizapp\controller\*.java
javac -d bin -cp bin src\com\quizapp\gui\*.java
if errorlevel 1 goto error

echo.
echo ✓ Compilation successful!
echo.
echo Running application...
echo.

cd bin
java -cp . com.quizapp.gui.MainWindow
cd ..

goto end

:error
echo.
echo ✗ Compilation failed!
echo Please check the error messages above.
pause

:end
