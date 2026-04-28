#!/bin/bash
# Script to run Nboard application with environment variables from .env file

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${GREEN}=========================================="
echo "Starting Nboard Application"
echo -e "==========================================${NC}"
echo ""

# Check if .env file exists
if [ ! -f .env ]; then
    echo -e "${YELLOW}⚠Warning: .env file not found!${NC}"
    echo "Creating from .env.example..."

    if [ -f .env.example ]; then
        cp .env.example .env
        echo -e "${YELLOW}📝 Please edit .env file with your actual credentials${NC}"
        echo "Run this script again after configuring .env"
        exit 1
    else
        echo -e "${RED} Error: .env.example not found!${NC}"
        exit 1
    fi
fi

echo -e "${GREEN}✓ Loading environment variables from .env${NC}"

# Export variables from .env (skip comments and empty lines)
set -a
source <(cat .env | sed -e '/^#/d;/^\s*$/d' -e "s/'/'\\\''/g" -e "s/=\(.*\)/=\"\1\"/g")
set +a

echo -e "${GREEN}✓ Environment variables loaded${NC}"
echo ""

# Check if MySQL is configured
if [[ $DATABASE_URL == *"mysql"* ]]; then
    echo -e "${GREEN}✓ MySQL database configured${NC}"
    echo "  Host: $(echo $DATABASE_URL | grep -oP '(?<=\/\/).*?(?=:)')"
else
    echo -e "${YELLOW} Using default database configuration${NC}"
fi

echo ""
echo -e "${GREEN} Starting application...${NC}"
echo ""

# Run the application
./mvnw spring-boot:run

