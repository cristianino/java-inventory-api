package com.inventory.application.usecase;

import com.inventory.domain.model.Inventory;
import com.inventory.domain.port.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Update Inventory Use Case Tests")
class UpdateInventoryUseCaseTest {

    @Mock
    private InventoryRepository inventoryRepository;

    private UpdateInventoryUseCase updateInventoryUseCase;

    @BeforeEach
    void setUp() {
        updateInventoryUseCase = new UpdateInventoryUseCase(inventoryRepository);
    }

    @Nested
    @DisplayName("Update Quantity By ID")
    class UpdateQuantityById {

        @Test
        @DisplayName("Should update quantity when inventory exists")
        void shouldUpdateQuantityWhenInventoryExists() {
            // Given
            UUID inventoryId = UUID.randomUUID();
            Integer newQuantity = 150;
            Inventory originalInventory = Inventory.create("product-123", 100);
            Inventory updatedInventory = Inventory.create("product-123", newQuantity);

            when(inventoryRepository.findById(inventoryId)).thenReturn(Optional.of(originalInventory));
            when(inventoryRepository.save(any(Inventory.class))).thenReturn(updatedInventory);

            // When
            Inventory result = updateInventoryUseCase.updateQuantity(inventoryId, newQuantity);

            // Then
            assertThat(result.getQuantity()).isEqualTo(newQuantity);
            verify(inventoryRepository).findById(inventoryId);
            verify(inventoryRepository).save(any(Inventory.class));
        }

        @Test
        @DisplayName("Should throw exception when inventory not found")
        void shouldThrowExceptionWhenInventoryNotFound() {
            // Given
            UUID inventoryId = UUID.randomUUID();
            Integer newQuantity = 150;

            when(inventoryRepository.findById(inventoryId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> updateInventoryUseCase.updateQuantity(inventoryId, newQuantity))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Inventory not found: " + inventoryId);

            verify(inventoryRepository).findById(inventoryId);
            verify(inventoryRepository, never()).save(any(Inventory.class));
        }

        @Test
        @DisplayName("Should throw exception when updating to negative quantity")
        void shouldThrowExceptionWhenUpdatingToNegativeQuantity() {
            // Given
            UUID inventoryId = UUID.randomUUID();
            Integer negativeQuantity = -10;
            Inventory inventory = Inventory.create("product-123", 100);

            when(inventoryRepository.findById(inventoryId)).thenReturn(Optional.of(inventory));

            // When & Then
            assertThatThrownBy(() -> updateInventoryUseCase.updateQuantity(inventoryId, negativeQuantity))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Quantity cannot be negative");

            verify(inventoryRepository).findById(inventoryId);
            verify(inventoryRepository, never()).save(any(Inventory.class));
        }
    }

    @Nested
    @DisplayName("Update Quantity By Product ID")
    class UpdateQuantityByProductId {

        @Test
        @DisplayName("Should update quantity when inventory exists for product")
        void shouldUpdateQuantityWhenInventoryExistsForProduct() {
            // Given
            String productId = "product-123";
            Integer newQuantity = 200;
            Inventory originalInventory = Inventory.create(productId, 100);
            Inventory updatedInventory = Inventory.create(productId, newQuantity);

            when(inventoryRepository.findByProductId(productId)).thenReturn(Optional.of(originalInventory));
            when(inventoryRepository.save(any(Inventory.class))).thenReturn(updatedInventory);

            // When
            Inventory result = updateInventoryUseCase.updateQuantityByProductId(productId, newQuantity);

            // Then
            assertThat(result.getQuantity()).isEqualTo(newQuantity);
            assertThat(result.getProductId()).isEqualTo(productId);
            verify(inventoryRepository).findByProductId(productId);
            verify(inventoryRepository).save(any(Inventory.class));
        }

        @Test
        @DisplayName("Should throw exception when inventory not found for product")
        void shouldThrowExceptionWhenInventoryNotFoundForProduct() {
            // Given
            String productId = "non-existent-product";
            Integer newQuantity = 150;

            when(inventoryRepository.findByProductId(productId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> updateInventoryUseCase.updateQuantityByProductId(productId, newQuantity))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Inventory not found for product: " + productId);

            verify(inventoryRepository).findByProductId(productId);
            verify(inventoryRepository, never()).save(any(Inventory.class));
        }
    }

    @Nested
    @DisplayName("Increment Quantity")
    class IncrementQuantity {

        @Test
        @DisplayName("Should increment quantity successfully")
        void shouldIncrementQuantitySuccessfully() {
            // Given
            UUID inventoryId = UUID.randomUUID();
            Integer incrementAmount = 50;
            Inventory originalInventory = Inventory.create("product-123", 100);

            when(inventoryRepository.findById(inventoryId)).thenReturn(Optional.of(originalInventory));
            when(inventoryRepository.save(any(Inventory.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            Inventory result = updateInventoryUseCase.incrementQuantity(inventoryId, incrementAmount);

            // Then
            assertThat(result.getQuantity()).isEqualTo(150);
            verify(inventoryRepository).findById(inventoryId);
            verify(inventoryRepository).save(any(Inventory.class));
        }

        @Test
        @DisplayName("Should throw exception when inventory not found for increment")
        void shouldThrowExceptionWhenInventoryNotFoundForIncrement() {
            // Given
            UUID inventoryId = UUID.randomUUID();
            Integer incrementAmount = 50;

            when(inventoryRepository.findById(inventoryId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> updateInventoryUseCase.incrementQuantity(inventoryId, incrementAmount))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Inventory not found: " + inventoryId);
        }

        @Test
        @DisplayName("Should throw exception when increment amount is negative")
        void shouldThrowExceptionWhenIncrementAmountIsNegative() {
            // Given
            UUID inventoryId = UUID.randomUUID();
            Integer negativeIncrement = -10;
            Inventory inventory = Inventory.create("product-123", 100);

            when(inventoryRepository.findById(inventoryId)).thenReturn(Optional.of(inventory));

            // When & Then
            assertThatThrownBy(() -> updateInventoryUseCase.incrementQuantity(inventoryId, negativeIncrement))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Increment amount cannot be negative");
        }
    }

    @Nested
    @DisplayName("Decrement Quantity")
    class DecrementQuantity {

        @Test
        @DisplayName("Should decrement quantity successfully")
        void shouldDecrementQuantitySuccessfully() {
            // Given
            UUID inventoryId = UUID.randomUUID();
            Integer decrementAmount = 30;
            Inventory originalInventory = Inventory.create("product-123", 100);

            when(inventoryRepository.findById(inventoryId)).thenReturn(Optional.of(originalInventory));
            when(inventoryRepository.save(any(Inventory.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            Inventory result = updateInventoryUseCase.decrementQuantity(inventoryId, decrementAmount);

            // Then
            assertThat(result.getQuantity()).isEqualTo(70);
            verify(inventoryRepository).findById(inventoryId);
            verify(inventoryRepository).save(any(Inventory.class));
        }

        @Test
        @DisplayName("Should throw exception when inventory not found for decrement")
        void shouldThrowExceptionWhenInventoryNotFoundForDecrement() {
            // Given
            UUID inventoryId = UUID.randomUUID();
            Integer decrementAmount = 30;

            when(inventoryRepository.findById(inventoryId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> updateInventoryUseCase.decrementQuantity(inventoryId, decrementAmount))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Inventory not found: " + inventoryId);
        }

        @Test
        @DisplayName("Should throw exception when insufficient inventory")
        void shouldThrowExceptionWhenInsufficientInventory() {
            // Given
            UUID inventoryId = UUID.randomUUID();
            Integer decrementAmount = 150;
            Inventory inventory = Inventory.create("product-123", 100);

            when(inventoryRepository.findById(inventoryId)).thenReturn(Optional.of(inventory));

            // When & Then
            assertThatThrownBy(() -> updateInventoryUseCase.decrementQuantity(inventoryId, decrementAmount))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Insufficient inventory quantity");
        }

        @Test
        @DisplayName("Should throw exception when decrement amount is negative")
        void shouldThrowExceptionWhenDecrementAmountIsNegative() {
            // Given
            UUID inventoryId = UUID.randomUUID();
            Integer negativeDecrement = -20;
            Inventory inventory = Inventory.create("product-123", 100);

            when(inventoryRepository.findById(inventoryId)).thenReturn(Optional.of(inventory));

            // When & Then
            assertThatThrownBy(() -> updateInventoryUseCase.decrementQuantity(inventoryId, negativeDecrement))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Decrement amount cannot be negative");
        }
    }
}