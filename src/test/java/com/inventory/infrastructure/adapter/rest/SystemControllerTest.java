package com.inventory.infrastructure.adapter.rest;package com.inventory.infrastructure.adapter.rest;



import org.junit.jupiter.api.DisplayName;import org.juni            package com.inventory.infrastructure.adapter.rest;

import org.junit.jupiter.api.Nested;

import org.junit.jupiter.api.Test;import org.junit.jupiter.api.DisplayName;

import org.springframework.beans.factory.annotation.Autowired;import org.junit.jupiter.api.Nested;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;import org.junit.jupiter.api.Test;

import org.springframework.boot.test.mock.mockito.MockBean;import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.MediaType;import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.test.context.TestPropertySource;import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.test.web.servlet.MockMvc;import org.springframework.http.MediaType;

import org.springframework.web.reactive.function.client.WebClient;import org.springframework.test.context.TestPropertySource;

import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;import org.springframework.web.reactive.function.client.WebClient;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(SystemController.class)import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;iter.api.DisplayName;

@TestPropertySource(properties = {import org.junit.jupiter.api.Nested;

    "app.product-service.url=http://localhost:8080",import org.junit.jupiter.api.Test;

    "app.product-service.endpoint=/api/v1/products"import org.springframework.beans.factory.annotation.Autowired;

})import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

class SystemControllerTest {import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;

    @Autowiredimport org.springframework.test.context.TestPropertySource;

    private MockMvc mockMvc;import org.springframework.test.web.servlet.MockMvc;

import org.springframework.web.reactive.function.client.WebClient;

    @MockBean

    private WebClient.Builder webClientBuilder;import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

    @Nested

    @DisplayName("System Information Tests")@WebMvcTest(SystemController.class)

    class SystemInfoTests {@TestPropertySource(properties = {

    "external.products-service.base-url=http://mock-products-api.com",

        @Test    "external.products-service.api-key=test-api-key-123"

        @DisplayName("Should return system information with 200 OK")})

        void shouldReturnSystemInfo() throws Exception {@DisplayName("SystemController Tests")

            mockMvc.perform(get("/api/v1/system/info")class SystemControllerTest {

                    .accept(MediaType.APPLICATION_JSON))

                    .andExpected(status().isOk())    @Autowired

                    .andExpected(content().contentType(MediaType.APPLICATION_JSON))    private MockMvc mockMvc;

                    .andExpected(jsonPath("$.service").value("java-inventory-api"));

        }    @MockBean

    }    private WebClient.Builder webClientBuilder;



    @Nested    @Nested

    @DisplayName("Connectivity Test Tests")    @DisplayName("System Info Endpoint")

    class ConnectivityTestTests {    class SystemInfoTests {



        @Test        @Test

        @DisplayName("Should handle connectivity test endpoint")        @DisplayName("Should return system information successfully")

        void shouldHandleConnectivityTest() throws Exception {        void should_ReturnSystemInfo_Successfully() throws Exception {

            mockMvc.perform(get("/api/v1/system/connectivity-test")            // Act & Assert

                    .accept(MediaType.APPLICATION_JSON))            mockMvc.perform(get("/api/v1/system/info")

                    .andExpected(status().isOk());                    .accept(MediaType.APPLICATION_JSON))

        }                    .andExpect(status().isOk())

    }                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))

}                    .andExpect(jsonPath("$.service").value("java-inventory-api"))
                    .andExpect(jsonPath("$.version").value("0.0.1-SNAPSHOT"))
                    .andExpect(jsonPath("$.timestamp").exists())
                    .andExpect(jsonPath("$.productApiUrl").value("http://mock-products-api.com"))
                    .andExpect(jsonPath("$.environment").exists());
        }

        @Test
        @DisplayName("Should include correct environment from system property")
        void should_IncludeEnvironment_FromSystemProperty() throws Exception {
            // Act & Assert
            mockMvc.perform(get("/api/v1/system/info")
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.environment").value("default"));
        }
    }

    @Nested
    @DisplayName("Connectivity Test Endpoint")
    class ConnectivityTestTests {

        @Test
        @DisplayName("Should return some response for connectivity test (actual behavior depends on WebClient setup)")
        void should_HandleConnectivityTestEndpoint() throws Exception {
            // This test will invoke the real connectivity logic but handle WebClient failures gracefully
            // Since we're mocking WebClient.Builder, actual calls will fail, but the endpoint should return a response
            mockMvc.perform(get("/api/v1/system/connectivity-test")
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(anyOf(status().isOk(), status().isServiceUnavailable()))
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.timestamp").exists())
                    .andExpect(jsonPath("$.productApiUrl").value("http://mock-products-api.com"));
        }
    }
}
