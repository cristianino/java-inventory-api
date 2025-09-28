package com.inventory.infrastructure.adapter.rest;

import com.github.jasminb.jsonapi.ResourceConverter;
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

@WebMvcTest(controllers = InventoryController.class)
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

    @MockBean
    private ResourceConverter resourceConverter;

    private Inventory createSampleInventory(String productId, Integer quantity) {
        return new Inventory(UUID.randomUUID(), productId, quantity, LocalDateTime.now(), LocalDateTime.now());
    }

    @Nested
    class GetInventoryTests {
        
        @Test
        void shouldReturnAllInventoryItems() throws Exception {
            List<Inventory> inventories = List.of(
                createSampleInventory("PROD-001", 100),
                createSampleInventory("PROD-002", 50)
            );
            when(getInventoryUseCase.findAll()).thenReturn(inventories);
            when(resourceConverter.writeDocument(any())).thenReturn("{}".getBytes());

            mockMvc.perform(get("/api/inventory"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/vnd.api+json"));
        }

        @Test
        void shouldReturnInventoryById() throws Exception {
            UUID id = UUID.randomUUID();
            Inventory inventory = createSampleInventory("PROD-001", 100);
            when(getInventoryUseCase.findById(id)).thenReturn(Optional.of(inventory));
            when(resourceConverter.writeDocument(any())).thenReturn("{}".getBytes());

            mockMvc.perform(get("/api/inventory/{id}", id))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/vnd.api+json"));
        }

        @Test
        void shouldReturnNotFoundWhenInventoryDoesNotExist() throws Exception {
            UUID id = UUID.randomUUID();
            when(getInventoryUseCase.findById(id)).thenReturn(Optional.empty());
            when(resourceConverter.writeDocument(any())).thenReturn("{}".getBytes());

            mockMvc.perform(get("/api/inventory/{id}", id))
                    .andExpect(status().isNotFound());
        }

        @Test
        void shouldReturnInventoryByProductId() throws Exception {
            String productId = "PROD-001";
            Inventory inventory = createSampleInventory(productId, 100);
            when(getInventoryUseCase.findByProductId(productId)).thenReturn(Optional.of(inventory));
            when(resourceConverter.writeDocument(any())).thenReturn("{}".getBytes());

            mockMvc.perform(get("/api/inventory/product/{productId}", productId))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/vnd.api+json"));
        }

        @Test
        void shouldReturnNotFoundWhenProductInventoryDoesNotExist() throws Exception {
            String productId = "NON-EXISTENT";
            when(getInventoryUseCase.findByProductId(productId)).thenReturn(Optional.empty());
            when(resourceConverter.writeDocument(any())).thenReturn("{}".getBytes());

            mockMvc.perform(get("/api/inventory/product/{productId}", productId))
                    .andExpect(status().isNotFound());
        }

        @Test
        void shouldReturnInternalServerErrorWhenGetByProductIdFails() throws Exception {
            String productId = "PROD-001";
            when(getInventoryUseCase.findByProductId(productId)).thenThrow(new RuntimeException("Database error"));

            mockMvc.perform(get("/api/inventory/product/{productId}", productId))
                    .andExpect(status().isInternalServerError());
        }

        @Test 
        void shouldReturnLowStockInventory() throws Exception {
            List<Inventory> lowStockInventories = List.of(
                createSampleInventory("PROD-003", 5)
            );
            when(getInventoryUseCase.findLowStock(10)).thenReturn(lowStockInventories);
            when(resourceConverter.writeDocument(any())).thenReturn("{}".getBytes());

            mockMvc.perform(get("/api/inventory?lowStockThreshold=10"))
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
            when(resourceConverter.writeDocument(any())).thenReturn("{}".getBytes());

            mockMvc.perform(post("/api/inventory")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType("application/vnd.api+json"));
        }

        @Test
        void shouldReturnBadRequestForInvalidRequest() throws Exception {
            CreateInventoryRequest request = new CreateInventoryRequest(null, -1);

            mockMvc.perform(post("/api/inventory")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void shouldReturnBadRequestWhenCreateInventoryWithInvalidArgument() throws Exception {
            CreateInventoryRequest request = new CreateInventoryRequest("PROD-001", 100);
            when(createInventoryUseCase.execute(any(), any())).thenThrow(new IllegalArgumentException("Product already exists"));

            mockMvc.perform(post("/api/inventory")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void shouldReturnBadRequestWhenCreateInventoryFails() throws Exception {
            CreateInventoryRequest request = new CreateInventoryRequest("PROD-001", 100);
            when(createInventoryUseCase.execute(any(), any())).thenThrow(new RuntimeException("Database error"));

            mockMvc.perform(post("/api/inventory")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
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
            when(resourceConverter.writeDocument(any())).thenReturn("{}".getBytes());

            mockMvc.perform(put("/api/inventory/{id}/quantity", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/vnd.api+json"));
        }

        @Test
        void shouldReturnBadRequestWhenUpdateQuantityWithInvalidArgument() throws Exception {
            UUID id = UUID.randomUUID();
            UpdateQuantityRequest request = new UpdateQuantityRequest(150);
            
            when(updateInventoryUseCase.updateQuantity(id, 150)).thenThrow(new IllegalArgumentException("Invalid quantity"));

            mockMvc.perform(put("/api/inventory/{id}/quantity", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void shouldReturnInternalServerErrorWhenUpdateQuantityFails() throws Exception {
            UUID id = UUID.randomUUID();
            UpdateQuantityRequest request = new UpdateQuantityRequest(150);
            
            when(updateInventoryUseCase.updateQuantity(id, 150)).thenThrow(new RuntimeException("Database error"));

            mockMvc.perform(put("/api/inventory/{id}/quantity", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        void shouldUpdateQuantityByProductIdSuccessfully() throws Exception {
            String productId = "PROD-001";
            UpdateQuantityRequest request = new UpdateQuantityRequest(150);
            Inventory updatedInventory = createSampleInventory(productId, 150);
            
            when(updateInventoryUseCase.updateQuantityByProductId(productId, 150)).thenReturn(updatedInventory);
            when(resourceConverter.writeDocument(any())).thenReturn("{}".getBytes());

            mockMvc.perform(put("/api/inventory/product/{productId}/quantity", productId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/vnd.api+json"));
        }

        @Test
        void shouldReturnBadRequestWhenUpdateQuantityByProductIdWithInvalidArgument() throws Exception {
            String productId = "PROD-001";
            UpdateQuantityRequest request = new UpdateQuantityRequest(150);
            
            when(updateInventoryUseCase.updateQuantityByProductId(productId, 150)).thenThrow(new IllegalArgumentException("Invalid product ID"));

            mockMvc.perform(put("/api/inventory/product/{productId}/quantity", productId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void shouldReturnInternalServerErrorWhenUpdateQuantityByProductIdFails() throws Exception {
            String productId = "PROD-001";
            UpdateQuantityRequest request = new UpdateQuantityRequest(150);
            
            when(updateInventoryUseCase.updateQuantityByProductId(productId, 150)).thenThrow(new RuntimeException("Database error"));

            mockMvc.perform(put("/api/inventory/product/{productId}/quantity", productId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    class DeleteInventoryTests {

        @Test
        void shouldDeleteInventorySuccessfully() throws Exception {
            UUID id = UUID.randomUUID();
            when(deleteInventoryUseCase.deleteById(id)).thenReturn(true);

            mockMvc.perform(delete("/api/inventory/{id}", id))
                    .andExpect(status().isNoContent());
        }

        @Test
        void shouldReturnNotFoundWhenDeleteInventoryNotExists() throws Exception {
            UUID id = UUID.randomUUID();
            when(deleteInventoryUseCase.deleteById(id)).thenReturn(false);

            mockMvc.perform(delete("/api/inventory/{id}", id))
                    .andExpect(status().isNotFound());
        }

        @Test
        void shouldReturnBadRequestWhenDeleteInventoryWithInvalidArgument() throws Exception {
            UUID id = UUID.randomUUID();
            when(deleteInventoryUseCase.deleteById(id)).thenThrow(new IllegalArgumentException("Invalid ID"));

            mockMvc.perform(delete("/api/inventory/{id}", id))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void shouldReturnInternalServerErrorWhenDeleteInventoryFails() throws Exception {
            UUID id = UUID.randomUUID();
            when(deleteInventoryUseCase.deleteById(id)).thenThrow(new RuntimeException("Database error"));

            mockMvc.perform(delete("/api/inventory/{id}", id))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        void shouldDeleteInventoryByProductIdSuccessfully() throws Exception {
            String productId = "PROD-001";
            when(deleteInventoryUseCase.deleteByProductId(productId)).thenReturn(true);

            mockMvc.perform(delete("/api/inventory/product/{productId}", productId))
                    .andExpect(status().isNoContent());
        }

        @Test
        void shouldReturnNotFoundWhenDeleteInventoryByProductIdNotExists() throws Exception {
            String productId = "NON-EXISTENT";
            when(deleteInventoryUseCase.deleteByProductId(productId)).thenReturn(false);

            mockMvc.perform(delete("/api/inventory/product/{productId}", productId))
                    .andExpect(status().isNotFound());
        }

        @Test
        void shouldReturnBadRequestWhenDeleteInventoryByProductIdWithInvalidArgument() throws Exception {
            String productId = "PROD-001";
            when(deleteInventoryUseCase.deleteByProductId(productId)).thenThrow(new IllegalArgumentException("Invalid product ID"));

            mockMvc.perform(delete("/api/inventory/product/{productId}", productId))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void shouldReturnInternalServerErrorWhenDeleteInventoryByProductIdFails() throws Exception {
            String productId = "PROD-001";
            when(deleteInventoryUseCase.deleteByProductId(productId)).thenThrow(new RuntimeException("Database error"));

            mockMvc.perform(delete("/api/inventory/product/{productId}", productId))
                    .andExpect(status().isInternalServerError());
        }
    }
}
