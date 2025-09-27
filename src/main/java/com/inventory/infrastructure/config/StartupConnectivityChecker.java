package com.inventory.infrastructure.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Component
public class StartupConnectivityChecker implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(StartupConnectivityChecker.class);

    private final WebClient webClient;
    private final String productApiBaseUrl;

    public StartupConnectivityChecker(WebClient.Builder webClientBuilder,
                                    @Value("${external.products.service.base-url}") String productApiBaseUrl) {
        this.webClient = webClientBuilder.build();
        this.productApiBaseUrl = productApiBaseUrl;
    }

    @Override
    public void run(String... args) {
        log.info("🚀 Starting Inventory API connectivity checks...");
        log.info("📡 Product API URL configured: {}", productApiBaseUrl);
        
        // Dar tiempo para que los servicios se inicialicen
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        checkProductApiConnectivity();
    }

    private void checkProductApiConnectivity() {
        try {
            log.info("🔍 Testing connection to Product API...");
            
            String healthResponse = webClient
                    .get()
                    .uri(productApiBaseUrl + "/actuator/health")
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(10))
                    .block();

            if (healthResponse != null && healthResponse.contains("\"status\":\"UP\"")) {
                log.info("✅ Successfully connected to Product API!");
                log.info("🔗 Product API Health Status: UP");
            } else {
                log.warn("⚠️ Product API responded but status unclear: {}", healthResponse);
            }
        } catch (Exception e) {
            log.error("❌ Failed to connect to Product API: {}", e.getMessage());
            log.error("💡 Make sure Product API is running on: {}", productApiBaseUrl);
            log.error("💡 Check network connectivity and API key configuration");
        }
    }
}