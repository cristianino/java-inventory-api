# java-inventory-api

Spring Boot 3 microservice for inventory management using Hexagonal Architecture, JSON:API, PostgreSQL, API key security, and Docker.

## Features

- **Hexagonal Architecture**: Clean separation of concerns with Domain, Application, and Infrastructure layers
- **CRUD Operations**: Complete inventory management by productId and quantity
- **JSON:API Compliance**: REST endpoints following JSON:API specification
- **External Integration**: WebClient integration with products-service using X-API-Key authentication
- **Resilience Patterns**: Circuit breaker, retries, and timeouts using Resilience4j
- **Database**: PostgreSQL with JPA and Flyway migrations
- **Documentation**: Swagger/OpenAPI integration
- **Monitoring**: Actuator health endpoints
- **Structured Logging**: JSON logs for Loki/Grafana compatibility
- **Testing**: Unit and integration tests with Testcontainers
- **Containerization**: Docker and docker-compose setup

## Technology Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **PostgreSQL** with JPA/Hibernate
- **Flyway** for database migrations
- **JSON:API** for API responses
- **Resilience4j** for circuit breaker and retry patterns
- **WebClient** for external service integration
- **Testcontainers** for integration testing
- **Docker** for containerization

## API Endpoints

### Inventory Management

- `POST /api/inventory` - Create new inventory entry
- `GET /api/inventory` - Get all inventory entries
- `GET /api/inventory/{id}` - Get inventory by ID
- `GET /api/inventory/product/{productId}` - Get inventory by product ID
- `PUT /api/inventory/{id}/quantity` - Update inventory quantity
- `PUT /api/inventory/product/{productId}/quantity` - Update inventory quantity by product ID
- `DELETE /api/inventory/{id}` - Delete inventory entry
- `DELETE /api/inventory/product/{productId}` - Delete inventory by product ID

### Health & Documentation

- `GET /actuator/health` - Health check endpoint
- `GET /swagger-ui/index.html` - Swagger UI documentation

## Running the Application

### Prerequisites

- Java 17+
- Docker and Docker Compose
- Maven 3.6+

### Local Development

1. Start PostgreSQL database:
```bash
docker-compose up postgres -d
```

2. Run the application:
```bash
mvn spring-boot:run
```

3. Access the application:
   - API: http://localhost:8080/api/inventory
   - Swagger UI: http://localhost:8080/swagger-ui/index.html
   - Health: http://localhost:8080/actuator/health

### Docker Deployment

1. Build and run with docker-compose:
```bash
docker-compose up --build
```

## Configuration

### Environment Variables

- `SPRING_DATASOURCE_URL` - PostgreSQL database URL
- `SPRING_DATASOURCE_USERNAME` - Database username
- `SPRING_DATASOURCE_PASSWORD` - Database password
- `EXTERNAL_PRODUCTS_SERVICE_BASE_URL` - Products service base URL
- `PRODUCTS_API_KEY` - API key for products service authentication
- `APP_API_KEY` - API key for incoming requests authentication

### Profiles

- `default` - Local development
- `docker` - Docker deployment
- `test` - Testing environment

## Testing

Run unit tests:
```bash
mvn test
```

Run integration tests:
```bash
mvn verify
```

## Architecture

### Hexagonal Architecture Layers

1. **Domain Layer** (`com.inventory.domain`)
   - Core business logic and entities
   - Domain ports (interfaces)

2. **Application Layer** (`com.inventory.application`)
   - Use cases and business workflows
   - Orchestrates domain objects

3. **Infrastructure Layer** (`com.inventory.infrastructure`)
   - External concerns (database, REST API, external services)
   - Adapters implementing domain ports

### External Dependencies

- **Products Service**: External service for product validation
- **PostgreSQL**: Primary data storage
- **Monitoring Stack**: Actuator endpoints for health checks

## JSON:API Response Format

All API responses follow the JSON:API specification:

```json
{
  "data": {
    "id": "uuid",
    "type": "inventory",
    "attributes": {
      "productId": "string",
      "quantity": 100,
      "createdAt": "2023-12-01T10:00:00",
      "updatedAt": "2023-12-01T10:00:00"
    }
  }
}
```
