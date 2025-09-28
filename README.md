# Java Inventory API

## Overview

A Spring Boot 3 microservice for inventory management implementing Clean Architecture (Hexagonal Architecture) with JSON:API compliance, external service integration, and comprehensive monitoring. This service provides complete CRUD operations for inventory management with real-time validation against external product services.

## 🏗️ Architecture & Design

### Clean Architecture Implementation

This project follows **Hexagonal Architecture** (Ports and Adapters) principles:

```
┌─────────────────────────────────────────────────────────────┐
│                    Infrastructure Layer                      │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │ REST API    │  │ Database    │  │ External Services   │  │
│  │ Controllers │  │ JPA Repos   │  │ WebClient          │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
├─────────────────────────────────────────────────────────────┤
│                    Application Layer                        │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │              Use Cases (Business Logic)                 │ │
│  │  - CreateInventoryUseCase                               │ │
│  │  - GetInventoryUseCase                                  │ │
│  │  - UpdateInventoryUseCase                               │ │
│  │  - DeleteInventoryUseCase                               │ │
│  └─────────────────────────────────────────────────────────┘ │
├─────────────────────────────────────────────────────────────┤
│                      Domain Layer                           │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │ Entities    │  │ Value       │  │ Domain Ports        │  │
│  │ - Inventory │  │ Objects     │  │ - InventoryRepo     │  │
│  │ - Product   │  │             │  │ - ProductService    │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

### Core Features

- **🏛️ Hexagonal Architecture**: Clean separation with Domain, Application, and Infrastructure layers
- **📦 Complete CRUD Operations**: Full inventory management by product ID and quantity
- **🌐 JSON:API Compliance**: RESTful endpoints following JSON:API v1.1 specification
- **🔗 External Service Integration**: WebClient-based communication with products service
- **🛡️ Resilience Patterns**: Circuit breaker, retries, and timeouts using Resilience4j
- **🗄️ Database Management**: PostgreSQL with JPA/Hibernate and Flyway migrations
- **📚 API Documentation**: Complete Swagger/OpenAPI 3.0 integration with examples
- **📊 Monitoring & Health**: Actuator endpoints with custom health indicators
- **📝 Structured Logging**: JSON-formatted logs for centralized logging (Loki/Grafana)
- **🧪 Comprehensive Testing**: Unit and integration tests with Testcontainers
- **🐳 Containerization**: Multi-stage Docker builds with docker-compose orchestration
- **🔒 Security**: API key-based authentication with configurable keys
- **📈 API Versioning**: Dual endpoint strategy (latest + versioned endpoints)

## 🛠️ Technology Stack

### Core Framework
- **Java 17** - LTS version with modern language features
- **Spring Boot 3.2.0** - Latest Spring Boot with Jakarta EE support
- **Spring Web** - RESTful API development
- **Spring Data JPA** - Data persistence layer

### Database & Persistence
- **PostgreSQL 15** - Primary database with JSON support
- **Flyway** - Database version control and migrations
- **HikariCP** - High-performance connection pooling

### API & Integration
- **JSON:API v1.1** - Standardized API response format
- **WebClient** - Reactive HTTP client for external services
- **Resilience4j** - Circuit breaker, retry, and timeout patterns
- **Swagger/OpenAPI 3.0** - API documentation and testing

### Development & Testing
- **Testcontainers** - Integration testing with real databases
- **JUnit 5** - Unit and integration testing framework
- **AssertJ** - Fluent assertion library
- **Spring Boot Test** - Testing utilities and mocks

### Monitoring & Operations
- **Spring Actuator** - Health checks and metrics
- **Logback** - Structured JSON logging
- **Docker** - Containerization and deployment

## 📡 API Specification

### API Versioning Strategy

This service implements a **dual endpoint strategy** for maximum compatibility:

- **Latest Endpoints** (`/api/inventory`): Always redirect to the current version
- **Versioned Endpoints** (`/api/v1/inventory`): Explicit version for client stability

### Inventory Management Endpoints

#### Core CRUD Operations
| Method | Endpoint | Description | Request | Response |
|--------|----------|-------------|---------|----------|
| `POST` | `/api/v1/inventory` | Create inventory entry | `{"productId": "123", "quantity": 50}` | JSON:API format |
| `GET` | `/api/v1/inventory` | List all inventory entries | - | JSON:API collection |
| `GET` | `/api/v1/inventory/{id}` | Get inventory by UUID | - | JSON:API single resource |
| `PUT` | `/api/v1/inventory/{id}/quantity` | Update inventory quantity | `{"quantity": 75}` | JSON:API format |
| `DELETE` | `/api/v1/inventory/{id}` | Delete inventory entry | - | `204 No Content` |

#### Product-Based Operations
| Method | Endpoint | Description | Request | Response |
|--------|----------|-------------|---------|----------|
| `GET` | `/api/v1/inventory/product/{productId}` | Get inventory by product ID | - | JSON:API format |
| `PUT` | `/api/v1/inventory/product/{productId}/quantity` | Update by product ID | `{"quantity": 100}` | JSON:API format |
| `DELETE` | `/api/v1/inventory/product/{productId}` | Delete by product ID | - | `204 No Content` |

### System Endpoints

#### Health & Monitoring
| Method | Endpoint | Description | Response |
|--------|----------|-------------|-----------|
| `GET` | `/actuator/health` | System health status | Health indicators |
| `GET` | `/api/v1/system/info` | API version and info | System metadata |
| `GET` | `/swagger-ui/index.html` | Interactive API docs | Swagger UI |

### Authentication

All endpoints require **X-API-Key** header:
```bash
curl -H "X-API-Key: your-secret-api-key-here" \
     http://localhost:8082/api/v1/inventory
```

## Running the Application

### Prerequisites

- Java 17+
- Docker and Docker Compose
- Maven 3.6+
- PostgreSQL container running (from product-api stack)

### 🚨 Critical First-Time Setup

**⚠️ IMPORTANT: Database must be created manually before first startup!**

Please follow the detailed instructions in [DATABASE_SETUP.md](DATABASE_SETUP.md) for:
- Database creation requirements
- Automatic data seeding process
- Troubleshooting common issues

### Quick Start

#### Setup Steps

1. **Create the database** (required only once):
```bash
docker exec -it postgres-db psql -U productuser -d postgres -c "CREATE DATABASE inventoryapi;"
```

2. **Start the application**:
```bash
docker-compose up --build -d
```

3. **Verify setup**:
```bash
# Check logs
docker logs -f inventory-api

# Test API
curl -H "X-API-Key: your-secret-api-key-here" http://localhost:8082/api/inventory
```

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
   - API: http://localhost:8082/api/inventory
   - Swagger UI: http://localhost:8082/swagger-ui/index.html
   - Health: http://localhost:8082/actuator/health

## Configuration

### Environment Variables

- `SPRING_DATASOURCE_URL` - PostgreSQL database URL
- `SPRING_DATASOURCE_USERNAME` - Database username
- `SPRING_DATASOURCE_PASSWORD` - Database password
- `EXTERNAL_PRODUCTS_SERVICE_BASE_URL` - Products service base URL
- `PRODUCTS_API_KEY` - API key for products service authentication
- `APP_API_KEY` - API key for incoming requests authentication

### Spring Profiles

| Profile | Purpose | Database | External Services |
|---------|---------|----------|-------------------|
| `default` | Local development | H2 in-memory | Mock services |
| `docker` | Container deployment | PostgreSQL | Real services |
| `test` | Automated testing | Testcontainers | Mock services |

## 🧪 Testing Strategy

### Unit Tests
```bash
# Run unit tests only
mvn test

# Run with coverage report
mvn test jacoco:report
```

### Integration Tests
```bash
# Run integration tests with Testcontainers
mvn verify

# Run specific test class
mvn test -Dtest=InventoryControllerIntegrationTest
```

### Testing Architecture

#### Unit Testing (Domain & Application Layers)
- **Domain Entities**: Business logic validation
- **Use Cases**: Business workflow testing
- **Mocking**: External dependencies isolated

#### Integration Testing (Infrastructure Layer)
- **Testcontainers**: Real PostgreSQL database
- **WebMvcTest**: REST API endpoint testing
- **MockWebServer**: External service simulation

### Test Coverage Goals
- **Domain Layer**: 100% (critical business logic)
- **Application Layer**: 95% (use case scenarios)
- **Infrastructure Layer**: 80% (integration points)

## 🏛️ Architecture Deep Dive

### Hexagonal Architecture Implementation

```
src/main/java/com/inventory/
├── domain/                          # 🎯 Domain Layer (Business Core)
│   ├── model/
│   │   ├── Inventory.java          # Core business entity
│   │   └── Product.java            # Value object
│   └── port/
│       ├── InventoryRepository.java # Repository contract
│       └── ProductService.java     # External service contract
├── application/                     # 🔄 Application Layer (Use Cases)
│   └── usecase/
│       ├── CreateInventoryUseCase.java
│       ├── GetInventoryUseCase.java
│       ├── UpdateInventoryUseCase.java
│       └── DeleteInventoryUseCase.java
└── infrastructure/                  # 🔌 Infrastructure Layer (Adapters)
    ├── adapter/
    │   ├── persistence/            # Database adapters
    │   ├── rest/                   # HTTP API adapters
    │   └── external/               # External service adapters
    └── config/                     # Configuration classes
```

### Key Design Principles

1. **Dependency Inversion**: Domain layer defines contracts (ports), infrastructure implements them
2. **Separation of Concerns**: Each layer has clear responsibilities
3. **Testability**: Domain logic can be tested without external dependencies
4. **Flexibility**: Easy to swap implementations (e.g., different databases)

### External Integrations

#### Products Service Integration
- **Purpose**: Real-time product validation before inventory operations
- **Protocol**: HTTP REST with JSON:API format
- **Authentication**: X-API-Key header authentication
- **Resilience**: Circuit breaker, retries, and timeouts
- **Fallback**: Graceful degradation when service unavailable

#### Database Integration
- **Primary Storage**: PostgreSQL with ACID compliance
- **Connection Pooling**: HikariCP for optimal performance
- **Migrations**: Flyway for version-controlled schema changes
- **Seeding**: Automatic initial data population

## 📄 JSON:API Response Format

All API responses follow the [JSON:API v1.1 specification](https://jsonapi.org/):

### Single Resource Response
```json
{
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440001",
    "type": "inventory",
    "attributes": {
      "productId": "PROD-001",
      "quantity": 100,
      "createdAt": "2025-09-28T17:51:46.752867",
      "updatedAt": "2025-09-28T17:51:46.752867"
    }
  },
  "meta": {
    "apiVersion": "1.0.0",
    "timestamp": "2025-09-28T18:20:05.376324306"
  },
  "links": {
    "self": "/api/v1/inventory/550e8400-e29b-41d4-a716-446655440001"
  }
}
```

### Collection Response
```json
{
  "data": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440001",
      "type": "inventory",
      "attributes": {
        "productId": "PROD-001",
        "quantity": 100,
        "createdAt": "2025-09-28T17:51:46.752867",
        "updatedAt": "2025-09-28T17:51:46.752867"
      }
    }
  ],
  "meta": {
    "apiVersion": "1.0.0",
    "totalCount": 6,
    "timestamp": "2025-09-28T18:20:05.376324306"
  },
  "links": {
    "self": "/api/v1/inventory"
  },
  "errors": null
}
```

### Error Response
```json
{
  "errors": [
    {
      "status": "400",
      "title": "Validation Error",
      "detail": "Product does not exist: INVALID-ID",
      "source": {
        "parameter": "productId"
      }
    }
  ]
}
```

## 🔧 Performance & Reliability Improvements

### Recommended Enhancements

#### 1. Transaction Management with `@Transactional`
```java
@Service
@Transactional(readOnly = true) // Default for all methods
public class CreateInventoryUseCase {
    
    @Transactional // Override for write operations
    public Inventory createInventory(CreateInventoryRequest request) {
        // Ensures atomicity for:
        // 1. Product validation
        // 2. Inventory creation
        // 3. Audit logging
        // Automatic rollback on exceptions
    }
}
```

**Benefits:**
- **ACID Compliance**: Ensures data consistency
- **Automatic Rollback**: Failed operations don't leave partial data
- **Connection Management**: Optimized database connection usage
- **Read-Only Optimization**: Better performance for query operations

#### 2. Caching Strategy
```java
@Service
public class ProductServiceImpl {
    
    @Cacheable(value = "products", key = "#productId")
    public Optional<Product> findById(String productId) {
        // Cache frequently accessed products
        // Reduces external API calls
        // Improves response times
    }
}
```

#### 3. Database Connection Optimization
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
```

#### 4. Monitoring Enhancements
- **Custom Metrics**: Track inventory operations per second
- **Health Indicators**: Monitor external service connectivity
- **Distributed Tracing**: Request correlation across services

## 🚀 Deployment & Operations

### Docker Deployment

#### Production Deployment
```bash
# Start the entire stack
docker-compose up -d

# Check service status
docker-compose ps

# View logs
docker-compose logs -f inventory-api
```

#### Environment Configuration
Create `.env` file from `.env.example`:
```bash
cp .env.example .env
# Edit .env with your specific values
```

### Kubernetes Deployment (Optional)
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: inventory-api
spec:
  replicas: 3
  selector:
    matchLabels:
      app: inventory-api
  template:
    metadata:
      labels:
        app: inventory-api
    spec:
      containers:
      - name: inventory-api
        image: inventory-api:latest
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "kubernetes"
```

## 🔍 Monitoring & Observability

### Health Checks
- **Application Health**: `/actuator/health`
- **Database Connectivity**: Custom health indicator
- **External Service Health**: Products service availability

### Metrics & Logging
- **Structured Logging**: JSON format for log aggregation
- **Application Metrics**: Custom business metrics
- **JVM Metrics**: Memory, GC, threads monitoring

### Integration with Monitoring Stack
- **Loki**: Log aggregation and searching
- **Grafana**: Dashboards and alerting
- **Prometheus**: Metrics collection and storage

## 📋 Development Guidelines

### Code Style & Standards
- **Clean Code**: Following Robert Martin's principles
- **SOLID Principles**: Especially in domain layer design
- **DRY**: Don't Repeat Yourself across layers
- **YAGNI**: You Aren't Gonna Need It for features

### Git Workflow
```bash
# Feature development
git checkout -b feature/add-inventory-search
git commit -m "feat: add inventory search by multiple criteria"
git push origin feature/add-inventory-search

# Create pull request with:
# - Clear description
# - Test coverage
# - Documentation updates
```

### Database Migrations
```bash
# Create new migration
src/main/resources/db/migration/V3__Add_inventory_audit_table.sql

# Migration naming convention:
# V{version}__{description}.sql
# Example: V3__Add_inventory_audit_table.sql
```

## 🤝 Contributing

### Development Setup
1. **Clone repository**
2. **Setup database** (see DATABASE_SETUP.md)
3. **Run tests** to ensure everything works
4. **Create feature branch** from main
5. **Implement changes** with tests
6. **Submit pull request** with documentation

### Pull Request Checklist
- [ ] Tests pass (`mvn verify`)
- [ ] Code coverage maintained
- [ ] Documentation updated
- [ ] Database migrations included (if needed)
- [ ] API changes documented in Swagger
- [ ] Integration tests added for new endpoints

## 📚 Additional Resources

### Documentation
- [Database Setup Guide](DATABASE_SETUP.md) - Complete database initialization
- [API Versioning Strategy](API_VERSIONING.md) - Versioning approach and migration
- [Development Guide](DEVELOPMENT.md) - Local development setup

### External References
- [JSON:API Specification](https://jsonapi.org/) - API format standard
- [Spring Boot Documentation](https://spring.io/projects/spring-boot) - Framework reference
- [Hexagonal Architecture](https://alistair.cockburn.us/hexagonal-architecture/) - Architecture pattern
- [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html) - Design principles

---

## 📄 License

This project is licensed under the MIT License. See LICENSE file for details.

---

**Built with ❤️ using Spring Boot 3, Clean Architecture, and modern Java practices.**
