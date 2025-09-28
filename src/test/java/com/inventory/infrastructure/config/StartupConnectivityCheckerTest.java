package com.inventory.infrastructure.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("StartupConnectivityChecker Tests")
class StartupConnectivityCheckerTest {

    @Mock
    private WebClient.Builder webClientBuilder;
    
    @Mock
    private WebClient webClient;
    
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    
    @Mock
    private WebClient.ResponseSpec responseSpec;
    
    private StartupConnectivityChecker startupConnectivityChecker;
    
    private final String productApiBaseUrl = "http://localhost:8080";

    @BeforeEach
    void setUp() {
        when(webClientBuilder.build()).thenReturn(webClient);
        startupConnectivityChecker = new StartupConnectivityChecker(webClientBuilder, productApiBaseUrl);
    }

    @Nested
    @DisplayName("Successful Connectivity Tests")
    class SuccessfulConnectivityTests {
        
        @Test
        @DisplayName("Should successfully connect to Product API with UP status")
        void shouldSuccessfullyConnectWithUpStatus() {
            // Arrange
            String healthResponse = "{\"status\":\"UP\",\"components\":{}}";
            setupSuccessfulWebClientMock(healthResponse);
            
            // Act
            startupConnectivityChecker.run();
            
            // Verify
            verify(webClient).get();
            verify(requestHeadersUriSpec).uri(productApiBaseUrl + "/actuator/health");
            verify(requestHeadersSpec).retrieve();
            verify(responseSpec).bodyToMono(String.class);
        }
        
        @Test
        @DisplayName("Should handle response with ambiguous health status")
        void shouldHandleAmbiguousHealthStatus() {
            // Arrange
            String healthResponse = "{\"status\":\"UNKNOWN\",\"components\":{}}";
            setupSuccessfulWebClientMock(healthResponse);
            
            // Act
            startupConnectivityChecker.run();
            
            // Verify
            verify(webClient).get();
            verify(requestHeadersUriSpec).uri(productApiBaseUrl + "/actuator/health");
        }
        
        private void setupSuccessfulWebClientMock(String response) {
            when(webClient.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(response));
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {
        
        @Test
        @DisplayName("Should handle WebClient timeout exception")
        void shouldHandleTimeoutException() {
            // Arrange
            when(webClient.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.bodyToMono(String.class)).thenReturn(
                Mono.error(new RuntimeException("Timeout"))
            );
            
            // Act
            startupConnectivityChecker.run();
            
            // Verify
            verify(webClient).get();
        }
        
        @Test
        @DisplayName("Should handle WebClientResponseException")
        void shouldHandleWebClientResponseException() {
            // Arrange
            when(webClient.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.bodyToMono(String.class)).thenReturn(
                Mono.error(WebClientResponseException.create(500, "Internal Server Error", null, null, null))
            );
            
            // Act
            startupConnectivityChecker.run();
            
            // Verify
            verify(webClient).get();
        }
        
        @Test
        @DisplayName("Should handle general exceptions")
        void shouldHandleGeneralExceptions() {
            // Arrange
            when(webClient.get()).thenThrow(new RuntimeException("Connection failed"));
            
            // Act
            startupConnectivityChecker.run();
            
            // Verify
            verify(webClient).get();
        }
    }

    @Nested
    @DisplayName("Configuration Tests")
    class ConfigurationTests {
        
        @Test
        @DisplayName("Should properly initialize with WebClient builder and API URL")
        void shouldProperlyInitialize() {
            // Arrange & Act
            StartupConnectivityChecker checker = new StartupConnectivityChecker(webClientBuilder, productApiBaseUrl);
            
            // Verify
            verify(webClientBuilder, atLeastOnce()).build();
            // Constructor completed successfully
        }
        
        @Test
        @DisplayName("Should handle empty args array")
        void shouldHandleEmptyArgs() {
            // Arrange
            when(webClient.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("{\"status\":\"UP\"}"));
            
            // Act
            startupConnectivityChecker.run(new String[]{});
            
            // Verify
            verify(webClient).get();
        }
        
        @Test
        @DisplayName("Should handle args with values")
        void shouldHandleArgsWithValues() {
            // Arrange
            when(webClient.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("{\"status\":\"UP\"}"));
            
            // Act
            startupConnectivityChecker.run("arg1", "arg2");
            
            // Verify
            verify(webClient).get();
        }
    }
}
