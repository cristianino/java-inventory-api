package com.inventory.application.usecase;

import com.inventory.domain.port.InventoryRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DeleteInventoryUseCase {
    private final InventoryRepository inventoryRepository;

    public DeleteInventoryUseCase(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public boolean deleteById(UUID id) {
        if (!inventoryRepository.findById(id).isPresent()) {
            throw new IllegalArgumentException("Inventory not found: " + id);
        }
        return inventoryRepository.deleteById(id);
    }

    public boolean deleteByProductId(String productId) {
        if (!inventoryRepository.findByProductId(productId).isPresent()) {
            throw new IllegalArgumentException("Inventory not found for product: " + productId);
        }
        return inventoryRepository.deleteByProductId(productId);
    }
}