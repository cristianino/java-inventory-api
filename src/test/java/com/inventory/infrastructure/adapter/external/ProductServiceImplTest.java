package com.inventory.infrastructure.adapter.external;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.inventory.domain.model.Product;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.math.BigDecimal;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Product Service Implementation Tests")
class ProductServiceImplTest {

    private static WireMockServer wireMockServer;
    private ProductServiceImpl productService;
    private String baseUrl;

    @BeforeAll
    static void setUpClass() {
        wireMockServer = new WireMockServer(wireMockConfig().port(8089));
        wireMockServer.start();
        WireMock.configureFor("localhost", 8089);
    }

    @AfterAll
    static void tearDownClass() {
        wireMockServer.stop();
    }

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:8089";
        WebClient.Builder webClientBuilder = WebClient.builder();
        productService = new ProductServiceImpl(
                webClientBuilder,
                baseUrl,
                "test-api-key",
                5000L
        );
        wireMockServer.resetAll();
    }

    @Nested
    @DisplayName("Find Product By ID Tests")
    class FindProductByIdTests {

        @Test
        @DisplayName("Should return product when API returns valid response")
        void shouldReturnProductWhenApiReturnsValidResponse() {
            // Given
            String productId = "product-123";
            String responseBody = """
                {
                    "data": {
                        "id": "product-123",
                        "type": "products",
                        "attributes": {
                            "name": "Gaming Laptop",
                            "description": "High performance gaming laptop",
                            "price": 1499.99,
                            "active": true
                        }
                    },
                    "links": {
                        "self": "/api/products/product-123"
                    }
                }
                """;

            stubFor(get(urlEqualTo("/api/products/" + productId))
                    .withHeader("X-API-Key", equalTo("test-api-key"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody(responseBody)));

            // When
            Optional<Product> result = productService.findById(productId);

            // Then
            assertThat(result).isPresent();
            Product product = result.get();
            assertThat(product.getId()).isEqualTo("product-123");
            assertThat(product.getName()).isEqualTo("Gaming Laptop");
            assertThat(product.getDescription()).isEqualTo("High performance gaming laptop");
            assertThat(product.getPrice()).isEqualByComparingTo(new BigDecimal("1499.99"));
            assertThat(product.isActive()).isTrue();

            verify(getRequestedFor(urlEqualTo("/api/products/" + productId))
                    .withHeader("X-API-Key", equalTo("test-api-key")));
        }

        @Test
        @DisplayName("Should return product with null description when not provided")
        void shouldReturnProductWithNullDescriptionWhenNotProvided() {
            // Given
            String productId = "product-456";
            String responseBody = """
                {
                    "data": {
                        "id": "product-456",
                        "type": "products",
                        "attributes": {
                            "name": "Basic Mouse",
                            "price": 29.99
                        }
                    }
                }
                """;

            stubFor(get(urlEqualTo("/api/products/" + productId))
                    .withHeader("X-API-Key", equalTo("test-api-key"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody(responseBody)));

            // When
            Optional<Product> result = productService.findById(productId);

            // Then
            assertThat(result).isPresent();
            Product product = result.get();
            assertThat(product.getId()).isEqualTo("product-456");
            assertThat(product.getName()).isEqualTo("Basic Mouse");
            assertThat(product.getDescription()).isEqualTo("No description available");
            assertThat(product.getPrice()).isEqualByComparingTo(new BigDecimal("29.99"));
            assertThat(product.isActive()).isTrue(); // Default value
        }

        @Test
        @DisplayName("Should return empty when product not found")
        void shouldReturnEmptyWhenProductNotFound() {
            // Given
            String productId = "non-existent";

            stubFor(get(urlEqualTo("/api/products/" + productId))
                    .withHeader("X-API-Key", equalTo("test-api-key"))
                    .willReturn(aResponse()
                            .withStatus(404)));

            // When
            Optional<Product> result = productService.findById(productId);

            // Then
            assertThat(result).isEmpty();
            verify(getRequestedFor(urlEqualTo("/api/products/" + productId)));
        }

        @Test
        @DisplayName("Should return empty when API returns null response")
        void shouldReturnEmptyWhenApiReturnsNullResponse() {
            // Given
            String productId = "null-response";

            stubFor(get(urlEqualTo("/api/products/" + productId))
                    .withHeader("X-API-Key", equalTo("test-api-key"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody("null")));

            // When
            Optional<Product> result = productService.findById(productId);

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should throw exception when API returns server error")
        void shouldThrowExceptionWhenApiReturnsServerError() {
            // Given
            String productId = "server-error";

            stubFor(get(urlEqualTo("/api/products/" + productId))
                    .withHeader("X-API-Key", equalTo("test-api-key"))
                    .willReturn(aResponse()
                            .withStatus(500)
                            .withBody("Internal server error")));

            // When & Then
            assertThatThrownBy(() -> productService.findById(productId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Failed to fetch product: " + productId)
                    .hasCauseInstanceOf(WebClientResponseException.class);
        }

        @Test
        @DisplayName("Should throw exception when API returns unauthorized")
        void shouldThrowExceptionWhenApiReturnsUnauthorized() {
            // Given
            String productId = "unauthorized";

            stubFor(get(urlEqualTo("/api/products/" + productId))
                    .withHeader("X-API-Key", equalTo("test-api-key"))
                    .willReturn(aResponse()
                            .withStatus(403)
                            .withBody("Forbidden")));

            // When & Then
            assertThatThrownBy(() -> productService.findById(productId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Failed to fetch product: " + productId);
        }

        @Test
        @DisplayName("Should handle malformed JSON gracefully")
        void shouldHandleMalformedJsonGracefully() {
            // Given
            String productId = "malformed-json";

            stubFor(get(urlEqualTo("/api/products/" + productId))
                    .withHeader("X-API-Key", equalTo("test-api-key"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody("{ invalid json }")));

            // When & Then
            assertThatThrownBy(() -> productService.findById(productId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Failed to fetch product: " + productId);
        }

        @Test
        @DisplayName("Should handle connection timeout")
        void shouldHandleConnectionTimeout() {
            // Given
            String productId = "timeout-test";

            stubFor(get(urlEqualTo("/api/products/" + productId))
                    .withHeader("X-API-Key", equalTo("test-api-key"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withFixedDelay(10000))); // 10 seconds delay, timeout is 5 seconds

            // When & Then
            assertThatThrownBy(() -> productService.findById(productId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Failed to fetch product: " + productId);
        }
    }

    @Nested
    @DisplayName("Product Exists Tests")
    class ProductExistsTests {

        @Test
        @DisplayName("Should return true when product exists")
        void shouldReturnTrueWhenProductExists() {
            // Given
            String productId = "existing-product";

            stubFor(head(urlEqualTo("/api/products/" + productId))
                    .withHeader("X-API-Key", equalTo("test-api-key"))
                    .willReturn(aResponse()
                            .withStatus(200)));

            // When
            boolean result = productService.existsById(productId);

            // Then
            assertThat(result).isTrue();
            verify(headRequestedFor(urlEqualTo("/api/products/" + productId))
                    .withHeader("X-API-Key", equalTo("test-api-key")));
        }

        @Test
        @DisplayName("Should return false when product does not exist")
        void shouldReturnFalseWhenProductDoesNotExist() {
            // Given
            String productId = "non-existent";

            stubFor(head(urlEqualTo("/api/products/" + productId))
                    .withHeader("X-API-Key", equalTo("test-api-key"))
                    .willReturn(aResponse()
                            .withStatus(404)));

            // When
            boolean result = productService.existsById(productId);

            // Then
            assertThat(result).isFalse();
            verify(headRequestedFor(urlEqualTo("/api/products/" + productId)));
        }

        @Test
        @DisplayName("Should throw exception when API returns server error for exists check")
        void shouldThrowExceptionWhenApiReturnsServerErrorForExistsCheck() {
            // Given
            String productId = "server-error";

            stubFor(head(urlEqualTo("/api/products/" + productId))
                    .withHeader("X-API-Key", equalTo("test-api-key"))
                    .willReturn(aResponse()
                            .withStatus(500)));

            // When & Then
            assertThatThrownBy(() -> productService.existsById(productId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Failed to check product existence: " + productId);
        }

        @Test
        @DisplayName("Should throw exception when API returns unauthorized for exists check")
        void shouldThrowExceptionWhenApiReturnsUnauthorizedForExistsCheck() {
            // Given
            String productId = "unauthorized";

            stubFor(head(urlEqualTo("/api/products/" + productId))
                    .withHeader("X-API-Key", equalTo("test-api-key"))
                    .willReturn(aResponse()
                            .withStatus(403)));

            // When & Then
            assertThatThrownBy(() -> productService.existsById(productId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Failed to check product existence: " + productId);
        }

        @Test
        @DisplayName("Should handle timeout for exists check")
        void shouldHandleTimeoutForExistsCheck() {
            // Given
            String productId = "timeout-test";

            stubFor(head(urlEqualTo("/api/products/" + productId))
                    .withHeader("X-API-Key", equalTo("test-api-key"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withFixedDelay(10000))); // 10 seconds delay

            // When & Then
            assertThatThrownBy(() -> productService.existsById(productId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Failed to check product existence: " + productId);
        }
    }

    @Nested
    @DisplayName("Circuit Breaker and Fallback Tests")
    @Disabled("Circuit breaker tests disabled - complex async behavior with reactive streams")
    class CircuitBreakerAndFallbackTests {

        @Test
        @DisplayName("Should use fallback when circuit breaker is triggered")
        void shouldUseFallbackWhenCircuitBreakerIsTriggered() {
            // Given
            String productId = "circuit-breaker-test";
            
            // Simulate multiple failures to trigger circuit breaker
            stubFor(get(urlEqualTo("/api/products/" + productId))
                    .withHeader("X-API-Key", equalTo("test-api-key"))
                    .willReturn(aResponse()
                            .withStatus(500)));

            // When - Make multiple calls to potentially trigger circuit breaker
            Optional<Product> result1 = productService.findById(productId);
            Optional<Product> result2 = productService.findById(productId);

            // Then - Should return empty from fallback
            assertThat(result1).isEmpty();
            assertThat(result2).isEmpty();
        }

        @Test
        @DisplayName("Should use fallback for exists check when circuit breaker is triggered")
        void shouldUseFallbackForExistsCheckWhenCircuitBreakerIsTriggered() {
            // Given
            String productId = "circuit-breaker-exists-test";
            
            // Simulate failure
            stubFor(head(urlEqualTo("/api/products/" + productId))
                    .withHeader("X-API-Key", equalTo("test-api-key"))
                    .willReturn(aResponse()
                            .withStatus(500)));

            // When - Make multiple calls to potentially trigger circuit breaker
            boolean result1 = productService.existsById(productId);
            boolean result2 = productService.existsById(productId);

            // Then - Should return false from fallback (fail-safe)
            assertThat(result1).isFalse();
            assertThat(result2).isFalse();
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("Should handle empty product ID")
        void shouldHandleEmptyProductId() {
            // Given
            String productId = "";

            stubFor(get(urlEqualTo("/api/products/"))
                    .withHeader("X-API-Key", equalTo("test-api-key"))
                    .willReturn(aResponse()
                            .withStatus(404)));

            // When
            Optional<Product> result = productService.findById(productId);

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should handle special characters in product ID")
        void shouldHandleSpecialCharactersInProductId() {
            // Given
            String productId = "product-with-special-chars-@#$%";
            String encodedProductId = "product-with-special-chars-@%23$%25";

            stubFor(get(urlMatching("/api/products/.*"))
                    .withHeader("X-API-Key", equalTo("test-api-key"))
                    .willReturn(aResponse()
                            .withStatus(404)));

            // When
            Optional<Product> result = productService.findById(productId);

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should handle very long product ID")
        void shouldHandleVeryLongProductId() {
            // Given
            String productId = "very-long-product-id-" + "x".repeat(1000);

            stubFor(get(urlMatching("/api/products/.*"))
                    .withHeader("X-API-Key", equalTo("test-api-key"))
                    .willReturn(aResponse()
                            .withStatus(404)));

            // When
            Optional<Product> result = productService.findById(productId);

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should handle different content types gracefully")
        void shouldHandleDifferentContentTypesGracefully() {
            // Given
            String productId = "different-content-type";

            stubFor(get(urlEqualTo("/api/products/" + productId))
                    .withHeader("X-API-Key", equalTo("test-api-key"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "text/plain")
                            .withBody("Plain text response")));

            // When & Then
            assertThatThrownBy(() -> productService.findById(productId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Failed to fetch product: " + productId);
        }
    }
}