#!/bin/bash

# Inventory API Development Script

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to check if Product API is running
check_product_api() {
    print_status "Checking if Product API is running..."
    if curl -s --fail "http://localhost:8080/actuator/health" > /dev/null 2>&1; then
        print_success "Product API is running on port 8080"
        return 0
    else
        print_error "Product API is not running on port 8080"
        print_warning "Please start the Product API first from /home/cristian/Documentos/java-product-api"
        return 1
    fi
}

# Function to build and start services
start_services() {
    print_status "Building and starting Inventory API services..."
    
    # Build and start services
    docker-compose -f docker-compose.dev.yml up --build -d
    
    print_status "Waiting for services to be ready..."
    sleep 10
    
    # Check if services are running
    if docker-compose -f docker-compose.dev.yml ps | grep -q "Up"; then
        print_success "Services started successfully!"
        print_status "Services available at:"
        echo "  - Inventory API: http://localhost:8082"
        echo "  - Inventory API Health: http://localhost:8082/actuator/health"
        echo "  - Inventory API Swagger: http://localhost:8082/swagger-ui.html"
        echo "  - Database Admin (Adminer): http://localhost:8083"
        echo "  - Debug Port: 5005"
        echo ""
        print_status "Database connection:"
        echo "  - Host: localhost"
        echo "  - Port: 5433"
        echo "  - Database: inventory_db"
        echo "  - Username: inventory_user"
        echo "  - Password: inventory_pass"
    else
        print_error "Failed to start services"
        docker-compose -f docker-compose.dev.yml logs
        exit 1
    fi
}

# Function to stop services
stop_services() {
    print_status "Stopping Inventory API services..."
    docker-compose -f docker-compose.dev.yml down
    print_success "Services stopped"
}

# Function to view logs
view_logs() {
    print_status "Viewing logs for Inventory API..."
    docker-compose -f docker-compose.dev.yml logs -f inventory-api-dev
}

# Function to restart services
restart_services() {
    print_status "Restarting Inventory API services..."
    docker-compose -f docker-compose.dev.yml restart
    print_success "Services restarted"
}

# Function to clean up
clean_services() {
    print_status "Cleaning up Inventory API services and volumes..."
    docker-compose -f docker-compose.dev.yml down -v --rmi all
    print_success "Cleanup completed"
}

# Function to test API connection
test_connection() {
    print_status "Testing API connectivity..."
    
    # Test Product API
    if curl -s -H "X-API-Key: your-secret-api-key-here" "http://localhost:8080/api/products/1" > /dev/null; then
        print_success "Product API connection: OK"
    else
        print_error "Product API connection: FAILED"
    fi
    
    # Test Inventory API
    sleep 5
    if curl -s "http://localhost:8082/actuator/health" > /dev/null; then
        print_success "Inventory API health check: OK"
    else
        print_error "Inventory API health check: FAILED"
    fi
}

# Main script logic
case "${1:-help}" in
    start)
        if check_product_api; then
            start_services
            test_connection
        else
            exit 1
        fi
        ;;
    stop)
        stop_services
        ;;
    restart)
        restart_services
        ;;
    logs)
        view_logs
        ;;
    clean)
        clean_services
        ;;
    test)
        test_connection
        ;;
    status)
        print_status "Checking service status..."
        docker-compose -f docker-compose.dev.yml ps
        ;;
    help|*)
        echo "Inventory API Development Helper"
        echo ""
        echo "Usage: $0 {start|stop|restart|logs|clean|test|status|help}"
        echo ""
        echo "Commands:"
        echo "  start   - Build and start all services"
        echo "  stop    - Stop all services"
        echo "  restart - Restart all services"
        echo "  logs    - View live logs"
        echo "  clean   - Stop services and remove volumes/images"
        echo "  test    - Test API connectivity"
        echo "  status  - Show service status"
        echo "  help    - Show this help message"
        echo ""
        echo "Prerequisites:"
        echo "  - Product API must be running on port 8080"
        echo "  - Docker and Docker Compose must be installed"
        ;;
esac