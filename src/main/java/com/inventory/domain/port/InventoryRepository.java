package com.inventory.domain.port;

import com.inventory.domain.model.Inventory;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InventoryRepository {
    Inventory save(Inventory inventory);
    Optional<Inventory> findById(UUID id);
    Optional<Inventory> findByProductId(String productId);
    List<Inventory> findAll();
    List<Inventory> findByQuantityLessThan(Integer threshold);
    boolean deleteById(UUID id);
    boolean deleteByProductId(String productId);
    long count();
}