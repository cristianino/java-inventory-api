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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Delete Inventory Use Case Tests")
class DeleteInventoryUseCaseTest {

    @Mock
    private InventoryRepository inventoryRepository;

    private DeleteInventoryUseCase deleteInventoryUseCase;

    @BeforeEach
    void setUp() {
        deleteInventoryUseCase = new DeleteInventoryUseCase(inventoryRepository);
    }

    @Nested
    @DisplayName("Delete By ID")
    class DeleteById {

        @Test
        @DisplayName("Should delete inventory when it exists")
        void shouldDeleteInventoryWhenItExists() {
            // Given
            UUID inventoryId = UUID.randomUUID();
            Inventory existingInventory = Inventory.create("product-123", 100);

            when(inventoryRepository.findById(inventoryId)).thenReturn(Optional.of(existingInventory));
            when(inventoryRepository.deleteById(inventoryId)).thenReturn(true);

            // When
            boolean result = deleteInventoryUseCase.deleteById(inventoryId);

            // Then
            assertThat(result).isTrue();
            verify(inventoryRepository).findById(inventoryId);
            verify(inventoryRepository).deleteById(inventoryId);
        }

        @Test
        @DisplayName("Should return false when deletion fails")
        void shouldReturnFalseWhenDeletionFails() {
            // Given
            UUID inventoryId = UUID.randomUUID();
            Inventory existingInventory = Inventory.create("product-123", 100);

            when(inventoryRepository.findById(inventoryId)).thenReturn(Optional.of(existingInventory));
            when(inventoryRepository.deleteById(inventoryId)).thenReturn(false);

            // When
            boolean result = deleteInventoryUseCase.deleteById(inventoryId);

            // Then
            assertThat(result).isFalse();
            verify(inventoryRepository).findById(inventoryId);
            verify(inventoryRepository).deleteById(inventoryId);
        }

        @Test
        @DisplayName("Should throw exception when inventory not found")
        void shouldThrowExceptionWhenInventoryNotFound() {
            // Given
            UUID inventoryId = UUID.randomUUID();

            when(inventoryRepository.findById(inventoryId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> deleteInventoryUseCase.deleteById(inventoryId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Inventory not found: " + inventoryId);

            verify(inventoryRepository).findById(inventoryId);
            verify(inventoryRepository, never()).deleteById(any(UUID.class));
        }

        @Test
        @DisplayName("Should handle null ID gracefully")
        void shouldHandleNullIdGracefully() {
            // Given
            UUID nullId = null;

            when(inventoryRepository.findById(nullId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> deleteInventoryUseCase.deleteById(nullId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Inventory not found: null");

            verify(inventoryRepository).findById(nullId);
        }

        @Test
        @DisplayName("Should propagate repository exception on find")
        void shouldPropagateRepositoryExceptionOnFind() {
            // Given
            UUID inventoryId = UUID.randomUUID();
            RuntimeException repositoryException = new RuntimeException("Database connection failed");

            when(inventoryRepository.findById(inventoryId)).thenThrow(repositoryException);

            // When & Then
            assertThatThrownBy(() -> deleteInventoryUseCase.deleteById(inventoryId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Database connection failed");

            verify(inventoryRepository).findById(inventoryId);
            verify(inventoryRepository, never()).deleteById(any(UUID.class));
        }

        @Test
        @DisplayName("Should propagate repository exception on delete")
        void shouldPropagateRepositoryExceptionOnDelete() {
            // Given
            UUID inventoryId = UUID.randomUUID();
            Inventory existingInventory = Inventory.create("product-123", 100);
            RuntimeException deleteException = new RuntimeException("Delete operation failed");

            when(inventoryRepository.findById(inventoryId)).thenReturn(Optional.of(existingInventory));
            when(inventoryRepository.deleteById(inventoryId)).thenThrow(deleteException);

            // When & Then
            assertThatThrownBy(() -> deleteInventoryUseCase.deleteById(inventoryId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Delete operation failed");

            verify(inventoryRepository).findById(inventoryId);
            verify(inventoryRepository).deleteById(inventoryId);
        }
    }

    @Nested
    @DisplayName("Delete By Product ID")
    class DeleteByProductId {

        @Test
        @DisplayName("Should delete inventory when it exists for product")
        void shouldDeleteInventoryWhenItExistsForProduct() {
            // Given
            String productId = "product-123";
            Inventory existingInventory = Inventory.create(productId, 100);

            when(inventoryRepository.findByProductId(productId)).thenReturn(Optional.of(existingInventory));
            when(inventoryRepository.deleteByProductId(productId)).thenReturn(true);

            // When
            boolean result = deleteInventoryUseCase.deleteByProductId(productId);

            // Then
            assertThat(result).isTrue();
            verify(inventoryRepository).findByProductId(productId);
            verify(inventoryRepository).deleteByProductId(productId);
        }

        @Test
        @DisplayName("Should return false when deletion by product ID fails")
        void shouldReturnFalseWhenDeletionByProductIdFails() {
            // Given
            String productId = "product-456";
            Inventory existingInventory = Inventory.create(productId, 100);

            when(inventoryRepository.findByProductId(productId)).thenReturn(Optional.of(existingInventory));
            when(inventoryRepository.deleteByProductId(productId)).thenReturn(false);

            // When
            boolean result = deleteInventoryUseCase.deleteByProductId(productId);

            // Then
            assertThat(result).isFalse();
            verify(inventoryRepository).findByProductId(productId);
            verify(inventoryRepository).deleteByProductId(productId);
        }

        @Test
        @DisplayName("Should throw exception when inventory not found for product")
        void shouldThrowExceptionWhenInventoryNotFoundForProduct() {
            // Given
            String productId = "non-existent-product";

            when(inventoryRepository.findByProductId(productId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> deleteInventoryUseCase.deleteByProductId(productId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Inventory not found for product: " + productId);

            verify(inventoryRepository).findByProductId(productId);
            verify(inventoryRepository, never()).deleteByProductId(anyString());
        }

        @Test
        @DisplayName("Should handle null product ID gracefully")
        void shouldHandleNullProductIdGracefully() {
            // Given
            String nullProductId = null;

            when(inventoryRepository.findByProductId(nullProductId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> deleteInventoryUseCase.deleteByProductId(nullProductId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Inventory not found for product: null");

            verify(inventoryRepository).findByProductId(nullProductId);
        }

        @Test
        @DisplayName("Should handle empty product ID")
        void shouldHandleEmptyProductId() {
            // Given
            String emptyProductId = "";

            when(inventoryRepository.findByProductId(emptyProductId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> deleteInventoryUseCase.deleteByProductId(emptyProductId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Inventory not found for product: ");

            verify(inventoryRepository).findByProductId(emptyProductId);
        }

        @Test
        @DisplayName("Should propagate repository exception on find by product ID")
        void shouldPropagateRepositoryExceptionOnFindByProductId() {
            // Given
            String productId = "product-123";
            RuntimeException repositoryException = new RuntimeException("Query execution failed");

            when(inventoryRepository.findByProductId(productId)).thenThrow(repositoryException);

            // When & Then
            assertThatThrownBy(() -> deleteInventoryUseCase.deleteByProductId(productId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Query execution failed");

            verify(inventoryRepository).findByProductId(productId);
            verify(inventoryRepository, never()).deleteByProductId(anyString());
        }

        @Test
        @DisplayName("Should propagate repository exception on delete by product ID")
        void shouldPropagateRepositoryExceptionOnDeleteByProductId() {
            // Given
            String productId = "product-123";
            Inventory existingInventory = Inventory.create(productId, 100);
            RuntimeException deleteException = new RuntimeException("Delete by product ID failed");

            when(inventoryRepository.findByProductId(productId)).thenReturn(Optional.of(existingInventory));
            when(inventoryRepository.deleteByProductId(productId)).thenThrow(deleteException);

            // When & Then
            assertThatThrownBy(() -> deleteInventoryUseCase.deleteByProductId(productId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Delete by product ID failed");

            verify(inventoryRepository).findByProductId(productId);
            verify(inventoryRepository).deleteByProductId(productId);
        }
    }

    @Nested
    @DisplayName("Edge Cases and Integration")
    class EdgeCasesAndIntegration {

        @Test
        @DisplayName("Should handle concurrent deletion attempts gracefully")
        void shouldHandleConcurrentDeletionAttemptsGracefully() {
            // Given
            UUID inventoryId = UUID.randomUUID();
            String productId = "product-concurrent";
            Inventory inventory = Inventory.create(productId, 100);

            when(inventoryRepository.findById(inventoryId)).thenReturn(Optional.of(inventory));
            when(inventoryRepository.findByProductId(productId)).thenReturn(Optional.of(inventory));
            when(inventoryRepository.deleteById(inventoryId)).thenReturn(true);
            when(inventoryRepository.deleteByProductId(productId)).thenReturn(true);

            // When
            boolean resultById = deleteInventoryUseCase.deleteById(inventoryId);
            boolean resultByProductId = deleteInventoryUseCase.deleteByProductId(productId);

            // Then
            assertThat(resultById).isTrue();
            assertThat(resultByProductId).isTrue();
        }

        @Test
        @DisplayName("Should maintain consistency between delete methods")
        void shouldMaintainConsistencyBetweenDeleteMethods() {
            // Given
            UUID inventoryId = UUID.randomUUID();
            String productId = "consistent-product";
            Inventory inventory = Inventory.create(productId, 75);

            // Test that both methods check for existence first
            when(inventoryRepository.findById(inventoryId)).thenReturn(Optional.empty());
            when(inventoryRepository.findByProductId(productId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> deleteInventoryUseCase.deleteById(inventoryId))
                    .isInstanceOf(IllegalArgumentException.class);
            
            assertThatThrownBy(() -> deleteInventoryUseCase.deleteByProductId(productId))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}