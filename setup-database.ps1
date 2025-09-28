# setup-database.ps1
# PowerShell script to initialize the inventory API database

Write-Host "🔍 Checking PostgreSQL container..." -ForegroundColor Cyan

# Check if postgres container is running
$postgresContainer = docker ps --filter "name=postgres-db" --format "table {{.Names}}" | Select-String "postgres-db"

if (-not $postgresContainer) {
    Write-Host "❌ PostgreSQL container 'postgres-db' is not running!" -ForegroundColor Red
    Write-Host "Please start the product-api stack first or ensure PostgreSQL is available." -ForegroundColor Yellow
    exit 1
}

Write-Host "✅ PostgreSQL container found" -ForegroundColor Green

Write-Host "🏗️  Creating database 'inventoryapi'..." -ForegroundColor Cyan

# Create the database
try {
    docker exec -it postgres-db psql -U productuser -d postgres -c "CREATE DATABASE inventoryapi;" 2>$null
    Write-Host "✅ Database 'inventoryapi' created successfully!" -ForegroundColor Green
} catch {
    Write-Host "ℹ️  Database 'inventoryapi' may already exist" -ForegroundColor Yellow
}

Write-Host "🔍 Verifying database creation..." -ForegroundColor Cyan

# Verify database exists
$dbExists = docker exec -it postgres-db psql -U productuser -d postgres -c "\l" | Select-String "inventoryapi"

if ($dbExists) {
    Write-Host "✅ Database 'inventoryapi' is ready!" -ForegroundColor Green
} else {
    Write-Host "❌ Failed to create database 'inventoryapi'" -ForegroundColor Red
    exit 1
}

Write-Host "🚀 Ready to start the inventory API!" -ForegroundColor Green
Write-Host ""
Write-Host "Next steps:" -ForegroundColor Cyan
Write-Host "1. docker-compose up --build -d" -ForegroundColor White
Write-Host "2. docker logs -f inventory-api" -ForegroundColor White
Write-Host ""
Write-Host "API will be available at: http://localhost:8082" -ForegroundColor Yellow