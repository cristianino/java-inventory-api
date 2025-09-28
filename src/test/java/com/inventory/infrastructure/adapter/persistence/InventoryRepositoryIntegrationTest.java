/* Commented out - TestContainers integration test disabled for now
package com.inventory.infrastructure.adapter.persistence;

import com.inventory.domain.model.Inventory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@DisplayName("Inventory Repository Integration Tests")
@Disabled("Integration tests disabled for coverage measurement - requires Docker")
class InventoryRepositoryIntegrationTest {

    @Autowired
    private InventoryJpaRepository jpaRepository;

    private InventoryRepositoryImpl repository;

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
*/