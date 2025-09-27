package com.inventory.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

public class Product {
    private final String id;
    private final String name;
    private final String description;
    private final BigDecimal price;
    private final boolean active;

    public Product(String id, String name, String description, BigDecimal price, boolean active) {
        this.id = Objects.requireNonNull(id, "Product ID cannot be null");
        this.name = Objects.requireNonNull(name, "Product name cannot be null");
        this.description = description;
        this.price = Objects.requireNonNull(price, "Product price cannot be null");
        this.active = active;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", active=" + active +
                '}';
    }
}