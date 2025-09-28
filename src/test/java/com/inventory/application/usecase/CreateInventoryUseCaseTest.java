package com.inventory.application.usecase;

import com.inventory.domain.model.Inventory;
import com.inventory.domain.port.InventoryRepository;
import com.inventory.domain.port.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Create Inventory Use Case Tests")
class CreateInventoryUseCaseTest {

    @Mock
    private InventoryRepository inventoryRepository;
    
    @Mock
    private ProductService productService;

    private CreateInventoryUseCase createInventoryUseCase;

    @BeforeEach
    void setUp() {
        createInventoryUseCase = new CreateInventoryUseCase(inventoryRepository, productService);
    }

    @Nested
    @DisplayName("Successful Creation")
    class SuccessfulCreation {

        @Test
        @DisplayName("Should create inventory when product exists and no inventory exists")
        void shouldCreateInventoryWhenProductExistsAndNoInventoryExists() {
            // Given
            String productId = "product-123";
            Integer quantity = 100;
            Inventory expectedInventory = Inventory.create(productId, quantity);

            when(productService.existsById(productId)).thenReturn(true);
            when(inventoryRepository.findByProductId(productId)).thenReturn(Optional.empty());
            when(inventoryRepository.save(any(Inventory.class))).thenReturn(expectedInventory);

            // When
            Inventory result = createInventoryUseCase.execute(productId, quantity);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getProductId()).isEqualTo(productId);
            assertThat(result.getQuantity()).isEqualTo(quantity);
            
            verify(productService).existsById(productId);
            verify(inventoryRepository).findByProductId(productId);
            verify(inventoryRepository).save(any(Inventory.class));
        }

        @Test
        @DisplayName("Should create inventory with zero quantity")
        void shouldCreateInventoryWithZeroQuantity() {
            // Given
            String productId = "product-456";
            Integer quantity = 0;
            Inventory expectedInventory = Inventory.create(productId, quantity);

            when(productService.existsById(productId)).thenReturn(true);
            when(inventoryRepository.findByProductId(productId)).thenReturn(Optional.empty());
            when(inventoryRepository.save(any(Inventory.class))).thenReturn(expectedInventory);

            // When
            Inventory result = createInventoryUseCase.execute(productId, quantity);

            // Then
            assertThat(result.getQuantity()).isZero();
            verify(inventoryRepository).save(any(Inventory.class));
        }

        @Test
        @DisplayName("Should create inventory with large quantity")
        void shouldCreateInventoryWithLargeQuantity() {
            // Given
            String productId = "product-789";
            Integer quantity = 999999;

            when(productService.existsById(productId)).thenReturn(true);
            when(inventoryRepository.findByProductId(productId)).thenReturn(Optional.empty());
            when(inventoryRepository.save(any(Inventory.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            Inventory result = createInventoryUseCase.execute(productId, quantity);

            // Then
            assertThat(result.getQuantity()).isEqualTo(quantity);
        }
    }

    @Nested
    @DisplayName("Validation Errors")
    class ValidationErrors {

        @Test
        @DisplayName("Should throw exception when product does not exist")
        void shouldThrowExceptionWhenProductDoesNotExist() {
            // Given
            String productId = "non-existent-product";
            Integer quantity = 100;

            when(productService.existsById(productId)).thenReturn(false);

            // When & Then
            assertThatThrownBy(() -> createInventoryUseCase.execute(productId, quantity))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Product does not exist: " + productId);

            verify(productService).existsById(productId);
            verify(inventoryRepository, never()).findByProductId(anyString());
            verify(inventoryRepository, never()).save(any(Inventory.class));
        }

        @Test
        @DisplayName("Should throw exception when inventory already exists")
        void shouldThrowExceptionWhenInventoryAlreadyExists() {
            // Given
            String productId = "existing-product";
            Integer quantity = 100;
            Inventory existingInventory = Inventory.create(productId, 50);

            when(productService.existsById(productId)).thenReturn(true);
            when(inventoryRepository.findByProductId(productId)).thenReturn(Optional.of(existingInventory));

            // When & Then
            assertThatThrownBy(() -> createInventoryUseCase.execute(productId, quantity))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Inventory already exists for product: " + productId);

            verify(productService).existsById(productId);
            verify(inventoryRepository).findByProductId(productId);
            verify(inventoryRepository, never()).save(any(Inventory.class));
        }

        @Test
        @DisplayName("Should throw exception when quantity is negative")
        void shouldThrowExceptionWhenQuantityIsNegative() {
            // Given
            String productId = "product-123";
            Integer negativeQuantity = -10;

            when(productService.existsById(productId)).thenReturn(true);
            when(inventoryRepository.findByProductId(productId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> createInventoryUseCase.execute(productId, negativeQuantity))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Quantity cannot be negative");

            verify(productService).existsById(productId);
            verify(inventoryRepository).findByProductId(productId);
            verify(inventoryRepository, never()).save(any(Inventory.class));
        }
    }

    @Nested
    @DisplayName("External Service Failures")
    class ExternalServiceFailures {

        @Test
        @DisplayName("Should propagate exception when product service fails")
        void shouldPropagateExceptionWhenProductServiceFails() {
            // Given
            String productId = "product-123";
            Integer quantity = 100;
            RuntimeException serviceException = new RuntimeException("Product service unavailable");

            when(productService.existsById(productId)).thenThrow(serviceException);

            // When & Then
            assertThatThrownBy(() -> createInventoryUseCase.execute(productId, quantity))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Product service unavailable");

            verify(productService).existsById(productId);
            verify(inventoryRepository, never()).findByProductId(anyString());
            verify(inventoryRepository, never()).save(any(Inventory.class));
        }

        @Test
        @DisplayName("Should propagate exception when repository fails")
        void shouldPropagateExceptionWhenRepositoryFails() {
            // Given
            String productId = "product-123";
            Integer quantity = 100;
            RuntimeException repositoryException = new RuntimeException("Database connection failed");

            when(productService.existsById(productId)).thenReturn(true);
            when(inventoryRepository.findByProductId(productId)).thenThrow(repositoryException);

            // When & Then
            assertThatThrownBy(() -> createInventoryUseCase.execute(productId, quantity))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Database connection failed");

            verify(productService).existsById(productId);
            verify(inventoryRepository).findByProductId(productId);
            verify(inventoryRepository, never()).save(any(Inventory.class));
        }

        @Test
        @DisplayName("Should propagate exception when save fails")
        void shouldPropagateExceptionWhenSaveFails() {
            // Given
            String productId = "product-123";
            Integer quantity = 100;
            RuntimeException saveException = new RuntimeException("Save operation failed");

            when(productService.existsById(productId)).thenReturn(true);
            when(inventoryRepository.findByProductId(productId)).thenReturn(Optional.empty());
            when(inventoryRepository.save(any(Inventory.class))).thenThrow(saveException);

            // When & Then
            assertThatThrownBy(() -> createInventoryUseCase.execute(productId, quantity))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Save operation failed");

            verify(inventoryRepository).save(any(Inventory.class));
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("Should handle null product id")
        void shouldHandleNullProductId() {
            // Given
            String productId = null;
            Integer quantity = 100;

            // When & Then
            assertThatThrownBy(() -> createInventoryUseCase.execute(productId, quantity))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Product does not exist: null");
        }

        @Test
        @DisplayName("Should handle empty product id")
        void shouldHandleEmptyProductId() {
            // Given
            String productId = "";
            Integer quantity = 100;

            when(productService.existsById(productId)).thenReturn(false);

            // When & Then
            assertThatThrownBy(() -> createInventoryUseCase.execute(productId, quantity))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Product does not exist: ");
        }

        @Test
        @DisplayName("Should handle null quantity")
        void shouldHandleNullQuantity() {
            // Given
            String productId = "product-123";
            Integer quantity = null;

            when(productService.existsById(productId)).thenReturn(true);
            when(inventoryRepository.findByProductId(productId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> createInventoryUseCase.execute(productId, quantity))
                    .isInstanceOf(NullPointerException.class);
        }
    }
}