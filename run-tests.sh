#!/bin/bash

# Test execution script for inventory API

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

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

# Function to create a test container
create_test_container() {
    print_status "Creating test container with Maven..."
    
    docker run --rm \
        -v "$(pwd)":/workspace \
        -w /workspace \
        --network host \
        maven:3.9.6-eclipse-temurin-21-alpine \
        sh -c "
            echo 'Installing dependencies...' && \
            mvn clean compile test-compile && \
            echo 'Running tests with coverage...' && \
            mvn test jacoco:report && \
            echo 'Coverage report generated!' && \
            echo 'Test results:' && \
            find target -name 'TEST-*.xml' -exec echo 'Found test result: {}' \; && \
            echo 'Coverage reports:' && \
            find target -name 'jacoco.xml' -exec echo 'Found coverage report: {}' \; && \
            find target -name 'index.html' -path '*/site/jacoco/*' -exec echo 'Found HTML coverage report: {}' \;
        "
}

# Function to run tests with coverage
run_tests_with_coverage() {
    print_status "Running tests with JaCoCo coverage reporting..."
    
    if create_test_container; then
        print_success "Tests completed successfully!"
        
        # Show coverage summary if available
        if [ -f "target/site/jacoco/index.html" ]; then
            print_success "Coverage report generated at: target/site/jacoco/index.html"
            print_status "You can open this file in a web browser to view detailed coverage"
        fi
        
        # Look for coverage percentage in jacoco.csv if available
        if [ -f "target/site/jacoco/jacoco.csv" ]; then
            print_status "Coverage summary from jacoco.csv:"
            head -n 5 target/site/jacoco/jacoco.csv || true
        fi
        
        return 0
    else
        print_error "Tests failed!"
        return 1
    fi
}

# Function to just compile and validate
compile_only() {
    print_status "Compiling project to check for syntax errors..."
    
    docker run --rm \
        -v "$(pwd)":/workspace \
        -w /workspace \
        maven:3.9.6-eclipse-temurin-21-alpine \
        sh -c "mvn clean compile test-compile"
}

# Function to run specific test class
run_specific_test() {
    local test_class=$1
    print_status "Running specific test class: $test_class"
    
    docker run --rm \
        -v "$(pwd)":/workspace \
        -w /workspace \
        --network host \
        maven:3.9.6-eclipse-temurin-21-alpine \
        sh -c "mvn clean test -Dtest=$test_class"
}

# Function to show test summary
show_test_summary() {
    print_status "Test Summary:"
    echo ""
    
    if [ -d "target/surefire-reports" ]; then
        local total_tests=$(find target/surefire-reports -name "TEST-*.xml" -exec grep -h "tests=" {} \; | sed 's/.*tests="\([^"]*\)".*/\1/' | awk '{sum+=$1} END {print sum}')
        local failed_tests=$(find target/surefire-reports -name "TEST-*.xml" -exec grep -h "failures=" {} \; | sed 's/.*failures="\([^"]*\)".*/\1/' | awk '{sum+=$1} END {print sum}')
        local error_tests=$(find target/surefire-reports -name "TEST-*.xml" -exec grep -h "errors=" {} \; | sed 's/.*errors="\([^"]*\)".*/\1/' | awk '{sum+=$1} END {print sum}')
        
        echo "Total Tests: ${total_tests:-0}"
        echo "Failed Tests: ${failed_tests:-0}"
        echo "Error Tests: ${error_tests:-0}"
        echo "Passed Tests: $((${total_tests:-0} - ${failed_tests:-0} - ${error_tests:-0}))"
        
        if [ "${failed_tests:-0}" -gt 0 ] || [ "${error_tests:-0}" -gt 0 ]; then
            print_warning "Some tests failed. Check target/surefire-reports for details."
        else
            print_success "All tests passed!"
        fi
    else
        print_warning "No test reports found in target/surefire-reports"
    fi
}

# Main script logic
case "${1:-all}" in
    all|test|tests)
        if run_tests_with_coverage; then
            show_test_summary
        fi
        ;;
    compile)
        compile_only
        ;;
    class)
        if [ -z "$2" ]; then
            echo "Usage: $0 class <ClassName>"
            echo "Example: $0 class InventoryTest"
            exit 1
        fi
        run_specific_test "$2"
        ;;
    summary)
        show_test_summary
        ;;
    clean)
        print_status "Cleaning target directory..."
        rm -rf target/
        print_success "Clean completed"
        ;;
    help|*)
        echo "Inventory API Test Runner"
        echo ""
        echo "Usage: $0 {all|compile|class|summary|clean|help}"
        echo ""
        echo "Commands:"
        echo "  all      - Run all tests with coverage (default)"
        echo "  compile  - Just compile to check syntax"
        echo "  class    - Run specific test class"
        echo "  summary  - Show test summary from previous run"
        echo "  clean    - Clean target directory"
        echo "  help     - Show this help message"
        echo ""
        echo "Examples:"
        echo "  $0 all"
        echo "  $0 class InventoryTest"
        echo "  $0 class 'com.inventory.domain.model.*'"
        ;;
esac