package com.inventory.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Product Domain Model Tests")
class ProductTest {

    @Nested
    @DisplayName("Product Creation")
    class ProductCreation {

        @Test
        @DisplayName("Should create product with all valid parameters")
        void shouldCreateProductWithValidParameters() {
            // Given
            String id = "PROD-001";
            String name = "Laptop Dell";
            String description = "High performance laptop";
            BigDecimal price = new BigDecimal("999.99");
            boolean active = true;

            // When
            Product product = new Product(id, name, description, price, active);

            // Then
            assertThat(product.getId()).isEqualTo(id);
            assertThat(product.getName()).isEqualTo(name);
            assertThat(product.getDescription()).isEqualTo(description);
            assertThat(product.getPrice()).isEqualByComparingTo(price);
            assertThat(product.isActive()).isEqualTo(active);
        }

        @Test
        @DisplayName("Should create product with null description")
        void shouldCreateProductWithNullDescription() {
            // Given
            String id = "PROD-002";
            String name = "Mouse";
            String description = null;
            BigDecimal price = new BigDecimal("29.99");
            boolean active = true;

            // When
            Product product = new Product(id, name, description, price, active);

            // Then
            assertThat(product.getDescription()).isNull();
            assertThat(product.getId()).isEqualTo(id);
            assertThat(product.getName()).isEqualTo(name);
        }

        @Test
        @DisplayName("Should create inactive product")
        void shouldCreateInactiveProduct() {
            // Given
            String id = "PROD-003";
            String name = "Discontinued Item";
            String description = "Old product";
            BigDecimal price = new BigDecimal("0.00");
            boolean active = false;

            // When
            Product product = new Product(id, name, description, price, active);

            // Then
            assertThat(product.isActive()).isFalse();
        }

        @Test
        @DisplayName("Should throw exception when id is null")
        void shouldThrowExceptionWhenIdIsNull() {
            // Given
            String id = null;
            String name = "Product";
            String description = "Description";
            BigDecimal price = new BigDecimal("10.00");
            boolean active = true;

            // When & Then
            assertThatThrownBy(() -> new Product(id, name, description, price, active))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("Product ID cannot be null");
        }

        @Test
        @DisplayName("Should throw exception when name is null")
        void shouldThrowExceptionWhenNameIsNull() {
            // Given
            String id = "PROD-001";
            String name = null;
            String description = "Description";
            BigDecimal price = new BigDecimal("10.00");
            boolean active = true;

            // When & Then
            assertThatThrownBy(() -> new Product(id, name, description, price, active))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("Product name cannot be null");
        }

        @Test
        @DisplayName("Should throw exception when price is null")
        void shouldThrowExceptionWhenPriceIsNull() {
            // Given
            String id = "PROD-001";
            String name = "Product";
            String description = "Description";
            BigDecimal price = null;
            boolean active = true;

            // When & Then
            assertThatThrownBy(() -> new Product(id, name, description, price, active))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("Product price cannot be null");
        }
    }

    @Nested
    @DisplayName("Product Equality and Hash")
    class ProductEqualityAndHash {

        @Test
        @DisplayName("Should be equal when products have same id")
        void shouldBeEqualWhenProductsHaveSameId() {
            // Given
            String id = "PROD-001";
            Product product1 = new Product(id, "Name1", "Desc1", new BigDecimal("10.00"), true);
            Product product2 = new Product(id, "Name2", "Desc2", new BigDecimal("20.00"), false);

            // When & Then
            assertThat(product1).isEqualTo(product2);
            assertThat(product1.hashCode()).isEqualTo(product2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when products have different ids")
        void shouldNotBeEqualWhenProductsHaveDifferentIds() {
            // Given
            Product product1 = new Product("PROD-001", "Name", "Desc", new BigDecimal("10.00"), true);
            Product product2 = new Product("PROD-002", "Name", "Desc", new BigDecimal("10.00"), true);

            // When & Then
            assertThat(product1).isNotEqualTo(product2);
        }

        @Test
        @DisplayName("Should not be equal to null")
        void shouldNotBeEqualToNull() {
            // Given
            Product product = new Product("PROD-001", "Name", "Desc", new BigDecimal("10.00"), true);

            // When & Then
            assertThat(product).isNotEqualTo(null);
        }

        @Test
        @DisplayName("Should not be equal to different class")
        void shouldNotBeEqualToDifferentClass() {
            // Given
            Product product = new Product("PROD-001", "Name", "Desc", new BigDecimal("10.00"), true);
            String otherObject = "Not a product";

            // When & Then
            assertThat(product).isNotEqualTo(otherObject);
        }

        @Test
        @DisplayName("Should be equal to itself")
        void shouldBeEqualToItself() {
            // Given
            Product product = new Product("PROD-001", "Name", "Desc", new BigDecimal("10.00"), true);

            // When & Then
            assertThat(product).isEqualTo(product);
        }
    }

    @Nested
    @DisplayName("Product ToString")
    class ProductToString {

        @Test
        @DisplayName("Should return string representation with all fields")
        void shouldReturnStringRepresentation() {
            // Given
            Product product = new Product("PROD-001", "Laptop", "Gaming laptop", new BigDecimal("1499.99"), true);

            // When
            String result = product.toString();

            // Then
            assertThat(result)
                    .contains("PROD-001")
                    .contains("Laptop")
                    .contains("Gaming laptop")
                    .contains("1499.99")
                    .contains("true");
        }

        @Test
        @DisplayName("Should handle null description in string representation")
        void shouldHandleNullDescriptionInString() {
            // Given
            Product product = new Product("PROD-002", "Mouse", null, new BigDecimal("29.99"), false);

            // When
            String result = product.toString();

            // Then
            assertThat(result)
                    .contains("PROD-002")
                    .contains("Mouse")
                    .contains("29.99")
                    .contains("false");
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @ParameterizedTest
        @ValueSource(strings = {"", " ", "VERY_LONG_PRODUCT_ID_THAT_EXCEEDS_NORMAL_LENGTH_123456789"})
        @DisplayName("Should handle various id formats")
        void shouldHandleVariousIdFormats(String id) {
            // Given & When
            Product product = new Product(id, "Product", "Description", new BigDecimal("10.00"), true);

            // Then
            assertThat(product.getId()).isEqualTo(id);
        }

        @Test
        @DisplayName("Should handle zero price")
        void shouldHandleZeroPrice() {
            // Given
            BigDecimal zeroPrice = BigDecimal.ZERO;

            // When
            Product product = new Product("PROD-001", "Free Item", "Free product", zeroPrice, true);

            // Then
            assertThat(product.getPrice()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("Should handle very large price")
        void shouldHandleVeryLargePrice() {
            // Given
            BigDecimal largePrice = new BigDecimal("999999999.99");

            // When
            Product product = new Product("PROD-001", "Expensive Item", "Very expensive", largePrice, true);

            // Then
            assertThat(product.getPrice()).isEqualByComparingTo(largePrice);
        }

        @Test
        @DisplayName("Should handle empty name")
        void shouldHandleEmptyName() {
            // Given & When
            Product product = new Product("PROD-001", "", "Description", new BigDecimal("10.00"), true);

            // Then
            assertThat(product.getName()).isEmpty();
        }

        @Test
        @DisplayName("Should handle empty description")
        void shouldHandleEmptyDescription() {
            // Given & When
            Product product = new Product("PROD-001", "Product", "", new BigDecimal("10.00"), true);

            // Then
            assertThat(product.getDescription()).isEmpty();
        }
    }
}