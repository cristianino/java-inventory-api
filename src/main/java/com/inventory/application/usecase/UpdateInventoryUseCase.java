package com.inventory.application.usecase;

import com.inventory.domain.model.Inventory;
import com.inventory.domain.port.InventoryRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UpdateInventoryUseCase {
    private final InventoryRepository inventoryRepository;

    public UpdateInventoryUseCase(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public Inventory updateQuantity(UUID id, Integer newQuantity) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Inventory not found: " + id));
        
        inventory.updateQuantity(newQuantity);
        return inventoryRepository.save(inventory);
    }

    public Inventory updateQuantityByProductId(String productId, Integer newQuantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new IllegalArgumentException("Inventory not found for product: " + productId));
        
        inventory.updateQuantity(newQuantity);
        return inventoryRepository.save(inventory);
    }

    public Inventory incrementQuantity(UUID id, Integer amount) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Inventory not found: " + id));
        
        inventory.incrementQuantity(amount);
        return inventoryRepository.save(inventory);
    }

    public Inventory decrementQuantity(UUID id, Integer amount) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Inventory not found: " + id));
        
        inventory.decrementQuantity(amount);
        return inventoryRepository.save(inventory);
    }
}