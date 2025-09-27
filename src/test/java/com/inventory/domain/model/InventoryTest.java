package com.inventory.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Inventory Domain Model Tests")
class InventoryTest {

    @Nested
    @DisplayName("Inventory Creation")
    class InventoryCreation {

        @Test
        @DisplayName("Should create inventory with factory method")
        void shouldCreateInventoryWithFactoryMethod() {
            // Given
            String productId = "product-123";
            Integer quantity = 100;

            // When
            Inventory inventory = Inventory.create(productId, quantity);

            // Then
            assertThat(inventory.getId()).isNotNull();
            assertThat(inventory.getProductId()).isEqualTo(productId);
            assertThat(inventory.getQuantity()).isEqualTo(quantity);
            assertThat(inventory.getCreatedAt()).isNotNull();
            assertThat(inventory.getUpdatedAt()).isNotNull();
            assertThat(inventory.getCreatedAt()).isEqualTo(inventory.getUpdatedAt());
        }

        @Test
        @DisplayName("Should create inventory with constructor")
        void shouldCreateInventoryWithConstructor() {
            // Given
            UUID id = UUID.randomUUID();
            String productId = "product-456";
            Integer quantity = 50;
            LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
            LocalDateTime updatedAt = LocalDateTime.now();

            // When
            Inventory inventory = new Inventory(id, productId, quantity, createdAt, updatedAt);

            // Then
            assertThat(inventory.getId()).isEqualTo(id);
            assertThat(inventory.getProductId()).isEqualTo(productId);
            assertThat(inventory.getQuantity()).isEqualTo(quantity);
            assertThat(inventory.getCreatedAt()).isEqualTo(createdAt);
            assertThat(inventory.getUpdatedAt()).isEqualTo(updatedAt);
        }

        @Test
        @DisplayName("Should create inventory with zero quantity")
        void shouldCreateInventoryWithZeroQuantity() {
            // Given
            String productId = "product-zero";
            Integer quantity = 0;

            // When
            Inventory inventory = Inventory.create(productId, quantity);

            // Then
            assertThat(inventory.getQuantity()).isZero();
        }

        @Test
        @DisplayName("Should throw exception when id is null")
        void shouldThrowExceptionWhenIdIsNull() {
            // Given
            UUID id = null;
            String productId = "product-123";
            Integer quantity = 10;
            LocalDateTime now = LocalDateTime.now();

            // When & Then
            assertThatThrownBy(() -> new Inventory(id, productId, quantity, now, now))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("ID cannot be null");
        }

        @Test
        @DisplayName("Should throw exception when product id is null")
        void shouldThrowExceptionWhenProductIdIsNull() {
            // Given
            UUID id = UUID.randomUUID();
            String productId = null;
            Integer quantity = 10;
            LocalDateTime now = LocalDateTime.now();

            // When & Then
            assertThatThrownBy(() -> new Inventory(id, productId, quantity, now, now))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("Product ID cannot be null");
        }

        @Test
        @DisplayName("Should throw exception when quantity is null")
        void shouldThrowExceptionWhenQuantityIsNull() {
            // Given
            UUID id = UUID.randomUUID();
            String productId = "product-123";
            Integer quantity = null;
            LocalDateTime now = LocalDateTime.now();

            // When & Then
            assertThatThrownBy(() -> new Inventory(id, productId, quantity, now, now))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("Quantity cannot be null");
        }

        @Test
        @DisplayName("Should throw exception when created at is null")
        void shouldThrowExceptionWhenCreatedAtIsNull() {
            // Given
            UUID id = UUID.randomUUID();
            String productId = "product-123";
            Integer quantity = 10;
            LocalDateTime createdAt = null;
            LocalDateTime updatedAt = LocalDateTime.now();

            // When & Then
            assertThatThrownBy(() -> new Inventory(id, productId, quantity, createdAt, updatedAt))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("Created at cannot be null");
        }

        @Test
        @DisplayName("Should throw exception when updated at is null")
        void shouldThrowExceptionWhenUpdatedAtIsNull() {
            // Given
            UUID id = UUID.randomUUID();
            String productId = "product-123";
            Integer quantity = 10;
            LocalDateTime createdAt = LocalDateTime.now();
            LocalDateTime updatedAt = null;

            // When & Then
            assertThatThrownBy(() -> new Inventory(id, productId, quantity, createdAt, updatedAt))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("Updated at cannot be null");
        }

        @Test
        @DisplayName("Should throw exception for negative quantity in constructor")
        void shouldThrowExceptionForNegativeQuantityInConstructor() {
            // Given
            UUID id = UUID.randomUUID();
            String productId = "product-123";
            Integer negativeQuantity = -1;
            LocalDateTime now = LocalDateTime.now();

            // When & Then
            assertThatThrownBy(() -> new Inventory(id, productId, negativeQuantity, now, now))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Quantity cannot be negative");
        }

        @Test
        @DisplayName("Should throw exception for negative quantity in factory method")
        void shouldThrowExceptionForNegativeQuantityInFactoryMethod() {
            // Given
            String productId = "product-123";
            Integer negativeQuantity = -5;

            // When & Then
            assertThatThrownBy(() -> Inventory.create(productId, negativeQuantity))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Quantity cannot be negative");
        }
    }

    @Nested
    @DisplayName("Quantity Updates")
    class QuantityUpdates {

        @Test
        @DisplayName("Should update quantity successfully")
        void shouldUpdateQuantitySuccessfully() {
            // Given
            Inventory inventory = Inventory.create("product-123", 100);
            LocalDateTime originalUpdatedAt = inventory.getUpdatedAt();
            Integer newQuantity = 150;

            // When
            inventory.updateQuantity(newQuantity);

            // Then
            assertThat(inventory.getQuantity()).isEqualTo(newQuantity);
            assertThat(inventory.getUpdatedAt()).isAfter(originalUpdatedAt);
        }

        @Test
        @DisplayName("Should update quantity to zero")
        void shouldUpdateQuantityToZero() {
            // Given
            Inventory inventory = Inventory.create("product-123", 100);
            Integer newQuantity = 0;

            // When
            inventory.updateQuantity(newQuantity);

            // Then
            assertThat(inventory.getQuantity()).isZero();
            assertThat(inventory.isOutOfStock()).isTrue();
        }

        @Test
        @DisplayName("Should throw exception when updating to negative quantity")
        void shouldThrowExceptionWhenUpdatingToNegativeQuantity() {
            // Given
            Inventory inventory = Inventory.create("product-123", 100);
            Integer negativeQuantity = -10;

            // When & Then
            assertThatThrownBy(() -> inventory.updateQuantity(negativeQuantity))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Quantity cannot be negative");
        }

        @Test
        @DisplayName("Should increment quantity successfully")
        void shouldIncrementQuantitySuccessfully() {
            // Given
            Inventory inventory = Inventory.create("product-123", 100);
            LocalDateTime originalUpdatedAt = inventory.getUpdatedAt();
            Integer increment = 50;

            // When
            inventory.incrementQuantity(increment);

            // Then
            assertThat(inventory.getQuantity()).isEqualTo(150);
            assertThat(inventory.getUpdatedAt()).isAfter(originalUpdatedAt);
        }

        @Test
        @DisplayName("Should increment quantity from zero")
        void shouldIncrementQuantityFromZero() {
            // Given
            Inventory inventory = Inventory.create("product-123", 0);
            Integer increment = 25;

            // When
            inventory.incrementQuantity(increment);

            // Then
            assertThat(inventory.getQuantity()).isEqualTo(25);
            assertThat(inventory.isOutOfStock()).isFalse();
        }

        @Test
        @DisplayName("Should throw exception when increment amount is negative")
        void shouldThrowExceptionWhenIncrementAmountIsNegative() {
            // Given
            Inventory inventory = Inventory.create("product-123", 100);
            Integer negativeIncrement = -5;

            // When & Then
            assertThatThrownBy(() -> inventory.incrementQuantity(negativeIncrement))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Increment amount cannot be negative");
        }

        @Test
        @DisplayName("Should decrement quantity successfully")
        void shouldDecrementQuantitySuccessfully() {
            // Given
            Inventory inventory = Inventory.create("product-123", 100);
            LocalDateTime originalUpdatedAt = inventory.getUpdatedAt();
            Integer decrement = 30;

            // When
            inventory.decrementQuantity(decrement);

            // Then
            assertThat(inventory.getQuantity()).isEqualTo(70);
            assertThat(inventory.getUpdatedAt()).isAfter(originalUpdatedAt);
        }

        @Test
        @DisplayName("Should decrement quantity to zero")
        void shouldDecrementQuantityToZero() {
            // Given
            Inventory inventory = Inventory.create("product-123", 50);
            Integer decrement = 50;

            // When
            inventory.decrementQuantity(decrement);

            // Then
            assertThat(inventory.getQuantity()).isZero();
            assertThat(inventory.isOutOfStock()).isTrue();
        }

        @Test
        @DisplayName("Should throw exception when decrement amount is negative")
        void shouldThrowExceptionWhenDecrementAmountIsNegative() {
            // Given
            Inventory inventory = Inventory.create("product-123", 100);
            Integer negativeDecrement = -10;

            // When & Then
            assertThatThrownBy(() -> inventory.decrementQuantity(negativeDecrement))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Decrement amount cannot be negative");
        }

        @Test
        @DisplayName("Should throw exception when insufficient inventory for decrement")
        void shouldThrowExceptionWhenInsufficientInventoryForDecrement() {
            // Given
            Inventory inventory = Inventory.create("product-123", 30);
            Integer largeDecrement = 50;

            // When & Then
            assertThatThrownBy(() -> inventory.decrementQuantity(largeDecrement))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Insufficient inventory quantity");
        }
    }

    @Nested
    @DisplayName("Stock Validation")
    class StockValidation {

        @Test
        @DisplayName("Should return true when inventory is out of stock")
        void shouldReturnTrueWhenInventoryIsOutOfStock() {
            // Given
            Inventory inventory = Inventory.create("product-123", 0);

            // When & Then
            assertThat(inventory.isOutOfStock()).isTrue();
        }

        @Test
        @DisplayName("Should return false when inventory has stock")
        void shouldReturnFalseWhenInventoryHasStock() {
            // Given
            Inventory inventory = Inventory.create("product-123", 10);

            // When & Then
            assertThat(inventory.isOutOfStock()).isFalse();
        }

        @Test
        @DisplayName("Should return true when has sufficient stock")
        void shouldReturnTrueWhenHasSufficientStock() {
            // Given
            Inventory inventory = Inventory.create("product-123", 100);
            Integer requiredQuantity = 50;

            // When & Then
            assertThat(inventory.hasStock(requiredQuantity)).isTrue();
        }

        @Test
        @DisplayName("Should return true when has exact required stock")
        void shouldReturnTrueWhenHasExactRequiredStock() {
            // Given
            Inventory inventory = Inventory.create("product-123", 75);
            Integer requiredQuantity = 75;

            // When & Then
            assertThat(inventory.hasStock(requiredQuantity)).isTrue();
        }

        @Test
        @DisplayName("Should return false when has insufficient stock")
        void shouldReturnFalseWhenHasInsufficientStock() {
            // Given
            Inventory inventory = Inventory.create("product-123", 25);
            Integer requiredQuantity = 50;

            // When & Then
            assertThat(inventory.hasStock(requiredQuantity)).isFalse();
        }

        @Test
        @DisplayName("Should return false when checking stock on empty inventory")
        void shouldReturnFalseWhenCheckingStockOnEmptyInventory() {
            // Given
            Inventory inventory = Inventory.create("product-123", 0);
            Integer requiredQuantity = 1;

            // When & Then
            assertThat(inventory.hasStock(requiredQuantity)).isFalse();
        }
    }

    @Nested
    @DisplayName("Equality and Hash")
    class EqualityAndHash {

        @Test
        @DisplayName("Should be equal when inventories have same id")
        void shouldBeEqualWhenInventoriesHaveSameId() {
            // Given
            UUID id = UUID.randomUUID();
            LocalDateTime now = LocalDateTime.now();
            Inventory inventory1 = new Inventory(id, "product-1", 100, now, now);
            Inventory inventory2 = new Inventory(id, "product-2", 200, now.minusDays(1), now.plusDays(1));

            // When & Then
            assertThat(inventory1).isEqualTo(inventory2);
            assertThat(inventory1.hashCode()).isEqualTo(inventory2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when inventories have different ids")
        void shouldNotBeEqualWhenInventoriesHaveDifferentIds() {
            // Given
            LocalDateTime now = LocalDateTime.now();
            Inventory inventory1 = new Inventory(UUID.randomUUID(), "product-1", 100, now, now);
            Inventory inventory2 = new Inventory(UUID.randomUUID(), "product-1", 100, now, now);

            // When & Then
            assertThat(inventory1).isNotEqualTo(inventory2);
        }

        @Test
        @DisplayName("Should not be equal to null")
        void shouldNotBeEqualToNull() {
            // Given
            Inventory inventory = Inventory.create("product-123", 100);

            // When & Then
            assertThat(inventory).isNotEqualTo(null);
        }

        @Test
        @DisplayName("Should not be equal to different class")
        void shouldNotBeEqualToDifferentClass() {
            // Given
            Inventory inventory = Inventory.create("product-123", 100);
            String otherObject = "Not an inventory";

            // When & Then
            assertThat(inventory).isNotEqualTo(otherObject);
        }

        @Test
        @DisplayName("Should be equal to itself")
        void shouldBeEqualToItself() {
            // Given
            Inventory inventory = Inventory.create("product-123", 100);

            // When & Then
            assertThat(inventory).isEqualTo(inventory);
        }
    }

    @Nested
    @DisplayName("ToString")
    class ToString {

        @Test
        @DisplayName("Should return string representation with all fields")
        void shouldReturnStringRepresentation() {
            // Given
            Inventory inventory = Inventory.create("product-123", 100);

            // When
            String result = inventory.toString();

            // Then
            assertThat(result)
                    .contains("Inventory")
                    .contains(inventory.getId().toString())
                    .contains("product-123")
                    .contains("100")
                    .contains(inventory.getCreatedAt().toString())
                    .contains(inventory.getUpdatedAt().toString());
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @ParameterizedTest
        @ValueSource(ints = {0, 1, 100, 999999})
        @DisplayName("Should handle various valid quantities")
        void shouldHandleVariousValidQuantities(Integer quantity) {
            // Given & When
            Inventory inventory = Inventory.create("product-test", quantity);

            // Then
            assertThat(inventory.getQuantity()).isEqualTo(quantity);
        }

        @Test
        @DisplayName("Should handle very large quantities")
        void shouldHandleVeryLargeQuantities() {
            // Given
            Integer largeQuantity = Integer.MAX_VALUE;

            // When
            Inventory inventory = Inventory.create("product-123", largeQuantity);

            // Then
            assertThat(inventory.getQuantity()).isEqualTo(largeQuantity);
        }

        @Test
        @DisplayName("Should handle empty product id")
        void shouldHandleEmptyProductId() {
            // Given
            String emptyProductId = "";

            // When
            Inventory inventory = Inventory.create(emptyProductId, 100);

            // Then
            assertThat(inventory.getProductId()).isEmpty();
        }

        @Test
        @DisplayName("Should handle very long product id")
        void shouldHandleVeryLongProductId() {
            // Given
            String longProductId = "VERY_LONG_PRODUCT_ID_" + "X".repeat(1000);

            // When
            Inventory inventory = Inventory.create(longProductId, 100);

            // Then
            assertThat(inventory.getProductId()).isEqualTo(longProductId);
        }
    }
}