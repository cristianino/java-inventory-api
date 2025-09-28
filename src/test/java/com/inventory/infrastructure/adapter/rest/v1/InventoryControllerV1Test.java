package com.inventory.infrastructure.adapter.rest.v1;

import com.inventory.application.usecase.CreateInventoryUseCase;
import com.inventory.application.usecase.DeleteInventoryUseCase;
import com.inventory.application.usecase.GetInventoryUseCase;
import com.inventory.application.usecase.UpdateInventoryUseCase;
import com.inventory.domain.model.Inventory;
import com.inventory.infrastructure.adapter.rest.dto.CreateInventoryRequest;
import com.inventory.infrastructure.adapter.rest.dto.UpdateQuantityRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InventoryControllerV1.class)
@DisplayName("InventoryControllerV1 Integration Tests")
class InventoryControllerV1Test {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CreateInventoryUseCase createInventoryUseCase;

    @MockBean
    private GetInventoryUseCase getInventoryUseCase;

    @MockBean
    private UpdateInventoryUseCase updateInventoryUseCase;

    @MockBean
    private DeleteInventoryUseCase deleteInventoryUseCase;

    private Inventory sampleInventory;
    private CreateInventoryRequest createRequest;
    private UpdateQuantityRequest updateRequest;

    @BeforeEach
    void setUp() {
        sampleInventory = new Inventory(
                UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
                "PROD-001",
                100,
                LocalDateTime.of(2023, 1, 1, 12, 0),
                LocalDateTime.of(2023, 1, 1, 12, 0)
        );

        createRequest = new CreateInventoryRequest();
        createRequest.setProductId("PROD-001");
        createRequest.setQuantity(100);

        updateRequest = new UpdateQuantityRequest();
        updateRequest.setQuantity(150);
    }

    @Test
    @DisplayName("Should create inventory successfully via V1 endpoint")
    void shouldCreateInventorySuccessfully() throws Exception {
        // Given
        when(createInventoryUseCase.execute(eq("PROD-001"), eq(100)))
                .thenReturn(sampleInventory);

        // When & Then
        mockMvc.perform(post("/api/v1/inventory")
                        .header("X-API-Key", "test-api-key")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Content-Type", "application/vnd.api+json"));

        verify(createInventoryUseCase).execute("PROD-001", 100);
    }

    @Test
    @DisplayName("Should get all inventory via V1 endpoint")
    void shouldGetAllInventory() throws Exception {
        // Given
        List<Inventory> inventories = Arrays.asList(sampleInventory);
        when(getInventoryUseCase.findAll()).thenReturn(inventories);

        // When & Then
        mockMvc.perform(get("/api/v1/inventory")
                        .header("X-API-Key", "test-api-key"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/vnd.api+json"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].type").value("inventory"))
                .andExpect(jsonPath("$.data[0].attributes.productId").value("PROD-001"))
                .andExpect(jsonPath("$.meta.apiVersion").value("1.0.0"));

        verify(getInventoryUseCase).findAll();
    }

    @Test
    @DisplayName("Should get inventory by ID via V1 endpoint")
    void shouldGetInventoryById() throws Exception {
        // Given
        UUID inventoryId = sampleInventory.getId();
        when(getInventoryUseCase.findById(inventoryId))
                .thenReturn(Optional.of(sampleInventory));

        // When & Then
        mockMvc.perform(get("/api/v1/inventory/{id}", inventoryId)
                        .header("X-API-Key", "test-api-key"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/vnd.api+json"))
                .andExpect(jsonPath("$.data.type").value("inventory"))
                .andExpect(jsonPath("$.data.id").value(inventoryId.toString()))
                .andExpect(jsonPath("$.data.attributes.productId").value("PROD-001"));

        verify(getInventoryUseCase).findById(inventoryId);
    }

    @Test
    @DisplayName("Should return 404 when inventory not found via V1 endpoint")
    void shouldReturn404WhenInventoryNotFound() throws Exception {
        // Given
        UUID inventoryId = UUID.randomUUID();
        when(getInventoryUseCase.findById(inventoryId))
                .thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/v1/inventory/{id}", inventoryId)
                        .header("X-API-Key", "test-api-key"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/vnd.api+json"))
                .andExpect(jsonPath("$.errors[0].status").value("404"))
                .andExpect(jsonPath("$.errors[0].code").value("RESOURCE_NOT_FOUND"));

        verify(getInventoryUseCase).findById(inventoryId);
    }

    @Test
    @DisplayName("Should update inventory quantity via V1 endpoint")
    void shouldUpdateInventoryQuantity() throws Exception {
        // Given
        UUID inventoryId = sampleInventory.getId();
        Inventory updatedInventory = new Inventory(
                inventoryId,
                "PROD-001",
                150,
                sampleInventory.getCreatedAt(),
                LocalDateTime.now()
        );

        when(updateInventoryUseCase.updateQuantity(inventoryId, 150))
                .thenReturn(updatedInventory);

        // When & Then
        mockMvc.perform(put("/api/v1/inventory/{id}/quantity", inventoryId)
                        .header("X-API-Key", "test-api-key")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/vnd.api+json"));

        verify(updateInventoryUseCase).updateQuantity(inventoryId, 150);
    }

    @Test
    @DisplayName("Should delete inventory via V1 endpoint")
    void shouldDeleteInventory() throws Exception {
        // Given
        UUID inventoryId = sampleInventory.getId();
        when(deleteInventoryUseCase.deleteById(inventoryId))
                .thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/api/v1/inventory/{id}", inventoryId)
                        .header("X-API-Key", "test-api-key"))
                .andExpect(status().isNoContent());

        verify(deleteInventoryUseCase).deleteById(inventoryId);
    }

    @Test
    @DisplayName("Should get inventory by product ID via V1 endpoint")
    void shouldGetInventoryByProductId() throws Exception {
        // Given
        when(getInventoryUseCase.findByProductId("PROD-001"))
                .thenReturn(Optional.of(sampleInventory));

        // When & Then
        mockMvc.perform(get("/api/v1/inventory/product/{productId}", "PROD-001")
                        .header("X-API-Key", "test-api-key"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/vnd.api+json"));

        verify(getInventoryUseCase).findByProductId("PROD-001");
    }

    @Test
    @DisplayName("Should get low stock inventory via V1 endpoint")
    void shouldGetLowStockInventory() throws Exception {
        // Given
        List<Inventory> lowStockInventories = Arrays.asList(sampleInventory);
        when(getInventoryUseCase.findLowStock(50)).thenReturn(lowStockInventories);

        // When & Then
        mockMvc.perform(get("/api/v1/inventory?lowStockThreshold=50")
                        .header("X-API-Key", "test-api-key"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/vnd.api+json"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.meta.apiVersion").value("1.0.0"));

        verify(getInventoryUseCase).findLowStock(50);
    }
}