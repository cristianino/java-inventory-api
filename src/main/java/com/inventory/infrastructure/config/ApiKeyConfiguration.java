package com.inventory.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for API key management.
 * This class provides access to the configured API key for incoming requests authentication.
 */
@Configuration
public class ApiKeyConfiguration {

    @Value("${app.api.key}")
    private String apiKey;

    /**
     * Get the configured API key for incoming requests authentication.
     * 
     * @return the API key
     */
    public String getApiKey() {
        return apiKey;
    }
}