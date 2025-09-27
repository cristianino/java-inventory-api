package com.inventory.domain.port;

import com.inventory.domain.model.Product;
import java.util.Optional;

public interface ProductService {
    Optional<Product> findById(String productId);
    boolean existsById(String productId);
}