#!/bin/bash

echo "Starting Personal Finance Manager..."
echo

# Check if Java is available
if ! command -v java &> /dev/null; then
    echo "Error: Java is not installed or not in PATH"
    echo "Please install Java JDK 17+ and add it to your PATH"
    exit 1
fi

# Check if Maven is available
if ! command -v mvn &> /dev/null; then
    echo "Error: Maven is not installed or not in PATH"
    echo "Please install Maven and add it to your PATH"
    exit 1
fi

echo "Building and running the application..."
echo

# Compile and run with JavaFX
mvn clean compile javafx:run

if [ $? -ne 0 ]; then
    echo
    echo "Error: Failed to run the application"
    echo "Make sure JavaFX is properly configured"
    exit 1
fi

echo
echo "Application closed."
