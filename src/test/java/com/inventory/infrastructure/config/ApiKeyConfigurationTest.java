package com.inventory.infrastructure.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ApiKeyConfiguration.class)
@TestPropertySource(properties = {
    "app.api.key=test-api-key-12345"
})
@DisplayName("ApiKeyConfiguration Tests")
class ApiKeyConfigurationTest {

    @Autowired
    private ApiKeyConfiguration apiKeyConfiguration;

    @Nested
    @DisplayName("API Key Configuration Tests")
    class ApiKeyConfigurationTests {

        @Test
        @DisplayName("Should load API key from application properties")
        void shouldLoadApiKeyFromProperties() {
            // Act
            String apiKey = apiKeyConfiguration.getApiKey();
            
            // Assert
            assertThat(apiKey).isEqualTo("test-api-key-12345");
        }

        @Test
        @DisplayName("Should return non-null API key")
        void shouldReturnNonNullApiKey() {
            // Act
            String apiKey = apiKeyConfiguration.getApiKey();
            
            // Assert
            assertThat(apiKey).isNotNull();
        }

        @Test
        @DisplayName("Should return non-empty API key")
        void shouldReturnNonEmptyApiKey() {
            // Act
            String apiKey = apiKeyConfiguration.getApiKey();
            
            // Assert
            assertThat(apiKey).isNotBlank();
        }
    }

    @Nested
    @DisplayName("Configuration Bean Tests") 
    class ConfigurationBeanTests {

        @Test
        @DisplayName("Should be properly instantiated as Spring bean")
        void shouldBeProperlyInstantiated() {
            // Assert
            assertThat(apiKeyConfiguration).isNotNull();
        }

        @Test
        @DisplayName("Should have Configuration annotation working")
        void shouldHaveConfigurationAnnotationWorking() {
            // This test verifies the @Configuration annotation is working
            // by checking that the bean is properly injected
            assertThat(apiKeyConfiguration).isNotNull();
            assertThat(apiKeyConfiguration.getApiKey()).isNotNull();
        }
    }
}
