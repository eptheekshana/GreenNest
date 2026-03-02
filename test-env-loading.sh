#!/bin/bash
# Test script to verify .env file is loaded correctly

echo "=========================================="
echo "Testing .env File Loading"
echo "=========================================="
echo ""

# Check if .env exists
if [ ! -f .env ]; then
    echo "ERROR: .env file not found!"
    exit 1
fi

echo "✓ .env file found"
echo ""

# Parse .env and show what will be loaded
echo "📋 Environment variables in .env:"
echo "---"
while IFS= read -r line; do
    # Skip comments and empty lines
    if [[ ! "$line" =~ ^#.* ]] && [[ ! -z "$line" ]]; then
        key=$(echo "$line" | cut -d'=' -f1)
        value=$(echo "$line" | cut -d'=' -f2-)

        # Mask sensitive values
        if [[ "$key" == *"PASSWORD"* ]] || [[ "$key" == *"SECRET"* ]] || [[ "$key" == *"KEY"* ]]; then
            echo "  $key=***HIDDEN***"
        else
            echo "  $key=$value"
        fi
    fi
done < .env
echo ""

echo "🚀 Starting application..."
echo "Watch for this line: '✓ Loaded X environment variables from .env file'"
echo ""
echo "=========================================="
echo ""

# Run the application
./mvnw spring-boot:run

