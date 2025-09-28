#!/bin/bash

# setup-database.sh
# Script to initialize the inventory API database

set -e

echo "🔍 Checking PostgreSQL container..."

# Check if postgres container is running
if ! docker ps | grep -q postgres-db; then
    echo "❌ PostgreSQL container 'postgres-db' is not running!"
    echo "Please start the product-api stack first or ensure PostgreSQL is available."
    exit 1
fi

echo "✅ PostgreSQL container found"

echo "🏗️  Creating database 'inventoryapi'..."

# Create the database
docker exec -it postgres-db psql -U productuser -d postgres -c "CREATE DATABASE inventoryapi;" 2>/dev/null || {
    echo "ℹ️  Database 'inventoryapi' may already exist"
}

echo "🔍 Verifying database creation..."

# Verify database exists
if docker exec -it postgres-db psql -U productuser -d postgres -c "\l" | grep -q inventoryapi; then
    echo "✅ Database 'inventoryapi' is ready!"
else
    echo "❌ Failed to create database 'inventoryapi'"
    exit 1
fi

echo "🚀 Ready to start the inventory API!"
echo ""
echo "Next steps:"
echo "1. docker-compose up --build -d"
echo "2. docker logs -f inventory-api"
echo ""
echo "API will be available at: http://localhost:8082"