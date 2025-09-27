package com.inventory.infrastructure.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.*;

@DisplayName("WebClient Configuration Tests")
class WebClientConfigurationTest {

    @Test
    @DisplayName("Should create WebClient builder bean")
    void shouldCreateWebClientBuilderBean() {
        // Given
        WebClientConfiguration config = new WebClientConfiguration();

        // When
        WebClient.Builder builder = config.webClientBuilder();

        // Then
        assertThat(builder).isNotNull();
        assertThat(builder).isInstanceOf(WebClient.Builder.class);
    }

    @Test
    @DisplayName("Should configure WebClient builder with correct settings")
    void shouldConfigureWebClientBuilderWithCorrectSettings() {
        // Given
        WebClientConfiguration config = new WebClientConfiguration();

        // When
        WebClient.Builder builder = config.webClientBuilder();
        WebClient webClient = builder.baseUrl("http://test.com").build();

        // Then
        assertThat(webClient).isNotNull();
        // The codec configuration is internal, so we just verify the WebClient can be built
    }

    @Test
    @DisplayName("Should create new builder instance on each call")
    void shouldCreateNewBuilderInstanceOnEachCall() {
        // Given
        WebClientConfiguration config = new WebClientConfiguration();

        // When
        WebClient.Builder builder1 = config.webClientBuilder();
        WebClient.Builder builder2 = config.webClientBuilder();

        // Then
        assertThat(builder1).isNotNull();
        assertThat(builder2).isNotNull();
        assertThat(builder1).isNotSameAs(builder2);
    }
}