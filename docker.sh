#!/bin/bash

# Resilience4j Docker Management Script
# Usage: ./docker.sh [command] [options]

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

print_usage() {
    echo "Usage: $0 [COMMAND] [OPTIONS]"
    echo ""
    echo "Commands:"
    echo "  build          Build all Docker images"
    echo "  start          Start all services"
    echo "  stop           Stop all services"
    echo "  restart        Restart all services"
    echo "  logs           View logs from all services"
    echo "  status         Show service status"
    echo "  test           Run basic health checks"
    echo "  clean          Stop services and remove containers/images"
    echo "  help           Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0 build                 # Build all images"
    echo "  $0 start                 # Start all services"
    echo "  $0 logs consumer-app     # View logs for specific service"
    echo "  $0 test                  # Test all endpoints"
}

build() {
    echo -e "${BLUE}Building Docker images...${NC}"
    docker-compose build --parallel
    echo -e "${GREEN}Build completed!${NC}"
}

start() {
    echo -e "${BLUE}Starting services...${NC}"
    docker-compose up -d
    echo -e "${GREEN}Services started!${NC}"
    echo -e "${YELLOW}Waiting for services to be healthy...${NC}"
    sleep 30
    status
}

stop() {
    echo -e "${BLUE}Stopping services...${NC}"
    docker-compose down
    echo -e "${GREEN}Services stopped!${NC}"
}

restart() {
    echo -e "${BLUE}Restarting services...${NC}"
    stop
    start
}

logs() {
    if [ -z "$1" ]; then
        echo -e "${BLUE}Showing logs from all services...${NC}"
        docker-compose logs -f
    else
        echo -e "${BLUE}Showing logs from $1...${NC}"
        docker-compose logs -f "$1"
    fi
}

status() {
    echo -e "${BLUE}Service Status:${NC}"
    docker-compose ps
    echo ""
    echo -e "${BLUE}Container Health:${NC}"
    docker ps --filter "label=com.docker.compose.project=$(basename $PWD)" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
}

test_endpoints() {
    echo -e "${BLUE}Testing service endpoints...${NC}"
    
    # Wait a bit for services to start
    echo "Waiting for services to be ready..."
    sleep 10
    
    # Test Payments API
    echo -n "Payments API health: "
    if curl -s -f http://localhost:8080/payments/health > /dev/null 2>&1; then
        echo -e "${GREEN}✓ Healthy${NC}"
    else
        echo -e "${RED}✗ Unhealthy${NC}"
    fi
    
    # Test Accounts API
    echo -n "Accounts API health: "
    if curl -s -f http://localhost:8083/accounts/health > /dev/null 2>&1; then
        echo -e "${GREEN}✓ Healthy${NC}"
    else
        echo -e "${RED}✗ Unhealthy${NC}"
    fi
    
    # Test Consumer App
    echo -n "Consumer App health: "
    if curl -s -f http://localhost:8081/consumer/health > /dev/null 2>&1; then
        echo -e "${GREEN}✓ Healthy${NC}"
    else
        echo -e "${RED}✗ Unhealthy${NC}"
    fi
    
    # Test API endpoints
    echo -e "\n${BLUE}Testing API endpoints...${NC}"
    
    echo -n "GET /payments: "
    if curl -s http://localhost:8080/payments/payments | jq . > /dev/null 2>&1; then
        echo -e "${GREEN}✓ Working${NC}"
    else
        echo -e "${RED}✗ Failed${NC}"
    fi
    
    echo -n "GET /accounts: "
    if curl -s http://localhost:8083/accounts/accounts | jq . > /dev/null 2>&1; then
        echo -e "${GREEN}✓ Working${NC}"
    else
        echo -e "${RED}✗ Failed${NC}"
    fi
    
    echo -n "GET /consumer/payments: "
    if curl -s http://localhost:8081/consumer/payments | jq . > /dev/null 2>&1; then
        echo -e "${GREEN}✓ Working${NC}"
    else
        echo -e "${RED}✗ Failed${NC}"
    fi
    
    echo -n "GET /consumer/accounts: "
    if curl -s http://localhost:8081/consumer/accounts | jq . > /dev/null 2>&1; then
        echo -e "${GREEN}✓ Working${NC}"
    else
        echo -e "${RED}✗ Failed${NC}"
    fi
}

clean() {
    echo -e "${YELLOW}Warning: This will stop all services and remove containers and images!${NC}"
    read -p "Are you sure? (y/N) " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        echo -e "${BLUE}Cleaning up...${NC}"
        docker-compose down -v --rmi all
        echo -e "${GREEN}Cleanup completed!${NC}"
    else
        echo "Cleanup cancelled."
    fi
}

# Check if docker-compose is available
if ! command -v docker-compose &> /dev/null; then
    echo -e "${RED}Error: docker-compose is not installed or not in PATH${NC}"
    exit 1
fi

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo -e "${RED}Error: Docker is not running${NC}"
    exit 1
fi

# Main command handling
case "${1:-help}" in
    build)
        build
        ;;
    start)
        start
        ;;
    stop)
        stop
        ;;
    restart)
        restart
        ;;
    logs)
        logs "$2"
        ;;
    status)
        status
        ;;
    test)
        test_endpoints
        ;;
    clean)
        clean
        ;;
    help|*)
        print_usage
        ;;
esac