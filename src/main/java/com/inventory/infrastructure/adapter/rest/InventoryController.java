package com.inventory.infrastructure.adapter.rest;

import com.github.jasminb.jsonapi.JSONAPIDocument;
import com.github.jasminb.jsonapi.ResourceConverter;
import com.inventory.application.usecase.*;
import com.inventory.domain.model.Inventory;
import com.inventory.infrastructure.adapter.rest.dto.CreateInventoryRequest;
import com.inventory.infrastructure.adapter.rest.dto.InventoryDto;
import com.inventory.infrastructure.adapter.rest.dto.UpdateQuantityRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/inventory")
@Tag(name = "Inventory Management", description = "CRUD operations for inventory management")
public class InventoryController {
    private static final Logger logger = LoggerFactory.getLogger(InventoryController.class);
    
    private final CreateInventoryUseCase createInventoryUseCase;
    private final GetInventoryUseCase getInventoryUseCase;
    private final UpdateInventoryUseCase updateInventoryUseCase;
    private final DeleteInventoryUseCase deleteInventoryUseCase;
    private final ResourceConverter resourceConverter;

    public InventoryController(CreateInventoryUseCase createInventoryUseCase,
                              GetInventoryUseCase getInventoryUseCase,
                              UpdateInventoryUseCase updateInventoryUseCase,
                              DeleteInventoryUseCase deleteInventoryUseCase,
                              ResourceConverter resourceConverter) {
        this.createInventoryUseCase = createInventoryUseCase;
        this.getInventoryUseCase = getInventoryUseCase;
        this.updateInventoryUseCase = updateInventoryUseCase;
        this.deleteInventoryUseCase = deleteInventoryUseCase;
        this.resourceConverter = resourceConverter;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = "application/vnd.api+json")
    @Operation(summary = "Create new inventory entry", description = "Creates a new inventory entry for a product")
    public ResponseEntity<String> createInventory(@Valid @RequestBody CreateInventoryRequest request) {
        try {
            logger.info("Creating inventory for product: {}", request.getProductId());
            
            Inventory inventory = createInventoryUseCase.execute(request.getProductId(), request.getQuantity());
            InventoryDto dto = toDto(inventory);
            
            JSONAPIDocument<InventoryDto> document = new JSONAPIDocument<>(dto);
            byte[] jsonBytes = resourceConverter.writeDocument(document);
            String jsonResponse = new String(jsonBytes);
            
            logger.info("Successfully created inventory: {}", inventory.getId());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .contentType(MediaType.valueOf("application/vnd.api+json"))
                    .body(jsonResponse);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid request for creating inventory: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error creating inventory: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(produces = "application/vnd.api+json")
    @Operation(summary = "Get all inventory entries", description = "Retrieves all inventory entries")
    public ResponseEntity<String> getAllInventory(
            @Parameter(description = "Low stock threshold filter")
            @RequestParam(required = false) Integer lowStockThreshold) {
        try {
            List<Inventory> inventories;
            if (lowStockThreshold != null) {
                logger.info("Getting low stock inventory with threshold: {}", lowStockThreshold);
                inventories = getInventoryUseCase.findLowStock(lowStockThreshold);
            } else {
                logger.info("Getting all inventory entries");
                inventories = getInventoryUseCase.findAll();
            }
            
            List<InventoryDto> dtos = inventories.stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());
            
            JSONAPIDocument<List<InventoryDto>> document = new JSONAPIDocument<>(dtos);
            byte[] jsonBytes = resourceConverter.writeDocument(document);
            String jsonResponse = new String(jsonBytes);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.valueOf("application/vnd.api+json"))
                    .body(jsonResponse);
        } catch (Exception e) {
            logger.error("Error retrieving inventory: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(value = "/{id}", produces = "application/vnd.api+json")
    @Operation(summary = "Get inventory by ID", description = "Retrieves inventory entry by its ID")
    public ResponseEntity<String> getInventoryById(@PathVariable UUID id) {
        try {
            logger.info("Getting inventory by ID: {}", id);
            
            Optional<Inventory> inventory = getInventoryUseCase.findById(id);
            if (inventory.isEmpty()) {
                logger.warn("Inventory not found: {}", id);
                return ResponseEntity.notFound().build();
            }
            
            InventoryDto dto = toDto(inventory.get());
            JSONAPIDocument<InventoryDto> document = new JSONAPIDocument<>(dto);
            byte[] jsonBytes = resourceConverter.writeDocument(document);
            String jsonResponse = new String(jsonBytes);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.valueOf("application/vnd.api+json"))
                    .body(jsonResponse);
        } catch (Exception e) {
            logger.error("Error retrieving inventory {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(value = "/product/{productId}", produces = "application/vnd.api+json")
    @Operation(summary = "Get inventory by product ID", description = "Retrieves inventory entry by product ID")
    public ResponseEntity<String> getInventoryByProductId(@PathVariable String productId) {
        try {
            logger.info("Getting inventory by product ID: {}", productId);
            
            Optional<Inventory> inventory = getInventoryUseCase.findByProductId(productId);
            if (inventory.isEmpty()) {
                logger.warn("Inventory not found for product: {}", productId);
                return ResponseEntity.notFound().build();
            }
            
            InventoryDto dto = toDto(inventory.get());
            JSONAPIDocument<InventoryDto> document = new JSONAPIDocument<>(dto);
            byte[] jsonBytes = resourceConverter.writeDocument(document);
            String jsonResponse = new String(jsonBytes);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.valueOf("application/vnd.api+json"))
                    .body(jsonResponse);
        } catch (Exception e) {
            logger.error("Error retrieving inventory for product {}: {}", productId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping(value = "/{id}/quantity", consumes = MediaType.APPLICATION_JSON_VALUE, produces = "application/vnd.api+json")
    @Operation(summary = "Update inventory quantity", description = "Updates the quantity of an inventory entry")
    public ResponseEntity<String> updateQuantity(@PathVariable UUID id, 
                                                @Valid @RequestBody UpdateQuantityRequest request) {
        try {
            logger.info("Updating quantity for inventory {}: {}", id, request.getQuantity());
            
            Inventory inventory = updateInventoryUseCase.updateQuantity(id, request.getQuantity());
            InventoryDto dto = toDto(inventory);
            
            JSONAPIDocument<InventoryDto> document = new JSONAPIDocument<>(dto);
            byte[] jsonBytes = resourceConverter.writeDocument(document);
            String jsonResponse = new String(jsonBytes);
            
            logger.info("Successfully updated inventory quantity: {}", id);
            return ResponseEntity.ok()
                    .contentType(MediaType.valueOf("application/vnd.api+json"))
                    .body(jsonResponse);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid request for updating inventory {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error updating inventory {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping(value = "/product/{productId}/quantity", consumes = MediaType.APPLICATION_JSON_VALUE, produces = "application/vnd.api+json")
    @Operation(summary = "Update inventory quantity by product ID", description = "Updates the quantity of an inventory entry by product ID")
    public ResponseEntity<String> updateQuantityByProductId(@PathVariable String productId,
                                                           @Valid @RequestBody UpdateQuantityRequest request) {
        try {
            logger.info("Updating quantity for product {}: {}", productId, request.getQuantity());
            
            Inventory inventory = updateInventoryUseCase.updateQuantityByProductId(productId, request.getQuantity());
            InventoryDto dto = toDto(inventory);
            
            JSONAPIDocument<InventoryDto> document = new JSONAPIDocument<>(dto);
            byte[] jsonBytes = resourceConverter.writeDocument(document);
            String jsonResponse = new String(jsonBytes);
            
            logger.info("Successfully updated inventory quantity for product: {}", productId);
            return ResponseEntity.ok()
                    .contentType(MediaType.valueOf("application/vnd.api+json"))
                    .body(jsonResponse);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid request for updating inventory for product {}: {}", productId, e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error updating inventory for product {}: {}", productId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete inventory entry", description = "Deletes an inventory entry by its ID")
    public ResponseEntity<Void> deleteInventory(@PathVariable UUID id) {
        try {
            logger.info("Deleting inventory: {}", id);
            
            boolean deleted = deleteInventoryUseCase.deleteById(id);
            if (deleted) {
                logger.info("Successfully deleted inventory: {}", id);
                return ResponseEntity.noContent().build();
            } else {
                logger.warn("Inventory not found for deletion: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid request for deleting inventory {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error deleting inventory {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/product/{productId}")
    @Operation(summary = "Delete inventory entry by product ID", description = "Deletes an inventory entry by product ID")
    public ResponseEntity<Void> deleteInventoryByProductId(@PathVariable String productId) {
        try {
            logger.info("Deleting inventory for product: {}", productId);
            
            boolean deleted = deleteInventoryUseCase.deleteByProductId(productId);
            if (deleted) {
                logger.info("Successfully deleted inventory for product: {}", productId);
                return ResponseEntity.noContent().build();
            } else {
                logger.warn("Inventory not found for product deletion: {}", productId);
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid request for deleting inventory for product {}: {}", productId, e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error deleting inventory for product {}: {}", productId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private InventoryDto toDto(Inventory inventory) {
        return new InventoryDto(
                inventory.getId(),
                inventory.getProductId(),
                inventory.getQuantity(),
                inventory.getCreatedAt(),
                inventory.getUpdatedAt()
        );
    }
}