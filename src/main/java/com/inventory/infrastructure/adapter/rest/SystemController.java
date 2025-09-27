package com.inventory.infrastructure.adapter.rest;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/system")
@Tag(name = "System", description = "System utilities and connectivity tests")
@SecurityRequirement(name = "X-API-Key")
public class SystemController {

    private final WebClient webClient;
    private final String productApiBaseUrl;
    private final String productsApiKey;

    public SystemController(WebClient.Builder webClientBuilder,
                           @Value("${external.products-service.base-url}") String productApiBaseUrl,
                           @Value("${external.products-service.api-key}") String productsApiKey) {
        this.webClient = webClientBuilder.build();
        this.productApiBaseUrl = productApiBaseUrl;
        this.productsApiKey = productsApiKey;
    }

    @GetMapping("/connectivity-test")
    public ResponseEntity<Map<String, Object>> testProductApiConnectivity() {
        Map<String, Object> result = new HashMap<>();
        result.put("timestamp", LocalDateTime.now());
        result.put("productApiUrl", productApiBaseUrl);

        try {
            // Test 1: Health endpoint
            String healthResponse = webClient
                    .get()
                    .uri(productApiBaseUrl + "/actuator/health")
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(5))
                    .block();

            result.put("healthCheck", Map.of(
                "status", "SUCCESS",
                "response", healthResponse
            ));

            // Test 2: Products endpoint (con autenticación si es necesaria)
            try {
                String productsResponse = webClient
                        .get()
                        .uri(productApiBaseUrl + "/api/v1/products")
                        .header("X-API-Key", productsApiKey)
                        .retrieve()
                        .bodyToMono(String.class)
                        .timeout(Duration.ofSeconds(10))
                        .block();

                result.put("productsEndpoint", Map.of(
                    "status", "SUCCESS",
                    "responseLength", productsResponse != null ? productsResponse.length() : 0
                ));
            } catch (Exception e) {
                result.put("productsEndpoint", Map.of(
                    "status", "FAILED",
                    "error", e.getMessage()
                ));
            }

            result.put("overallStatus", "CONNECTED");
            return ResponseEntity.ok(result);

        } catch (WebClientResponseException e) {
            result.put("healthCheck", Map.of(
                "status", "FAILED",
                "error", "HTTP " + e.getStatusCode() + ": " + e.getMessage()
            ));
            result.put("overallStatus", "DISCONNECTED");
            return ResponseEntity.status(503).body(result);
        } catch (Exception e) {
            result.put("healthCheck", Map.of(
                "status", "FAILED",
                "error", e.getMessage()
            ));
            result.put("overallStatus", "DISCONNECTED");
            return ResponseEntity.status(503).body(result);
        }
    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getSystemInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("service", "java-inventory-api");
        info.put("version", "0.0.1-SNAPSHOT");
        info.put("timestamp", LocalDateTime.now());
        info.put("productApiUrl", productApiBaseUrl);
        info.put("environment", System.getProperty("spring.profiles.active", "default"));
        
        return ResponseEntity.ok(info);
    }
}