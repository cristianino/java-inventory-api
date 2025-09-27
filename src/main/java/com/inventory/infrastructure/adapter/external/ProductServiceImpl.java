package com.inventory.infrastructure.adapter.external;

import com.inventory.domain.model.Product;
import com.inventory.domain.port.ProductService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);
    
    private final WebClient webClient;
    private final String apiKey;
    private final Duration timeout;

    public ProductServiceImpl(WebClient.Builder webClientBuilder,
                             @Value("${external.products-service.base-url}") String baseUrl,
                             @Value("${external.products-service.api-key}") String apiKey,
                             @Value("${external.products-service.timeout}") long timeoutMs) {
        this.webClient = webClientBuilder
                .baseUrl(baseUrl)
                .build();
        this.apiKey = apiKey;
        this.timeout = Duration.ofMillis(timeoutMs);
    }

    @Override
    @CircuitBreaker(name = "products-service", fallbackMethod = "findByIdFallback")
    @Retry(name = "products-service")
    public Optional<Product> findById(String productId) {
        try {
            logger.info("Fetching product with ID: {}", productId);
            
            ProductResponse response = webClient.get()
                    .uri("/api/products/{id}", productId)
                    .header("X-API-Key", apiKey)
                    .retrieve()
                    .bodyToMono(ProductResponse.class)
                    .timeout(timeout)
                    .block();

            if (response != null) {
                Product product = new Product(
                        response.getId(),
                        response.getName(),
                        response.getDescription(),
                        response.getPrice(),
                        response.isActive()
                );
                logger.info("Successfully fetched product: {}", productId);
                return Optional.of(product);
            }
            
            return Optional.empty();
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                logger.warn("Product not found: {}", productId);
                return Optional.empty();
            }
            logger.error("Error fetching product {}: {}", productId, e.getMessage());
            throw new RuntimeException("Failed to fetch product: " + productId, e);
        } catch (Exception e) {
            logger.error("Unexpected error fetching product {}: {}", productId, e.getMessage());
            throw new RuntimeException("Failed to fetch product: " + productId, e);
        }
    }

    @Override
    @CircuitBreaker(name = "products-service", fallbackMethod = "existsByIdFallback")
    @Retry(name = "products-service")
    public boolean existsById(String productId) {
        try {
            logger.info("Checking if product exists: {}", productId);
            
            webClient.head()
                    .uri("/api/products/{id}", productId)
                    .header("X-API-Key", apiKey)
                    .retrieve()
                    .toBodilessEntity()
                    .timeout(timeout)
                    .block();
            
            logger.info("Product exists: {}", productId);
            return true;
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                logger.warn("Product does not exist: {}", productId);
                return false;
            }
            logger.error("Error checking product existence {}: {}", productId, e.getMessage());
            throw new RuntimeException("Failed to check product existence: " + productId, e);
        } catch (Exception e) {
            logger.error("Unexpected error checking product existence {}: {}", productId, e.getMessage());
            throw new RuntimeException("Failed to check product existence: " + productId, e);
        }
    }

    // Fallback methods
    public Optional<Product> findByIdFallback(String productId, Exception ex) {
        logger.error("Fallback triggered for findById({}): {}", productId, ex.getMessage());
        return Optional.empty();
    }

    public boolean existsByIdFallback(String productId, Exception ex) {
        logger.error("Fallback triggered for existsById({}): {}", productId, ex.getMessage());
        return false; // Fail safe - assume product doesn't exist
    }

    // Inner class for product response
    public static class ProductResponse {
        private String id;
        private String name;
        private String description;
        private BigDecimal price;
        private boolean active;

        // Constructors
        public ProductResponse() {}

        // Getters and Setters
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }
    }
}