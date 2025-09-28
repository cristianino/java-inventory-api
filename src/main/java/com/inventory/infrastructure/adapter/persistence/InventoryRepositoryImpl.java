package com.inventory.infrastructure.adapter.persistence;

import com.inventory.domain.model.Inventory;
import com.inventory.domain.port.InventoryRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@Transactional
public class InventoryRepositoryImpl implements InventoryRepository {
    private final InventoryJpaRepository jpaRepository;

    public InventoryRepositoryImpl(InventoryJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Inventory save(Inventory inventory) {
        InventoryEntity entity = toEntity(inventory);
        InventoryEntity savedEntity = jpaRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Inventory> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Inventory> findByProductId(String productId) {
        return jpaRepository.findByProductId(productId).map(this::toDomain);
    }

    @Override
    public List<Inventory> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Inventory> findByQuantityLessThan(Integer threshold) {
        return jpaRepository.findByQuantityLessThan(threshold).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteById(UUID id) {
        if (jpaRepository.existsById(id)) {
            jpaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteByProductId(String productId) {
        if (jpaRepository.existsByProductId(productId)) {
            jpaRepository.deleteByProductId(productId);
            return true;
        }
        return false;
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }

    private InventoryEntity toEntity(Inventory inventory) {
        return new InventoryEntity(
                inventory.getId(),
                inventory.getProductId(),
                inventory.getQuantity(),
                inventory.getCreatedAt(),
                inventory.getUpdatedAt()
        );
    }

    private Inventory toDomain(InventoryEntity entity) {
        return new Inventory(
                entity.getId(),
                entity.getProductId(),
                entity.getQuantity(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}