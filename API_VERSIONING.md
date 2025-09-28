# API Versioning Strategy

This document describes the versioning strategy implemented in the Java Inventory API.

## Implemented Strategy

### URL Path Versioning

The API uses URL path versioning with two types of endpoints:

1. **Default Endpoints (unversioned)**: Always point to the current latest version
2. **Versioned Endpoints**: Specific versions that maintain backward compatibility

### URL Structure

#### Default Version (Latest)
```
GET /api/inventory           # Always points to the latest version (currently v1)
GET /api/system             # Always points to the latest version (currently v1)
```

#### Specific Version V1
```
GET /api/v1/inventory       # Specific v1 version
GET /api/v1/system         # Specific v1 version
```

## Available Endpoints

### Inventory Management

| Method | Default Endpoint | V1 Endpoint | Description |
|--------|------------------|-------------|-------------|
| POST | `/api/inventory` | `/api/v1/inventory` | Create new inventory |
| GET | `/api/inventory` | `/api/v1/inventory` | Get all inventories |
| GET | `/api/inventory/{id}` | `/api/v1/inventory/{id}` | Get inventory by ID |
| GET | `/api/inventory/product/{productId}` | `/api/v1/inventory/product/{productId}` | Get inventory by Product ID |
| PUT | `/api/inventory/{id}/quantity` | `/api/v1/inventory/{id}/quantity` | Update quantity by ID |
| PUT | `/api/inventory/product/{productId}/quantity` | `/api/v1/inventory/product/{productId}/quantity` | Update quantity by Product ID |
| DELETE | `/api/inventory/{id}` | `/api/v1/inventory/{id}` | Delete inventory by ID |
| DELETE | `/api/inventory/product/{productId}` | `/api/v1/inventory/product/{productId}` | Delete inventory by Product ID |

### System Utilities

| Method | Default Endpoint | V1 Endpoint | Description |
|--------|------------------|-------------|-------------|
| GET | `/api/system/connectivity-test` | `/api/v1/system/connectivity-test` | Connectivity test |
| GET | `/api/system/info` | `/api/v1/system/info` | System information |

## Technical Implementation

### Controllers

#### Default Controllers
- `InventoryController`: Handles `/api/inventory/*`
- `SystemControllerDefault`: Handles `/api/system/*`

#### V1 Controllers  
- `InventoryControllerV1`: Handles `/api/v1/inventory/*`
- `SystemController`: Handles `/api/v1/system/*`

### Swagger/OpenAPI

Swagger documentation shows both versions with differentiated tags:

- **"Inventory Management (Default)"**: Unversioned endpoints
- **"Inventory Management V1"**: V1 versioned endpoints
- **"System (Default)"**: System endpoints without version
- **"System V1"**: V1 versioned system endpoints

## Advantages of This Strategy

1. **Flexibility**: Clients can use default version to always get the latest version
2. **Stability**: Clients needing stability can use specific versioned endpoints
3. **Backward Compatibility**: Specific versions remain unchanged
4. **Easy Migration**: Clients can gradually migrate from specific versions to default

## Future Versioning Workflow

### For V2:

1. Create V2 controllers:
   - `InventoryControllerV2` at `/api/v2/inventory/*`
   - `SystemControllerV2` at `/api/v2/system/*`

2. Update default controllers to point to V2

3. Maintain V1 for backward compatibility

### Version Deprecation

1. Mark version as deprecated in Swagger
2. Add deprecation headers in responses
3. Notify clients with sufficient time
4. Remove version after grace period

## Response Headers

All responses include version information:

```json
{
  "meta": {
    "apiVersion": "1.0.0",
    "timestamp": "2023-09-27T10:30:00Z"
  }
}
```

## Authentication

All versions require the same authentication method:
- Header: `X-API-Key`
- Value: `your-secret-api-key-here` (for testing)

## Testing

Tests are organized by version:
- `/test/.../rest/InventoryControllerTest.java` - Tests for default version
- `/test/.../rest/v1/InventoryControllerV1Test.java` - Tests for v1 version

## Considerations

1. **Maintenance**: Each version requires separate maintenance
2. **Documentation**: Swagger should clearly reflect differences between versions
3. **Monitoring**: Track usage of each version to plan deprecations
4. **Performance**: Default endpoints have a small logical redirection overhead