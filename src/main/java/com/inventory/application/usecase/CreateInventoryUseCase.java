package com.inventory.application.usecase;

import com.inventory.domain.model.Inventory;
import com.inventory.domain.port.InventoryRepository;
import com.inventory.domain.port.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CreateInventoryUseCase {
    private static final Logger logger = LoggerFactory.getLogger(CreateInventoryUseCase.class);
    
    private final InventoryRepository inventoryRepository;
    private final ProductService productService;

    public CreateInventoryUseCase(InventoryRepository inventoryRepository, ProductService productService) {
        this.inventoryRepository = inventoryRepository;
        this.productService = productService;
    }

    public Inventory execute(String productId, Integer quantity) {
        logger.info("Starting inventory creation - productId: {}, quantity: {}", productId, quantity);
        
        // Validate product exists
        logger.debug("Validating product existence: {}", productId);
        if (!productService.existsById(productId)) {
            logger.warn("Product validation failed - product does not exist: {}", productId);
            throw new IllegalArgumentException("Product does not exist: " + productId);
        }

        // Check if inventory already exists for this product
        logger.debug("Checking for existing inventory: {}", productId);
        if (inventoryRepository.findByProductId(productId).isPresent()) {
            logger.warn("Inventory creation failed - inventory already exists for product: {}", productId);
            throw new IllegalArgumentException("Inventory already exists for product: " + productId);
        }

        // Create and save inventory
        logger.debug("Creating new inventory for product: {}", productId);
        Inventory inventory = Inventory.create(productId, quantity);
        Inventory savedInventory = inventoryRepository.save(inventory);
        
        logger.info("Successfully created inventory - id: {}, productId: {}, quantity: {}", 
                   savedInventory.getId(), productId, quantity);
        
        return savedInventory;
    }
}