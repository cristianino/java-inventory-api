package com.inventory.infrastructure.adapter.rest;

import com.inventory.application.usecase.*;
import com.inventory.domain.model.Inventory;
import com.inventory.infrastructure.adapter.rest.dto.CreateInventoryRequest;
import com.inventory.infrastructure.adapter.rest.dto.UpdateQuantityRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InventoryController.class)
@ActiveProfiles("test")
class InventoryControllerTest {

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

    @Nested
    class GetInventoryTests {
        
        @Test
        void shouldReturnAllInventoryItems() throws Exception {
            List<Inventory> inventories = List.of(
                createSampleInventory("PROD-001", 100),
                createSampleInventory("PROD-002", 50)
            );
            when(getInventoryUseCase.findAll()).thenReturn(inventories);

            mockMvc.perform(get("/api/inventories"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/vnd.api+json"));
        }

        @Test
        void shouldReturnInventoryById() throws Exception {
            UUID id = UUID.randomUUID();
            Inventory inventory = createSampleInventory("PROD-001", 100);
            when(getInventoryUseCase.findById(id)).thenReturn(Optional.of(inventory));

            mockMvc.perform(get("/api/inventories/{id}", id))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/vnd.api+json"));
        }

        @Test
        void shouldReturnNotFoundWhenInventoryDoesNotExist() throws Exception {
            UUID id = UUID.randomUUID();
            when(getInventoryUseCase.findById(id)).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/inventories/{id}", id))
                    .andExpect(status().isNotFound());
        }

        @Test
        void shouldReturnInventoryByProductId() throws Exception {
            String productId = "PROD-001";
            Inventory inventory = createSampleInventory(productId, 100);
            when(getInventoryUseCase.findByProductId(productId)).thenReturn(Optional.of(inventory));

            mockMvc.perform(get("/api/inventories/product/{productId}", productId))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/vnd.api+json"));
        }

        @Test 
        void shouldReturnLowStockInventory() throws Exception {
            List<Inventory> lowStockInventories = List.of(
                createSampleInventory("PROD-003", 5)
            );
            when(getInventoryUseCase.findLowStock(10)).thenReturn(lowStockInventories);

            mockMvc.perform(get("/api/inventories?threshold=10"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/vnd.api+json"));
        }
    }

    @Nested
    class CreateInventoryTests {
        
        @Test
        void shouldCreateInventorySuccessfully() throws Exception {
            CreateInventoryRequest request = new CreateInventoryRequest("PROD-001", 100);
            Inventory createdInventory = createSampleInventory("PROD-001", 100);
            
            when(createInventoryUseCase.execute(any(), any())).thenReturn(createdInventory);

            mockMvc.perform(post("/api/inventories")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType("application/vnd.api+json"));
        }

        @Test
        void shouldReturnBadRequestForInvalidRequest() throws Exception {
            CreateInventoryRequest invalidRequest = new CreateInventoryRequest(null, -1);

            mockMvc.perform(post("/api/inventories")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class UpdateInventoryTests {
        
        @Test
        void shouldUpdateQuantitySuccessfully() throws Exception {
            UUID id = UUID.randomUUID();
            UpdateQuantityRequest request = new UpdateQuantityRequest(150);
            Inventory updatedInventory = createSampleInventory("PROD-001", 150);
            
            when(updateInventoryUseCase.updateQuantity(id, 150)).thenReturn(updatedInventory);

            mockMvc.perform(put("/api/inventories/{id}/quantity", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/vnd.api+json"));
        }

        @Test
        void shouldIncrementQuantitySuccessfully() throws Exception {
            UUID id = UUID.randomUUID();
            UpdateQuantityRequest request = new UpdateQuantityRequest(50);
            Inventory updatedInventory = createSampleInventory("PROD-001", 150);
            
            when(updateInventoryUseCase.incrementQuantity(id, 50)).thenReturn(updatedInventory);

            mockMvc.perform(patch("/api/inventories/{id}/increment", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/vnd.api+json"));
        }

        @Test
        void shouldDecrementQuantitySuccessfully() throws Exception {
            UUID id = UUID.randomUUID();
            UpdateQuantityRequest request = new UpdateQuantityRequest(25);
            Inventory updatedInventory = createSampleInventory("PROD-001", 75);
            
            when(updateInventoryUseCase.decrementQuantity(id, 25)).thenReturn(updatedInventory);

            mockMvc.perform(patch("/api/inventories/{id}/decrement", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/vnd.api+json"));
        }
    }

    @Nested
    class DeleteInventoryTests {
        
        @Test
        void shouldDeleteInventorySuccessfully() throws Exception {
            UUID id = UUID.randomUUID();

            mockMvc.perform(delete("/api/inventories/{id}", id))
                    .andExpect(status().isNoContent());
        }
    }

    private Inventory createSampleInventory(String productId, Integer quantity) {
        return new Inventory(
            UUID.randomUUID(),
            productId,
            quantity,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }
}
