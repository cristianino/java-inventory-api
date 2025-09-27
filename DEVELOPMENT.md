# Development Guide

## Quick Start

### Option 1: Docker Compose (Recommended)

Start all services with a single command:

```bash
# Start all services (inventory-api, postgres, mock products service)
docker-compose up --build

# Or run in background
docker-compose up --build -d
```

The API will be available at: http://localhost:8080

### Option 2: Local Development

1. Start PostgreSQL:
```bash
docker-compose up postgres -d
```

2. Start the application:
```bash
mvn spring-boot:run
```

## Testing the API

### Using the Test Script

Run the comprehensive test script:

```bash
./test-api.sh
```

### Manual Testing

1. **Health Check**:
```bash
curl http://localhost:8080/actuator/health
```

2. **Create Inventory**:
```bash
curl -X POST http://localhost:8080/api/inventory \
  -H "Content-Type: application/json" \
  -d '{"productId": "product-123", "quantity": 100}'
```

3. **Get All Inventory**:
```bash
curl http://localhost:8080/api/inventory
```

4. **API Documentation**:
Visit: http://localhost:8080/swagger-ui/index.html

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                    REST API (JSON:API)                     │
│                 InventoryController                         │
└─────────────────────────┬───────────────────────────────────┘
                          │
┌─────────────────────────▼───────────────────────────────────┐
│                  Application Layer                          │
│     CreateInventoryUseCase, GetInventoryUseCase...         │
└─────────────────────────┬───────────────────────────────────┘
                          │
┌─────────────────────────▼───────────────────────────────────┐
│                    Domain Layer                             │
│        Inventory (Entity), InventoryRepository (Port)      │
└─────────────────────────┬───────────────────────────────────┘
                          │
┌─────────────────────────▼───────────────────────────────────┐
│                Infrastructure Layer                         │
│  InventoryRepositoryImpl, ProductServiceImpl (Adapters)    │
└─────────────────────────────────────────────────────────────┘
```

## Development Workflow

### 1. Build and Test

```bash
# Clean build
mvn clean compile

# Run tests
mvn test

# Run integration tests
mvn verify

# Package application
mvn package
```

### 2. Code Quality

The project follows:
- **Hexagonal Architecture** principles
- **Clean Code** practices
- **SOLID** principles
- **Domain-Driven Design** concepts

### 3. Database Migrations

Flyway migrations are located in `src/main/resources/db/migration/`:

- `V1__Create_inventory_table.sql` - Initial inventory table

### 4. Configuration

#### Environment Variables

- `SPRING_DATASOURCE_URL` - PostgreSQL connection URL
- `SPRING_DATASOURCE_USERNAME` - Database username
- `SPRING_DATASOURCE_PASSWORD` - Database password
- `EXTERNAL_PRODUCTS_SERVICE_BASE_URL` - Products service URL
- `PRODUCTS_API_KEY` - API key for products service

#### Profiles

- `default` - Local development
- `docker` - Docker deployment  
- `test` - Testing environment

## External Services

### Products Service Integration

The inventory service integrates with an external products service:

- **Authentication**: X-API-Key header
- **Resilience**: Circuit breaker, retries, timeouts
- **Mock Service**: Included in docker-compose for testing

## Monitoring and Observability

### Health Checks

- `/actuator/health` - Application health
- `/actuator/health/db` - Database connectivity

### Logging

- **Local Development**: Console logs
- **Production**: JSON structured logs (for Loki/Grafana)

### Metrics

- `/actuator/metrics` - Application metrics
- `/actuator/prometheus` - Prometheus metrics

## Docker

### Building the Image

```bash
docker build -t inventory-api .
```

### Multi-stage Build

The Dockerfile uses multi-stage builds for optimal image size:
1. **Builder stage**: Compile and package the application
2. **Runtime stage**: Run the application with minimal dependencies

## Testing Strategy

### Unit Tests
- Domain model validation
- Business logic testing
- Located in `src/test/java`

### Integration Tests
- Database integration with Testcontainers
- Repository layer testing
- Full application context testing

### API Testing
- REST endpoint testing
- JSON:API compliance validation
- Error handling verification

## Troubleshooting

### Common Issues

1. **Port conflicts**: Ensure ports 8080, 5432, 8081 are available
2. **Database connection**: Check PostgreSQL is running
3. **External service**: Verify products service mock is available

### Debug Mode

Run with debug logging:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--logging.level.com.inventory=DEBUG"
```

### Health Checks

Monitor service health:

```bash
# Application health
curl http://localhost:8080/actuator/health

# Database health  
curl http://localhost:8080/actuator/health/db
```