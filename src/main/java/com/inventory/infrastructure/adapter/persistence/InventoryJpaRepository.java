package com.inventory.infrastructure.adapter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InventoryJpaRepository extends JpaRepository<InventoryEntity, UUID> {
    Optional<InventoryEntity> findByProductId(String productId);
    List<InventoryEntity> findByQuantityLessThan(Integer threshold);
    boolean existsByProductId(String productId);
    void deleteByProductId(String productId);
}