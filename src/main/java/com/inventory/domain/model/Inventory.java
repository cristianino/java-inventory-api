package com.inventory.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Inventory {
    private final UUID id;
    private final String productId;
    private Integer quantity;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Inventory(UUID id, String productId, Integer quantity, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = Objects.requireNonNull(id, "ID cannot be null");
        this.productId = Objects.requireNonNull(productId, "Product ID cannot be null");
        this.quantity = Objects.requireNonNull(quantity, "Quantity cannot be null");
        this.createdAt = Objects.requireNonNull(createdAt, "Created at cannot be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "Updated at cannot be null");
        
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
    }

    public static Inventory create(String productId, Integer quantity) {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        return new Inventory(id, productId, quantity, now, now);
    }

    public void updateQuantity(Integer newQuantity) {
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        this.quantity = newQuantity;
        this.updatedAt = LocalDateTime.now();
    }

    public void incrementQuantity(Integer amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Increment amount cannot be negative");
        }
        this.quantity += amount;
        this.updatedAt = LocalDateTime.now();
    }

    public void decrementQuantity(Integer amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Decrement amount cannot be negative");
        }
        if (this.quantity < amount) {
            throw new IllegalArgumentException("Insufficient inventory quantity");
        }
        this.quantity -= amount;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isOutOfStock() {
        return quantity == 0;
    }

    public boolean hasStock(Integer requiredQuantity) {
        return quantity >= requiredQuantity;
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public String getProductId() {
        return productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Inventory inventory = (Inventory) o;
        return Objects.equals(id, inventory.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Inventory{" +
                "id=" + id +
                ", productId='" + productId + '\'' +
                ", quantity=" + quantity +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}