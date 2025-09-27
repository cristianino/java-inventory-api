package com.inventory.domain.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class InventoryTest {

    @Test
    void shouldCreateInventory() {
        // Given
        String productId = "product-123";
        Integer quantity = 100;

        // When
        Inventory inventory = Inventory.create(productId, quantity);

        // Then
        assertNotNull(inventory.getId());
        assertEquals(productId, inventory.getProductId());
        assertEquals(quantity, inventory.getQuantity());
        assertNotNull(inventory.getCreatedAt());
        assertNotNull(inventory.getUpdatedAt());
    }

    @Test
    void shouldThrowExceptionForNegativeQuantity() {
        // Given
        UUID id = UUID.randomUUID();
        String productId = "product-123";
        Integer negativeQuantity = -1;
        LocalDateTime now = LocalDateTime.now();

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
                new Inventory(id, productId, negativeQuantity, now, now));
    }

    @Test
    void shouldUpdateQuantity() {
        // Given
        Inventory inventory = Inventory.create("product-123", 100);
        Integer newQuantity = 150;

        // When
        inventory.updateQuantity(newQuantity);

        // Then
        assertEquals(newQuantity, inventory.getQuantity());
    }

    @Test
    void shouldIncrementQuantity() {
        // Given
        Inventory inventory = Inventory.create("product-123", 100);
        Integer increment = 50;

        // When
        inventory.incrementQuantity(increment);

        // Then
        assertEquals(150, inventory.getQuantity());
    }

    @Test
    void shouldDecrementQuantity() {
        // Given
        Inventory inventory = Inventory.create("product-123", 100);
        Integer decrement = 30;

        // When
        inventory.decrementQuantity(decrement);

        // Then
        assertEquals(70, inventory.getQuantity());
    }

    @Test
    void shouldThrowExceptionWhenInsufficientStock() {
        // Given
        Inventory inventory = Inventory.create("product-123", 10);
        Integer largeDecrement = 20;

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
                inventory.decrementQuantity(largeDecrement));
    }

    @Test
    void shouldCheckIfOutOfStock() {
        // Given
        Inventory inventory = Inventory.create("product-123", 0);

        // When & Then
        assertTrue(inventory.isOutOfStock());
    }

    @Test
    void shouldCheckIfHasStock() {
        // Given
        Inventory inventory = Inventory.create("product-123", 100);

        // When & Then
        assertTrue(inventory.hasStock(50));
        assertFalse(inventory.hasStock(150));
    }
}