package com.inventory.application.usecase;

import com.inventory.domain.model.Inventory;
import com.inventory.domain.port.InventoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class GetInventoryUseCase {
    private final InventoryRepository inventoryRepository;

    public GetInventoryUseCase(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public Optional<Inventory> findById(UUID id) {
        return inventoryRepository.findById(id);
    }

    public Optional<Inventory> findByProductId(String productId) {
        return inventoryRepository.findByProductId(productId);
    }

    public List<Inventory> findAll() {
        return inventoryRepository.findAll();
    }

    public List<Inventory> findLowStock(Integer threshold) {
        return inventoryRepository.findByQuantityLessThan(threshold);
    }
}