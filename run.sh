#!/bin/bash

# Personal Budget Manager - Quick Start Script
# This script builds and runs the application

echo ""
echo "========================================"
echo "Personal Budget Manager - Quick Start"
echo "========================================"
echo ""

# Check if Maven wrapper is available
if [ ! -f "./mvnw" ]; then
    echo "ERROR: Maven wrapper not found!"
    echo "Please run this script from the project root directory."
    exit 1
fi

# Clean and build
echo "Cleaning and building the project..."
./mvnw clean install -DskipTests

if [ $? -ne 0 ]; then
    echo ""
    echo "ERROR: Build failed!"
    echo "Please check the errors above and refer to README.md for troubleshooting."
    exit 1
fi

echo ""
echo "========================================"
echo "Build Successful!"
echo "========================================"
echo ""
echo "Starting the application..."
echo "The app will be available at: http://localhost:8080"
echo "Press Ctrl+C to stop the application."
echo ""

# Run the application
./mvnw spring-boot:run
