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
            
            ProductApiResponse response = webClient.get()
                    .uri("/api/products/{id}", productId)
                    .header("X-API-Key", apiKey)
                    .retrieve()
                    .bodyToMono(ProductApiResponse.class)
                    .timeout(timeout)
                    .block();

            if (response != null && response.getData() != null) {
                ProductData productData = response.getData();
                ProductAttributes attributes = productData.getAttributes();
                
                Product product = new Product(
                        productData.getId(),
                        attributes.getName(),
                        attributes.getDescription() != null ? attributes.getDescription() : "No description available",
                        attributes.getPrice(),
                        attributes.isActive() != null ? attributes.isActive() : true
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

    // Inner classes for JSON:API response format
    public static class ProductApiResponse {
        private ProductData data;
        private ProductLinks links;

        public ProductApiResponse() {}

        public ProductData getData() {
            return data;
        }

        public void setData(ProductData data) {
            this.data = data;
        }

        public ProductLinks getLinks() {
            return links;
        }

        public void setLinks(ProductLinks links) {
            this.links = links;
        }
    }

    public static class ProductData {
        private String id;
        private String type;
        private ProductAttributes attributes;

        public ProductData() {}

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public ProductAttributes getAttributes() {
            return attributes;
        }

        public void setAttributes(ProductAttributes attributes) {
            this.attributes = attributes;
        }
    }

    public static class ProductAttributes {
        private String name;
        private String description;
        private BigDecimal price;
        private Boolean active;

        public ProductAttributes() {}

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

        public Boolean isActive() {
            return active;
        }

        public void setActive(Boolean active) {
            this.active = active;
        }
    }

    public static class ProductLinks {
        private String self;

        public ProductLinks() {}

        public String getSelf() {
            return self;
        }

        public void setSelf(String self) {
            this.self = self;
        }
    }
}