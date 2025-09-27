package com.inventory.infrastructure.adapter.external;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;

@Component("productApiConnection")
public class ProductApiHealthIndicator {

    private static final Logger log = LoggerFactory.getLogger(ProductApiHealthIndicator.class);
    
    private final WebClient webClient;
    private final String productApiBaseUrl;

    public ProductApiHealthIndicator(WebClient.Builder webClientBuilder,
                                   @Value("${external.products.service.base-url}") String productApiBaseUrl) {
        this.webClient = webClientBuilder.build();
        this.productApiBaseUrl = productApiBaseUrl;
    }

    public boolean isProductApiHealthy() {
        try {
            String healthResponse = webClient
                    .get()
                    .uri(productApiBaseUrl + "/actuator/health")
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(5))
                    .block();

            boolean isHealthy = healthResponse != null && healthResponse.contains("\"status\":\"UP\"");
            log.info("Product API health check: {} - URL: {}", isHealthy ? "UP" : "DOWN", productApiBaseUrl);
            return isHealthy;
        } catch (WebClientResponseException e) {
            log.warn("Product API health check failed with HTTP {}: {}", e.getStatusCode(), e.getMessage());
            return false;
        } catch (Exception e) {
            log.warn("Product API health check failed: {}", e.getMessage());
            return false;
        }
    }
}