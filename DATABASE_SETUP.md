# Database Setup Guide

## Overview

The Java Inventory API requires a PostgreSQL database to be properly initialized before the application can start. This guide covers the essential database setup process, including database creation and automatic data seeding.

## 🚨 Critical Database Setup Steps

### Why This Step is Important

**The database must be created manually before running the application for the first time.** This is a one-time setup requirement because:

1. **Spring Boot/Flyway Limitation**: Spring Boot attempts to connect to the target database during startup, before any custom initialization code can run
2. **Docker Network Dependencies**: The application needs to establish a connection to verify database connectivity
3. **Migration Execution**: Flyway requires an existing database to execute migration scripts

**Without proper database setup, the application will fail to start with the error:**
```
FATAL: database "inventoryapi" does not exist
```

### Prerequisites

- PostgreSQL container running (usually via product-api stack)
- Network connectivity between containers
- Correct database credentials configured

## 🔧 Setup Instructions

### Step 1: Verify PostgreSQL Container

Ensure the PostgreSQL container is running:

```bash
docker ps | grep postgres
```

Expected output should show a running PostgreSQL container named `postgres-db`.

### Step 2: Create the Database (REQUIRED)

**This step must be completed before starting the inventory API:**

```bash
# Connect to PostgreSQL and create the database
docker exec -it postgres-db psql -U productuser -d postgres -c "CREATE DATABASE inventoryapi;"
```

**Expected output:**
```
CREATE DATABASE
```

### Step 3: Verify Database Creation

```bash
# List all databases to confirm creation
docker exec -it postgres-db psql -U productuser -d postgres -c "\l"
```

You should see `inventoryapi` in the database list.

### Step 4: Start the Application

Now you can safely start the inventory API:

```bash
docker-compose up -d
```

## 🌱 Automatic Data Seeding

Once the database exists, the application will automatically:

1. **Execute Flyway Migrations**:
   - `V1__Create_inventory_table.sql` - Creates the inventory table structure
   - `V2__Insert_initial_inventory_data.sql` - Populates initial sample data

2. **Run Database Seeder**:
   - Checks if data already exists
   - Logs the seeding process
   - Prevents duplicate data insertion

### Sample Data Inserted

The seeder automatically creates 5 inventory records:

| Product ID | Quantity |
|------------|----------|
| PROD-001   | 100      |
| PROD-002   | 50       |
| PROD-003   | 75       |
| PROD-004   | 200      |
| PROD-005   | 25       |

## 🔄 Verification Steps

### Check Application Logs

Monitor the startup process:

```bash
docker logs -f inventory-api
```

**Look for these success indicators:**

```
✅ Successfully applied 2 migrations to schema "public", now at version v2
🌱 Checking if database seeding is needed...
📊 Database is empty, seeding will be handled by Flyway migration V2
✅ Initial inventory data will be inserted by migration script
🎯 Database seeding check completed successfully
```

### Verify Data Creation

```bash
# Check that data was inserted correctly
docker exec -it postgres-db psql -U productuser -d inventoryapi -c "SELECT product_id, quantity FROM inventory ORDER BY product_id;"
```

**Expected output:**
```
 product_id | quantity 
------------+----------
 PROD-001   |      100
 PROD-002   |       50
 PROD-003   |       75
 PROD-004   |      200
 PROD-005   |       25
(5 rows)
```

### Test API Endpoints

```bash
# Health check
curl http://localhost:8082/actuator/health

# Get all inventory (requires API key)
curl -H "X-API-Key: your-secret-api-key-here" http://localhost:8082/api/inventory
```

## 🚨 Troubleshooting

### Database Connection Issues

**Problem**: `FATAL: database "inventoryapi" does not exist`

**Solution**: Follow Step 2 above to create the database manually.

**Problem**: `FATAL: password authentication failed for user "dev"`

**Solution**: Verify database credentials in `.env` file:
```env
SPRING_DATASOURCE_USERNAME=productuser
SPRING_DATASOURCE_PASSWORD=dev_password_123
```

### Network Issues

**Problem**: `java-product-api-postgres-dev: Name does not resolve`

**Solution**: Ensure the application uses the correct PostgreSQL container name:
```env
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres-db:5432/inventoryapi
```

### Seeding Issues

**Problem**: No data appears after startup

**Solution**: 
1. Check application logs for migration errors
2. Verify database connectivity
3. Ensure `DATABASE_SEEDING_ENABLED=true` in `.env`

## 🔧 Environment Configuration

### Key Environment Variables

```env
# Database Configuration
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres-db:5432/inventoryapi
SPRING_DATASOURCE_USERNAME=productuser
SPRING_DATASOURCE_PASSWORD=dev_password_123

# Seeding Control
DATABASE_SEEDING_ENABLED=true

# API Security
APP_API_KEY=your-secret-api-key-here
PRODUCTS_API_KEY=dev_api_key_for_local_development_12345
```

## 📚 Additional Notes

### Re-running from Scratch

To test the seeder from a clean state:

```bash
# 1. Stop the application
docker-compose down

# 2. Drop the database
docker exec -it postgres-db psql -U productuser -d postgres -c "DROP DATABASE inventoryapi;"

# 3. Recreate the database
docker exec -it postgres-db psql -U productuser -d postgres -c "CREATE DATABASE inventoryapi;"

# 4. Start the application
docker-compose up -d
```

### Production Considerations

- **Database Creation**: In production, databases should be created through infrastructure automation (Terraform, CloudFormation, etc.)
- **Credentials**: Use secure credential management systems
- **Seeding**: Consider disabling automatic seeding in production environments
- **Migrations**: Always backup databases before running migrations in production

## ✅ Success Checklist

- [ ] PostgreSQL container is running
- [ ] Database `inventoryapi` has been created manually
- [ ] Environment variables are properly configured
- [ ] Application starts without database connection errors
- [ ] Flyway migrations execute successfully
- [ ] 5 sample inventory records are created
- [ ] API endpoints respond correctly
- [ ] Health check returns status UP

---

**⚠️ Remember: The manual database creation step is critical and must be completed before the first application startup.**