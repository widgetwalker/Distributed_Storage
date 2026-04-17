#!/bin/bash
# Build script for Quiz Master Application
# Compiles all Java files and runs the application

echo ""
echo "======================================"
echo "  Champion's Quiz Master Build Script"
echo "======================================"
echo ""

# Create bin directory if it doesn't exist
mkdir -p bin

echo "[1/3] Compiling model classes..."
javac -d bin src/com/quizapp/model/*.java
if [ $? -ne 0 ]; then
    echo "✗ Compilation failed!"
    exit 1
fi

echo "[2/3] Compiling database layer..."
javac -d bin -cp bin src/com/quizapp/db/*.java
if [ $? -ne 0 ]; then
    echo "✗ Compilation failed!"
    exit 1
fi

echo "[3/3] Compiling controller and GUI..."
javac -d bin -cp bin src/com/quizapp/controller/*.java
javac -d bin -cp bin src/com/quizapp/gui/*.java
if [ $? -ne 0 ]; then
    echo "✗ Compilation failed!"
    exit 1
fi

echo ""
echo "✓ Compilation successful!"
echo ""
echo "Running application..."
echo ""

cd bin
java -cp . com.quizapp.gui.MainWindow
cd ..
