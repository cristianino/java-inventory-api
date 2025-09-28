package com.inventory.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("OpenAPI Configuration Tests")
class OpenApiConfigurationTest {

    @Test
    @DisplayName("Should create OpenAPI bean")
    void shouldCreateOpenApiBean() {
        // Given
        OpenApiConfiguration config = new OpenApiConfiguration();

        // When
        OpenAPI openAPI = config.inventoryOpenAPI();

        // Then
        assertThat(openAPI).isNotNull();
        assertThat(openAPI).isInstanceOf(OpenAPI.class);
    }

    @Test
    @DisplayName("Should configure OpenAPI with correct info")
    void shouldConfigureOpenApiWithCorrectInfo() {
        // Given
        OpenApiConfiguration config = new OpenApiConfiguration();

        // When
        OpenAPI openAPI = config.inventoryOpenAPI();

        // Then
        assertThat(openAPI).isNotNull();
        
        Info info = openAPI.getInfo();
        assertThat(info).isNotNull();
        assertThat(info.getTitle()).isEqualTo("Java Inventory API");
        assertThat(info.getDescription()).contains("Spring Boot 3 microservice for inventory management using Hexagonal Architecture");
        assertThat(info.getDescription()).contains("API Versioning Strategy");
        assertThat(info.getVersion()).isEqualTo("v1.0.0");
    }

    @Test
    @DisplayName("Should configure OpenAPI with correct contact information")
    void shouldConfigureOpenApiWithCorrectContactInformation() {
        // Given
        OpenApiConfiguration config = new OpenApiConfiguration();

        // When
        OpenAPI openAPI = config.inventoryOpenAPI();

        // Then
        Contact contact = openAPI.getInfo().getContact();
        assertThat(contact).isNotNull();
        assertThat(contact.getName()).isEqualTo("Inventory Team");
        assertThat(contact.getEmail()).isEqualTo("inventory@example.com");
    }

    @Test
    @DisplayName("Should configure OpenAPI with correct license information")
    void shouldConfigureOpenApiWithCorrectLicenseInformation() {
        // Given
        OpenApiConfiguration config = new OpenApiConfiguration();

        // When
        OpenAPI openAPI = config.inventoryOpenAPI();

        // Then
        License license = openAPI.getInfo().getLicense();
        assertThat(license).isNotNull();
        assertThat(license.getName()).isEqualTo("MIT License");
        assertThat(license.getUrl()).isEqualTo("https://opensource.org/licenses/MIT");
    }

    @Test
    @DisplayName("Should create new OpenAPI instance on each call")
    void shouldCreateNewOpenApiInstanceOnEachCall() {
        // Given
        OpenApiConfiguration config = new OpenApiConfiguration();

        // When
        OpenAPI openAPI1 = config.inventoryOpenAPI();
        OpenAPI openAPI2 = config.inventoryOpenAPI();

        // Then
        assertThat(openAPI1).isNotNull();
        assertThat(openAPI2).isNotNull();
        assertThat(openAPI1).isNotSameAs(openAPI2);
    }

    @Test
    @DisplayName("Should have consistent configuration across instances")
    void shouldHaveConsistentConfigurationAcrossInstances() {
        // Given
        OpenApiConfiguration config = new OpenApiConfiguration();

        // When
        OpenAPI openAPI1 = config.inventoryOpenAPI();
        OpenAPI openAPI2 = config.inventoryOpenAPI();

        // Then
        assertThat(openAPI1.getInfo().getTitle()).isEqualTo(openAPI2.getInfo().getTitle());
        assertThat(openAPI1.getInfo().getVersion()).isEqualTo(openAPI2.getInfo().getVersion());
        assertThat(openAPI1.getInfo().getContact().getName()).isEqualTo(openAPI2.getInfo().getContact().getName());
        assertThat(openAPI1.getInfo().getLicense().getName()).isEqualTo(openAPI2.getInfo().getLicense().getName());
    }
}