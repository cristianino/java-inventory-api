package com.inventory.infrastructure.adapter.rest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.reactive.function.client.WebClient;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SystemController.class)
@TestPropertySource(properties = {
    "external.products-service.base-url=http://localhost:8080",
    "external.products-service.api-key=test-api-key"
})
class SystemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WebClient.Builder webClientBuilder;

    @Nested
    class SystemInfoTests {

        @Test
        void shouldReturnSystemInformation() throws Exception {
            mockMvc.perform(get("/api/v1/system/info"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.service").value("java-inventory-api"))
                    .andExpect(jsonPath("$.environment").value("default"))
                    .andExpect(jsonPath("$.productApiUrl").value("http://localhost:8080"))
                    .andExpect(jsonPath("$.version").value("0.0.1-SNAPSHOT"))
                    .andExpect(jsonPath("$.timestamp").exists());
        }

        @Test
        void shouldReturnValidSystemInfoStructure() throws Exception {
            mockMvc.perform(get("/api/v1/system/info"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.service").isString())
                    .andExpect(jsonPath("$.environment").isString())
                    .andExpect(jsonPath("$.productApiUrl").isString())
                    .andExpect(jsonPath("$.version").isString())
                    .andExpect(jsonPath("$.timestamp").isString());
        }

        @Test
        void shouldHaveCorrectServiceName() throws Exception {
            mockMvc.perform(get("/api/v1/system/info"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.service").value("java-inventory-api"));
        }
    }

    @Nested
    class ConnectivityTestTests {

        @Test
        void shouldHandleConnectivityTestWithoutWebClient() throws Exception {
            // Sin WebClient mockeado, debería devolver error 503
            mockMvc.perform(get("/api/v1/system/connectivity-test"))
                    .andExpect(status().isServiceUnavailable())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.overallStatus").value("DISCONNECTED"))
                    .andExpect(jsonPath("$.healthCheck.status").value("FAILED"))
                    .andExpect(jsonPath("$.productApiUrl").value("http://localhost:8080"))
                    .andExpect(jsonPath("$.timestamp").exists())
                    .andExpect(jsonPath("$.healthCheck.error").exists());
        }

        @Test
        void shouldIncludeTimestampInConnectivityResponse() throws Exception {
            mockMvc.perform(get("/api/v1/system/connectivity-test"))
                    .andExpect(status().isServiceUnavailable())
                    .andExpect(jsonPath("$.timestamp").exists())
                    .andExpect(jsonPath("$.timestamp").isString());
        }

        @Test
        void shouldIncludeProductApiUrlInResponse() throws Exception {
            mockMvc.perform(get("/api/v1/system/connectivity-test"))
                    .andExpect(status().isServiceUnavailable())
                    .andExpect(jsonPath("$.productApiUrl").value("http://localhost:8080"));
        }

        @Test
        void shouldReturnProperErrorStructure() throws Exception {
            mockMvc.perform(get("/api/v1/system/connectivity-test"))
                    .andExpect(status().isServiceUnavailable())
                    .andExpect(jsonPath("$.healthCheck").exists())
                    .andExpect(jsonPath("$.healthCheck.status").value("FAILED"))
                    .andExpect(jsonPath("$.healthCheck.error").isString());
        }

        @Test
        void shouldHandleMultipleConnectivityRequests() throws Exception {
            // Primera llamada
            mockMvc.perform(get("/api/v1/system/connectivity-test"))
                    .andExpect(status().isServiceUnavailable());
            
            // Segunda llamada - debería comportarse igual
            mockMvc.perform(get("/api/v1/system/connectivity-test"))
                    .andExpect(status().isServiceUnavailable())
                    .andExpect(jsonPath("$.overallStatus").value("DISCONNECTED"));
        }
    }
}
