package com.inventory.infrastructure.adapter.persistence;

import com.inventory.domain.model.Inventory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("InventoryRepositoryImpl Tests")
class InventoryRepositoryImplTest {

    @Mock
    private InventoryJpaRepository jpaRepository;

    private InventoryRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new InventoryRepositoryImpl(jpaRepository);
    }

    @Test
    @DisplayName("Should save inventory successfully")
    void shouldSaveInventorySuccessfully() {
        // Given
        Inventory inventory = Inventory.create("product-123", 100);
        InventoryEntity entity = createEntity("product-123", 100);
        
        when(jpaRepository.save(any(InventoryEntity.class))).thenReturn(entity);

        // When
        Inventory result = repository.save(inventory);

        // Then
        assertNotNull(result);
        assertEquals("product-123", result.getProductId());
        assertEquals(100, result.getQuantity());
        verify(jpaRepository).save(any(InventoryEntity.class));
    }

    @Test
    @DisplayName("Should find all inventories successfully")
    void shouldFindAllInventoriesSuccessfully() {
        // Given
        List<InventoryEntity> entities = Arrays.asList(
            createEntity("product-1", 50),
            createEntity("product-2", 75)
        );
        when(jpaRepository.findAll()).thenReturn(entities);

        // When
        List<Inventory> result = repository.findAll();

        // Then
        assertEquals(2, result.size());
        assertEquals("product-1", result.get(0).getProductId());
        assertEquals("product-2", result.get(1).getProductId());
        verify(jpaRepository).findAll();
    }

    @Test
    @DisplayName("Should find by product ID successfully")
    void shouldFindByProductIdSuccessfully() {
        // Given
        String productId = "product-123";
        InventoryEntity entity = createEntity(productId, 50);
        when(jpaRepository.findByProductId(productId)).thenReturn(Optional.of(entity));

        // When
        Optional<Inventory> result = repository.findByProductId(productId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(productId, result.get().getProductId());
        assertEquals(50, result.get().getQuantity());
        verify(jpaRepository).findByProductId(productId);
    }

    @Test
    @DisplayName("Should delete by ID successfully")
    void shouldDeleteByIdSuccessfully() {
        // Given
        UUID id = UUID.randomUUID();
        when(jpaRepository.existsById(id)).thenReturn(true);

        // When
        boolean result = repository.deleteById(id);

        // Then
        assertTrue(result);
        verify(jpaRepository).existsById(id);
        verify(jpaRepository).deleteById(id);
    }

    private InventoryEntity createEntity(String productId, int quantity) {
        LocalDateTime now = LocalDateTime.now();
        return new InventoryEntity(UUID.randomUUID(), productId, quantity, now, now);
    }
}