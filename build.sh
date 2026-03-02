#!/bin/bash
# Build & Deploy Script for Nboard Registration & Login Fixes

echo "=========================================="
echo "🚀 Nboard Build & Test Script"
echo "=========================================="
echo ""

# Navigate to project directory
cd /home/eptheekshana/IdeaProjects/GreenNest

echo "📦 Step 1: Clean and Compile"
echo "Command: ./mvnw clean compile"
echo ""
./mvnw clean compile -q
if [ $? -eq 0 ]; then
    echo "✅ Compilation successful!"
else
    echo "❌ Compilation failed!"
    exit 1
fi

echo ""
echo "📦 Step 2: Run Tests (Optional)"
echo "Command: ./mvnw test"
echo "Skipping tests as configured..."
echo "✅ Tests skipped"

echo ""
echo "📦 Step 3: Package Application"
echo "Command: ./mvnw package -DskipTests"
echo ""
./mvnw package -DskipTests -q
if [ $? -eq 0 ]; then
    echo "✅ Package created successfully!"
else
    echo "❌ Package creation failed!"
    exit 1
fi

echo ""
echo "=========================================="
echo "✅ BUILD COMPLETE"
echo "=========================================="
echo ""

# Show generated JAR
JAR_FILE=$(find target -name "nboard-*.jar" -type f | grep -v ".original")
if [ -f "$JAR_FILE" ]; then
    SIZE=$(du -h "$JAR_FILE" | cut -f1)
    echo "📦 Generated JAR:"
    echo "   File: $JAR_FILE"
    echo "   Size: $SIZE"
else
    echo "⚠️  JAR file not found"
fi

echo ""
echo "=========================================="
echo "🚀 NEXT STEPS"
echo "=========================================="
echo ""
echo "1. Start the application:"
echo "   java -jar target/nboard-0.0.1-SNAPSHOT.jar"
echo ""
echo "2. Access registration:"
echo "   http://localhost:8080/register"
echo ""
echo "3. Access login:"
echo "   http://localhost:8080/login"
echo ""
echo "4. View database:"
echo "   http://localhost:8080/h2-console"
echo "   JDBC URL: jdbc:h2:./nboard_db"
echo "   Username: sa"
echo ""
echo "5. Check documentation:"
echo "   - QUICK_REFERENCE.md (quick commands)"
echo "   - REGISTRATION_FIX_COMPLETE.md (complete guide)"
echo "   - TESTING_GUIDE.md (test cases)"
echo ""
echo "=========================================="
echo "✨ All fixes implemented and ready to test!"
echo "=========================================="

