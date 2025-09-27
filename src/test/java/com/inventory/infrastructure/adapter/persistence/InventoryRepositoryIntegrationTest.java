package com.inventory.infrastructure.adapter.persistence;

import com.inventory.domain.model.Inventory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@Transactional
class InventoryRepositoryIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("inventory_test")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private InventoryRepositoryImpl inventoryRepository;

    @Test
    void shouldSaveAndFindInventory() {
        // Given
        Inventory inventory = Inventory.create("product-123", 100);

        // When
        Inventory savedInventory = inventoryRepository.save(inventory);
        Optional<Inventory> foundInventory = inventoryRepository.findById(savedInventory.getId());

        // Then
        assertTrue(foundInventory.isPresent());
        assertEquals(inventory.getProductId(), foundInventory.get().getProductId());
        assertEquals(inventory.getQuantity(), foundInventory.get().getQuantity());
    }

    @Test
    void shouldFindByProductId() {
        // Given
        String productId = "product-456";
        Inventory inventory = Inventory.create(productId, 50);
        inventoryRepository.save(inventory);

        // When
        Optional<Inventory> foundInventory = inventoryRepository.findByProductId(productId);

        // Then
        assertTrue(foundInventory.isPresent());
        assertEquals(productId, foundInventory.get().getProductId());
        assertEquals(50, foundInventory.get().getQuantity());
    }

    @Test
    void shouldDeleteByProductId() {
        // Given
        String productId = "product-789";
        Inventory inventory = Inventory.create(productId, 25);
        inventoryRepository.save(inventory);

        // When
        boolean deleted = inventoryRepository.deleteByProductId(productId);
        Optional<Inventory> foundInventory = inventoryRepository.findByProductId(productId);

        // Then
        assertTrue(deleted);
        assertFalse(foundInventory.isPresent());
    }
}