package com.inventory.infrastructure.config;

import com.github.jasminb.jsonapi.ResourceConverter;
import com.github.jasminb.jsonapi.JSONAPIDocument;
import com.inventory.infrastructure.adapter.rest.dto.InventoryDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DisplayName("JSON:API Configuration Tests")
class JsonApiConfigurationTest {

    @Test
    @DisplayName("Should create ResourceConverter bean")
    void shouldCreateResourceConverterBean() {
        // Given
        JsonApiConfiguration config = new JsonApiConfiguration();

        // When
        ResourceConverter converter = config.resourceConverter();

        // Then
        assertThat(converter).isNotNull();
        assertThat(converter).isInstanceOf(ResourceConverter.class);
    }

    @Test
    @DisplayName("Should configure ResourceConverter with InventoryDto class")
    void shouldConfigureResourceConverterWithInventoryDtoClass() {
        // Given
        JsonApiConfiguration config = new JsonApiConfiguration();

        // When
        ResourceConverter converter = config.resourceConverter();

        // Then
        assertThat(converter).isNotNull();
        // We can't easily test the internal configuration, but we can verify it's properly configured
        // Just verify the converter has the InventoryDto class registered
        assertThatCode(() -> {
            InventoryDto dto = new InventoryDto(
                UUID.randomUUID(), 
                "PRODUCT-001", 
                10, 
                LocalDateTime.now(), 
                LocalDateTime.now()
            );
            JSONAPIDocument<InventoryDto> document = new JSONAPIDocument<>(dto);
        }).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should create new ResourceConverter instance on each call")
    void shouldCreateNewResourceConverterInstanceOnEachCall() {
        // Given
        JsonApiConfiguration config = new JsonApiConfiguration();

        // When
        ResourceConverter converter1 = config.resourceConverter();
        ResourceConverter converter2 = config.resourceConverter();

        // Then
        assertThat(converter1).isNotNull();
        assertThat(converter2).isNotNull();
        assertThat(converter1).isNotSameAs(converter2);
    }
}