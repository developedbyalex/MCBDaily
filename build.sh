#!/bin/bash

echo "Building MCBDaily Plugin..."
echo

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "ERROR: Maven is not installed or not in PATH"
    echo "Please install Maven and try again"
    echo "Download from: https://maven.apache.org/download.cgi"
    exit 1
fi

echo "Cleaning previous build..."
mvn clean

echo "Compiling and packaging..."
mvn compile package

if [ $? -eq 0 ]; then
    echo
    echo "SUCCESS! Plugin built successfully!"
    echo "JAR file location: target/MCBDaily-1.0.0.jar"
    echo
    echo "You can now:"
    echo "1. Copy the JAR to your server's plugins folder"
    echo "2. Restart your server"
    echo "3. Configure rewards in plugins/MCBDaily/config.yml"
else
    echo
    echo "ERROR: Build failed! Check the errors above."
fi

echo 