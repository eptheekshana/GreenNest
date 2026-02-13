#!/bin/bash
# Quick verification script for database configuration

echo "======================================"
echo "🔍 Nboard Configuration Verification"
echo "======================================"
echo ""

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

ERRORS=0

# Check .env file
echo -n "1. Checking .env file... "
if [ -f .env ]; then
    echo -e "${GREEN}✓ Found${NC}"
else
    echo -e "${RED}✗ Missing${NC}"
    ERRORS=$((ERRORS+1))
fi

# Check .env.example
echo -n "2. Checking .env.example... "
if [ -f .env.example ]; then
    echo -e "${GREEN}✓ Found${NC}"
else
    echo -e "${RED}✗ Missing${NC}"
    ERRORS=$((ERRORS+1))
fi

# Check if .env is in .gitignore
echo -n "3. Checking if .env is ignored by git... "
if git check-ignore .env > /dev/null 2>&1; then
    echo -e "${GREEN}✓ Properly ignored${NC}"
else
    echo -e "${RED}✗ NOT ignored (SECURITY RISK!)${NC}"
    ERRORS=$((ERRORS+1))
fi

# Check run.sh
echo -n "4. Checking run.sh script... "
if [ -f run.sh ] && [ -x run.sh ]; then
    echo -e "${GREEN}✓ Found and executable${NC}"
else
    echo -e "${RED}✗ Missing or not executable${NC}"
    ERRORS=$((ERRORS+1))
fi

# Check application.properties
echo -n "5. Checking application.properties... "
if grep -q '${DATABASE_URL' src/main/resources/application.properties; then
    echo -e "${GREEN}✓ Uses environment variables${NC}"
else
    echo -e "${RED}✗ May have hardcoded values${NC}"
    ERRORS=$((ERRORS+1))
fi

# Check for hardcoded credentials in application.properties
echo -n "6. Checking for hardcoded credentials... "
if grep -iE 'password.*=.*[^$]' src/main/resources/application.properties | grep -v '${' > /dev/null; then
    echo -e "${RED}✗ Found hardcoded credentials${NC}"
    ERRORS=$((ERRORS+1))
else
    echo -e "${GREEN}✓ No hardcoded credentials${NC}"
fi

# Check if Maven can compile
echo -n "7. Testing Maven compilation... "
if ./mvnw compile -q -DskipTests > /dev/null 2>&1; then
    echo -e "${GREEN}✓ Compiles successfully${NC}"
else
    echo -e "${RED}✗ Compilation failed${NC}"
    ERRORS=$((ERRORS+1))
fi

# Load .env and check required variables
echo -n "8. Checking required environment variables... "
if [ -f .env ]; then
    source .env
    MISSING=""
    [ -z "$DATABASE_URL" ] && MISSING="${MISSING}DATABASE_URL "
    [ -z "$DATABASE_USERNAME" ] && MISSING="${MISSING}DATABASE_USERNAME "
    [ -z "$DATABASE_PASSWORD" ] && MISSING="${MISSING}DATABASE_PASSWORD "
    [ -z "$DO_SPACES_KEY" ] && MISSING="${MISSING}DO_SPACES_KEY "
    [ -z "$DO_SPACES_SECRET" ] && MISSING="${MISSING}DO_SPACES_SECRET "

    if [ -z "$MISSING" ]; then
        echo -e "${GREEN}✓ All required variables set${NC}"
    else
        echo -e "${YELLOW}⚠ Missing: $MISSING${NC}"
    fi
else
    echo -e "${RED}✗ .env file not found${NC}"
    ERRORS=$((ERRORS+1))
fi

echo ""
echo "======================================"
if [ $ERRORS -eq 0 ]; then
    echo -e "${GREEN}✅ All checks passed! Ready to deploy.${NC}"
    echo ""
    echo "To run the application:"
    echo "  ./run.sh"
    echo ""
    echo "To commit changes:"
    echo "  git add .env.example .gitignore pom.xml run.sh"
    echo "  git add src/ SECURITY.md README.md GIT_COMMIT_GUIDE.md"
    echo "  git commit -m 'feat: Secure database configuration'"
    exit 0
else
    echo -e "${RED}❌ $ERRORS check(s) failed. Please fix the issues above.${NC}"
    exit 1
fi

