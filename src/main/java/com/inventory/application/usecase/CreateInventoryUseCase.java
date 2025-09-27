package com.inventory.application.usecase;

import com.inventory.domain.model.Inventory;
import com.inventory.domain.port.InventoryRepository;
import com.inventory.domain.port.ProductService;
import org.springframework.stereotype.Service;

@Service
public class CreateInventoryUseCase {
    private final InventoryRepository inventoryRepository;
    private final ProductService productService;

    public CreateInventoryUseCase(InventoryRepository inventoryRepository, ProductService productService) {
        this.inventoryRepository = inventoryRepository;
        this.productService = productService;
    }

    public Inventory execute(String productId, Integer quantity) {
        // Validate product exists
        if (!productService.existsById(productId)) {
            throw new IllegalArgumentException("Product does not exist: " + productId);
        }

        // Check if inventory already exists for this product
        if (inventoryRepository.findByProductId(productId).isPresent()) {
            throw new IllegalArgumentException("Inventory already exists for product: " + productId);
        }

        // Create and save inventory
        Inventory inventory = Inventory.create(productId, quantity);
        return inventoryRepository.save(inventory);
    }
}