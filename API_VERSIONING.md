# API Versioning Strategy

Este documento describe la estrategia de versionado implementada en la Java Inventory API.

## Estrategia Implementada

### Versionado por URL Path

La API utiliza versionado por URL path con dos tipos de endpoints:

1. **Endpoints Default (sin versión)**: Siempre apuntan a la versión más actual
2. **Endpoints Versionados**: Versiones específicas que mantienen compatibilidad hacia atrás

### Estructura de URLs

#### Versión Default (Latest)
```
GET /api/inventory           # Siempre apunta a la versión más actual (actualmente v1)
GET /api/system             # Siempre apunta a la versión más actual (actualmente v1)
```

#### Versión Específica V1
```
GET /api/v1/inventory       # Versión específica v1
GET /api/v1/system         # Versión específica v1
```

## Endpoints Disponibles

### Inventory Management

| Método | Default Endpoint | V1 Endpoint | Descripción |
|--------|------------------|-------------|-------------|
| POST | `/api/inventory` | `/api/v1/inventory` | Crear nuevo inventario |
| GET | `/api/inventory` | `/api/v1/inventory` | Obtener todos los inventarios |
| GET | `/api/inventory/{id}` | `/api/v1/inventory/{id}` | Obtener inventario por ID |
| GET | `/api/inventory/product/{productId}` | `/api/v1/inventory/product/{productId}` | Obtener inventario por Product ID |
| PUT | `/api/inventory/{id}/quantity` | `/api/v1/inventory/{id}/quantity` | Actualizar cantidad por ID |
| PUT | `/api/inventory/product/{productId}/quantity` | `/api/v1/inventory/product/{productId}/quantity` | Actualizar cantidad por Product ID |
| DELETE | `/api/inventory/{id}` | `/api/v1/inventory/{id}` | Eliminar inventario por ID |
| DELETE | `/api/inventory/product/{productId}` | `/api/v1/inventory/product/{productId}` | Eliminar inventario por Product ID |

### System Utilities

| Método | Default Endpoint | V1 Endpoint | Descripción |
|--------|------------------|-------------|-------------|
| GET | `/api/system/connectivity-test` | `/api/v1/system/connectivity-test` | Test de conectividad |
| GET | `/api/system/info` | `/api/v1/system/info` | Información del sistema |

## Implementación Técnica

### Controladores

#### Default Controllers
- `InventoryController`: Maneja `/api/inventory/*`
- `SystemControllerDefault`: Maneja `/api/system/*`

#### V1 Controllers  
- `InventoryControllerV1`: Maneja `/api/v1/inventory/*`
- `SystemController`: Maneja `/api/v1/system/*`

### Swagger/OpenAPI

La documentación de Swagger muestra ambas versiones con tags diferenciados:

- **"Inventory Management (Default)"**: Endpoints sin versión
- **"Inventory Management V1"**: Endpoints versionados v1
- **"System (Default)"**: Endpoints del sistema sin versión
- **"System V1"**: Endpoints del sistema versionados v1

## Ventajas de esta Estrategia

1. **Flexibilidad**: Los clientes pueden usar la versión default para obtener siempre la última versión
2. **Estabilidad**: Los clientes que necesiten estabilidad pueden usar endpoints versionados específicos
3. **Compatibilidad hacia atrás**: Las versiones específicas se mantienen sin cambios
4. **Fácil migración**: Los clientes pueden migrar gradualmente de versiones específicas a default

## Flujo de Versionado Futuro

### Para V2:

1. Crear controladores V2:
   - `InventoryControllerV2` en `/api/v2/inventory/*`
   - `SystemControllerV2` en `/api/v2/system/*`

2. Actualizar controladores default para apuntar a V2

3. Mantener V1 para compatibilidad hacia atrás

### Deprecación de Versiones

1. Marcar la versión como deprecated en Swagger
2. Añadir headers de deprecación en las respuestas
3. Notificar a los clientes con tiempo suficiente
4. Remover la versión después del período de gracia

## Headers de Respuesta

Todas las respuestas incluyen información de versión:

```json
{
  "meta": {
    "apiVersion": "1.0.0",
    "timestamp": "2023-09-27T10:30:00Z"
  }
}
```

## Autenticación

Todas las versiones requieren el mismo método de autenticación:
- Header: `X-API-Key`
- Valor: `your-secret-api-key-here` (para testing)

## Testing

Los tests están organizados por versión:
- `/test/.../rest/InventoryControllerTest.java` - Tests para version default
- `/test/.../rest/v1/InventoryControllerV1Test.java` - Tests para version v1

## Consideraciones

1. **Mantenimiento**: Cada versión requiere mantenimiento separado
2. **Documentación**: Swagger debe reflejar claramente las diferencias entre versiones
3. **Monitoreo**: Trackear el uso de cada versión para planificar deprecaciones
4. **Performance**: Los endpoints default tienen una pequeña sobrecarga de redirección lógica