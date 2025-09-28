package com.inventory.infrastructure.adapter.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.reactive.function.client.WebClient;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SystemControllerDefault.class)
@DisplayName("SystemControllerDefault Tests")
public class SystemControllerDefaultTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WebClient.Builder webClientBuilder;

    @Nested
    @DisplayName("System Info Tests")
    class SystemInfoTests {

        @Test
        @DisplayName("Should return system information")
        void shouldReturnSystemInfo() throws Exception {
            mockMvc.perform(get("/api/system/info"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$.service").value("java-inventory-api"))
                    .andExpect(jsonPath("$.version").value("0.0.1-SNAPSHOT"))
                    .andExpect(jsonPath("$.timestamp").exists())
                    .andExpect(jsonPath("$.productApiUrl").exists())
                    .andExpect(jsonPath("$.environment").exists());
        }

        @Test
        @DisplayName("Should include environment information")
        void shouldIncludeEnvironmentInfo() throws Exception {
            mockMvc.perform(get("/api/system/info"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.environment").value("default"));
        }

        @Test
        @DisplayName("Should have correct JSON structure for info endpoint")
        void shouldHaveCorrectJsonStructureForInfo() throws Exception {
            mockMvc.perform(get("/api/system/info"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.service").isString())
                    .andExpect(jsonPath("$.version").isString())
                    .andExpect(jsonPath("$.timestamp").isString())
                    .andExpect(jsonPath("$.productApiUrl").isString())
                    .andExpect(jsonPath("$.environment").isString());
        }
    }

    @Nested
    @DisplayName("Connectivity Test Tests")
    class ConnectivityTestTests {

        @Test
        @DisplayName("Should return 503 when connectivity test fails - health check error")
        void shouldReturn503WhenHealthCheckFails() throws Exception {
            mockMvc.perform(get("/api/system/connectivity-test"))
                    .andExpect(status().isServiceUnavailable())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$.timestamp").exists())
                    .andExpect(jsonPath("$.productApiUrl").exists())
                    .andExpect(jsonPath("$.healthCheck.status").value("FAILED"))
                    .andExpect(jsonPath("$.healthCheck.error").exists())
                    .andExpect(jsonPath("$.overallStatus").value("DISCONNECTED"));
        }

        @Test
        @DisplayName("Should include timestamp in connectivity test response")
        void shouldIncludeTimestampInConnectivityTest() throws Exception {
            mockMvc.perform(get("/api/system/connectivity-test"))
                    .andExpect(status().isServiceUnavailable())
                    .andExpect(jsonPath("$.timestamp").isString());
        }

        @Test
        @DisplayName("Should include productApiUrl in connectivity test response")
        void shouldIncludeProductApiUrlInConnectivityTest() throws Exception {
            mockMvc.perform(get("/api/system/connectivity-test"))
                    .andExpect(status().isServiceUnavailable())
                    .andExpect(jsonPath("$.productApiUrl").isString());
        }

        @Test
        @DisplayName("Should have correct error structure when WebClient fails")
        void shouldHaveCorrectErrorStructureWhenWebClientFails() throws Exception {
            mockMvc.perform(get("/api/system/connectivity-test"))
                    .andExpect(status().isServiceUnavailable())
                    .andExpect(jsonPath("$.healthCheck").exists())
                    .andExpect(jsonPath("$.healthCheck.status").value("FAILED"))
                    .andExpect(jsonPath("$.healthCheck.error").isString())
                    .andExpect(jsonPath("$.overallStatus").value("DISCONNECTED"));
        }

        @Test
        @DisplayName("Should handle connectivity test gracefully")
        void shouldHandleConnectivityTestGracefully() throws Exception {
            mockMvc.perform(get("/api/system/connectivity-test"))
                    .andExpect(status().isServiceUnavailable())
                    .andExpect(content().contentType("application/json"));
        }
    }
}
