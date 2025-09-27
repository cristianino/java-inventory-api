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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Get Inventory Use Case Tests")
class GetInventoryUseCaseTest {

    @Mock
    private InventoryRepository inventoryRepository;

    private GetInventoryUseCase getInventoryUseCase;

    @BeforeEach
    void setUp() {
        getInventoryUseCase = new GetInventoryUseCase(inventoryRepository);
    }

    @Nested
    @DisplayName("Find By ID")
    class FindById {

        @Test
        @DisplayName("Should return inventory when found by ID")
        void shouldReturnInventoryWhenFoundById() {
            // Given
            UUID inventoryId = UUID.randomUUID();
            Inventory expectedInventory = Inventory.create("product-123", 100);
            
            when(inventoryRepository.findById(inventoryId)).thenReturn(Optional.of(expectedInventory));

            // When
            Optional<Inventory> result = getInventoryUseCase.findById(inventoryId);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(expectedInventory);
            verify(inventoryRepository).findById(inventoryId);
        }

        @Test
        @DisplayName("Should return empty when inventory not found by ID")
        void shouldReturnEmptyWhenInventoryNotFoundById() {
            // Given
            UUID inventoryId = UUID.randomUUID();
            
            when(inventoryRepository.findById(inventoryId)).thenReturn(Optional.empty());

            // When
            Optional<Inventory> result = getInventoryUseCase.findById(inventoryId);

            // Then
            assertThat(result).isEmpty();
            verify(inventoryRepository).findById(inventoryId);
        }

        @Test
        @DisplayName("Should handle null ID")
        void shouldHandleNullId() {
            // Given
            UUID nullId = null;

            // When
            Optional<Inventory> result = getInventoryUseCase.findById(nullId);

            // Then
            verify(inventoryRepository).findById(nullId);
        }

        @Test
        @DisplayName("Should propagate repository exception")
        void shouldPropagateRepositoryException() {
            // Given
            UUID inventoryId = UUID.randomUUID();
            RuntimeException repositoryException = new RuntimeException("Database connection failed");
            
            when(inventoryRepository.findById(inventoryId)).thenThrow(repositoryException);

            // When & Then
            assertThatThrownBy(() -> getInventoryUseCase.findById(inventoryId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Database connection failed");

            verify(inventoryRepository).findById(inventoryId);
        }
    }

    @Nested
    @DisplayName("Find By Product ID")
    class FindByProductId {

        @Test
        @DisplayName("Should return inventory when found by product ID")
        void shouldReturnInventoryWhenFoundByProductId() {
            // Given
            String productId = "product-123";
            Inventory expectedInventory = Inventory.create(productId, 100);
            
            when(inventoryRepository.findByProductId(productId)).thenReturn(Optional.of(expectedInventory));

            // When
            Optional<Inventory> result = getInventoryUseCase.findByProductId(productId);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(expectedInventory);
            assertThat(result.get().getProductId()).isEqualTo(productId);
            verify(inventoryRepository).findByProductId(productId);
        }

        @Test
        @DisplayName("Should return empty when inventory not found by product ID")
        void shouldReturnEmptyWhenInventoryNotFoundByProductId() {
            // Given
            String productId = "non-existent-product";
            
            when(inventoryRepository.findByProductId(productId)).thenReturn(Optional.empty());

            // When
            Optional<Inventory> result = getInventoryUseCase.findByProductId(productId);

            // Then
            assertThat(result).isEmpty();
            verify(inventoryRepository).findByProductId(productId);
        }

        @Test
        @DisplayName("Should handle null product ID")
        void shouldHandleNullProductId() {
            // Given
            String nullProductId = null;

            // When
            Optional<Inventory> result = getInventoryUseCase.findByProductId(nullProductId);

            // Then
            verify(inventoryRepository).findByProductId(nullProductId);
        }

        @Test
        @DisplayName("Should handle empty product ID")
        void shouldHandleEmptyProductId() {
            // Given
            String emptyProductId = "";
            
            when(inventoryRepository.findByProductId(emptyProductId)).thenReturn(Optional.empty());

            // When
            Optional<Inventory> result = getInventoryUseCase.findByProductId(emptyProductId);

            // Then
            assertThat(result).isEmpty();
            verify(inventoryRepository).findByProductId(emptyProductId);
        }

        @Test
        @DisplayName("Should propagate repository exception")
        void shouldPropagateRepositoryException() {
            // Given
            String productId = "product-123";
            RuntimeException repositoryException = new RuntimeException("Database timeout");
            
            when(inventoryRepository.findByProductId(productId)).thenThrow(repositoryException);

            // When & Then
            assertThatThrownBy(() -> getInventoryUseCase.findByProductId(productId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Database timeout");

            verify(inventoryRepository).findByProductId(productId);
        }
    }

    @Nested
    @DisplayName("Find All")
    class FindAll {

        @Test
        @DisplayName("Should return all inventories when found")
        void shouldReturnAllInventoriesWhenFound() {
            // Given
            List<Inventory> expectedInventories = Arrays.asList(
                    Inventory.create("product-1", 100),
                    Inventory.create("product-2", 200),
                    Inventory.create("product-3", 50)
            );
            
            when(inventoryRepository.findAll()).thenReturn(expectedInventories);

            // When
            List<Inventory> result = getInventoryUseCase.findAll();

            // Then
            assertThat(result).hasSize(3);
            assertThat(result).containsExactlyElementsOf(expectedInventories);
            verify(inventoryRepository).findAll();
        }

        @Test
        @DisplayName("Should return empty list when no inventories found")
        void shouldReturnEmptyListWhenNoInventoriesFound() {
            // Given
            when(inventoryRepository.findAll()).thenReturn(List.of());

            // When
            List<Inventory> result = getInventoryUseCase.findAll();

            // Then
            assertThat(result).isEmpty();
            verify(inventoryRepository).findAll();
        }

        @Test
        @DisplayName("Should return single inventory in list")
        void shouldReturnSingleInventoryInList() {
            // Given
            Inventory singleInventory = Inventory.create("product-only", 75);
            List<Inventory> expectedInventories = List.of(singleInventory);
            
            when(inventoryRepository.findAll()).thenReturn(expectedInventories);

            // When
            List<Inventory> result = getInventoryUseCase.findAll();

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0)).isEqualTo(singleInventory);
            verify(inventoryRepository).findAll();
        }

        @Test
        @DisplayName("Should propagate repository exception")
        void shouldPropagateRepositoryException() {
            // Given
            RuntimeException repositoryException = new RuntimeException("Database connection lost");
            
            when(inventoryRepository.findAll()).thenThrow(repositoryException);

            // When & Then
            assertThatThrownBy(() -> getInventoryUseCase.findAll())
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Database connection lost");

            verify(inventoryRepository).findAll();
        }
    }

    @Nested
    @DisplayName("Integration Scenarios")
    class IntegrationScenarios {

        @Test
        @DisplayName("Should handle multiple calls efficiently")
        void shouldHandleMultipleCallsEfficiently() {
            // Given
            UUID inventoryId = UUID.randomUUID();
            String productId = "product-multi";
            Inventory inventory = Inventory.create(productId, 100);
            
            when(inventoryRepository.findById(inventoryId)).thenReturn(Optional.of(inventory));
            when(inventoryRepository.findByProductId(productId)).thenReturn(Optional.of(inventory));
            when(inventoryRepository.findAll()).thenReturn(List.of(inventory));

            // When
            Optional<Inventory> resultById = getInventoryUseCase.findById(inventoryId);
            Optional<Inventory> resultByProductId = getInventoryUseCase.findByProductId(productId);
            List<Inventory> resultAll = getInventoryUseCase.findAll();

            // Then
            assertThat(resultById).isPresent();
            assertThat(resultByProductId).isPresent();
            assertThat(resultAll).hasSize(1);
            
            verify(inventoryRepository).findById(inventoryId);
            verify(inventoryRepository).findByProductId(productId);
            verify(inventoryRepository).findAll();
        }

        @Test
        @DisplayName("Should maintain consistency across different find methods")
        void shouldMaintainConsistencyAcrossDifferentFindMethods() {
            // Given
            String productId = "consistent-product";
            Inventory inventory = Inventory.create(productId, 150);
            
            when(inventoryRepository.findByProductId(productId)).thenReturn(Optional.of(inventory));
            when(inventoryRepository.findAll()).thenReturn(List.of(inventory));

            // When
            Optional<Inventory> byProductId = getInventoryUseCase.findByProductId(productId);
            List<Inventory> all = getInventoryUseCase.findAll();

            // Then
            assertThat(byProductId).isPresent();
            assertThat(all).hasSize(1);
            assertThat(byProductId.get().getProductId()).isEqualTo(all.get(0).getProductId());
            assertThat(byProductId.get().getQuantity()).isEqualTo(all.get(0).getQuantity());
        }
    }
}