package com.inventory.infrastructure.adapter.rest.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Inventory DTO Tests")
class InventoryDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Nested
    @DisplayName("Valid DTO Creation")
    class ValidDtoCreation {

        @Test
        @DisplayName("Should create valid DTO with all fields")
        void shouldCreateValidDtoWithAllFields() {
            // Given
            UUID id = UUID.randomUUID();
            String productId = "product-123";
            Integer quantity = 100;
            LocalDateTime now = LocalDateTime.now();

            // When
            InventoryDto dto = new InventoryDto();
            dto.setId(id);
            dto.setProductId(productId);
            dto.setQuantity(quantity);
            dto.setCreatedAt(now);
            dto.setUpdatedAt(now);

            // Then
            assertThat(dto.getId()).isEqualTo(id);
            assertThat(dto.getProductId()).isEqualTo(productId);
            assertThat(dto.getQuantity()).isEqualTo(quantity);
            assertThat(dto.getCreatedAt()).isEqualTo(now);
            assertThat(dto.getUpdatedAt()).isEqualTo(now);

            Set<ConstraintViolation<InventoryDto>> violations = validator.validate(dto);
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should create DTO with zero quantity")
        void shouldCreateDtoWithZeroQuantity() {
            // Given
            InventoryDto dto = new InventoryDto();
            dto.setId(UUID.randomUUID());
            dto.setProductId("product-zero");
            dto.setQuantity(0);
            dto.setCreatedAt(LocalDateTime.now());
            dto.setUpdatedAt(LocalDateTime.now());

            // When
            Set<ConstraintViolation<InventoryDto>> violations = validator.validate(dto);

            // Then
            assertThat(violations).isEmpty();
            assertThat(dto.getQuantity()).isZero();
        }

        @Test
        @DisplayName("Should create DTO with null timestamps")
        void shouldCreateDtoWithNullTimestamps() {
            // Given
            InventoryDto dto = new InventoryDto();
            dto.setId(UUID.randomUUID());
            dto.setProductId("product-123");
            dto.setQuantity(100);
            dto.setCreatedAt(null);
            dto.setUpdatedAt(null);

            // When
            Set<ConstraintViolation<InventoryDto>> violations = validator.validate(dto);

            // Then
            assertThat(violations).isEmpty();
            assertThat(dto.getCreatedAt()).isNull();
            assertThat(dto.getUpdatedAt()).isNull();
        }
    }

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        @Test
        @DisplayName("Should fail validation when product ID is null")
        void shouldFailValidationWhenProductIdIsNull() {
            // Given
            InventoryDto dto = new InventoryDto();
            dto.setId(UUID.randomUUID());
            dto.setProductId(null);
            dto.setQuantity(100);

            // When
            Set<ConstraintViolation<InventoryDto>> violations = validator.validate(dto);

            // Then
            assertThat(violations).hasSize(1);
            ConstraintViolation<InventoryDto> violation = violations.iterator().next();
            assertThat(violation.getMessage()).isEqualTo("Product ID is required");
            assertThat(violation.getPropertyPath().toString()).isEqualTo("productId");
        }

        @Test
        @DisplayName("Should fail validation when product ID is blank")
        void shouldFailValidationWhenProductIdIsBlank() {
            // Given
            InventoryDto dto = new InventoryDto();
            dto.setId(UUID.randomUUID());
            dto.setProductId("");
            dto.setQuantity(100);

            // When
            Set<ConstraintViolation<InventoryDto>> violations = validator.validate(dto);

            // Then
            assertThat(violations).hasSize(1);
            ConstraintViolation<InventoryDto> violation = violations.iterator().next();
            assertThat(violation.getMessage()).isEqualTo("Product ID is required");
        }

        @Test
        @DisplayName("Should fail validation when product ID is whitespace only")
        void shouldFailValidationWhenProductIdIsWhitespaceOnly() {
            // Given
            InventoryDto dto = new InventoryDto();
            dto.setId(UUID.randomUUID());
            dto.setProductId("   ");
            dto.setQuantity(100);

            // When
            Set<ConstraintViolation<InventoryDto>> violations = validator.validate(dto);

            // Then
            assertThat(violations).hasSize(1);
            ConstraintViolation<InventoryDto> violation = violations.iterator().next();
            assertThat(violation.getMessage()).isEqualTo("Product ID is required");
        }

        @Test
        @DisplayName("Should fail validation when quantity is null")
        void shouldFailValidationWhenQuantityIsNull() {
            // Given
            InventoryDto dto = new InventoryDto();
            dto.setId(UUID.randomUUID());
            dto.setProductId("product-123");
            dto.setQuantity(null);

            // When
            Set<ConstraintViolation<InventoryDto>> violations = validator.validate(dto);

            // Then
            assertThat(violations).hasSize(1);
            ConstraintViolation<InventoryDto> violation = violations.iterator().next();
            assertThat(violation.getMessage()).isEqualTo("Quantity is required");
            assertThat(violation.getPropertyPath().toString()).isEqualTo("quantity");
        }

        @Test
        @DisplayName("Should fail validation when quantity is negative")
        void shouldFailValidationWhenQuantityIsNegative() {
            // Given
            InventoryDto dto = new InventoryDto();
            dto.setId(UUID.randomUUID());
            dto.setProductId("product-123");
            dto.setQuantity(-1);

            // When
            Set<ConstraintViolation<InventoryDto>> violations = validator.validate(dto);

            // Then
            assertThat(violations).hasSize(1);
            ConstraintViolation<InventoryDto> violation = violations.iterator().next();
            assertThat(violation.getMessage()).isEqualTo("Quantity cannot be negative");
            assertThat(violation.getPropertyPath().toString()).isEqualTo("quantity");
        }

        @Test
        @DisplayName("Should fail validation with multiple constraint violations")
        void shouldFailValidationWithMultipleConstraintViolations() {
            // Given
            InventoryDto dto = new InventoryDto();
            dto.setId(UUID.randomUUID());
            dto.setProductId("");
            dto.setQuantity(-10);

            // When
            Set<ConstraintViolation<InventoryDto>> violations = validator.validate(dto);

            // Then
            assertThat(violations).hasSize(2);
            assertThat(violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .toList())
                    .containsExactlyInAnyOrder(
                            "Product ID is required",
                            "Quantity cannot be negative"
                    );
        }
    }

    @Nested
    @DisplayName("Equality and Hash Code")
    class EqualityAndHashCode {

        @Test
        @DisplayName("Should be equal when all fields are the same")
        void shouldBeEqualWhenAllFieldsAreTheSame() {
            // Given
            UUID id = UUID.fromString("08677325-befe-41fe-8bde-07e00e7df198");
            String productId = "product-123";
            Integer quantity = 100;
            LocalDateTime createdAt = LocalDateTime.of(2023, 1, 1, 12, 0);
            LocalDateTime updatedAt = LocalDateTime.of(2023, 1, 1, 12, 30);

            InventoryDto dto1 = new InventoryDto();
            dto1.setId(id);
            dto1.setProductId(productId);
            dto1.setQuantity(quantity);
            dto1.setCreatedAt(createdAt);
            dto1.setUpdatedAt(updatedAt);

            InventoryDto dto2 = new InventoryDto();
            dto2.setId(id);
            dto2.setProductId(productId);
            dto2.setQuantity(quantity);
            dto2.setCreatedAt(createdAt);
            dto2.setUpdatedAt(updatedAt);

            // When & Then
            assertThat(dto1).isEqualTo(dto2);
            assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when IDs are different")
        void shouldNotBeEqualWhenIdsAreDifferent() {
            // Given
            InventoryDto dto1 = new InventoryDto();
            dto1.setId(UUID.randomUUID());
            dto1.setProductId("product-123");
            dto1.setQuantity(100);

            InventoryDto dto2 = new InventoryDto();
            dto2.setId(UUID.randomUUID());
            dto2.setProductId("product-123");
            dto2.setQuantity(100);

            // When & Then
            assertThat(dto1).isNotEqualTo(dto2);
        }

        @Test
        @DisplayName("Should not be equal to null")
        void shouldNotBeEqualToNull() {
            // Given
            InventoryDto dto = new InventoryDto();
            dto.setId(UUID.randomUUID());
            dto.setProductId("product-123");
            dto.setQuantity(100);

            // When & Then
            assertThat(dto).isNotEqualTo(null);
        }

        @Test
        @DisplayName("Should not be equal to different class")
        void shouldNotBeEqualToDifferentClass() {
            // Given
            InventoryDto dto = new InventoryDto();
            dto.setId(UUID.randomUUID());
            dto.setProductId("product-123");
            dto.setQuantity(100);

            String otherObject = "Not an InventoryDto";

            // When & Then
            assertThat(dto).isNotEqualTo(otherObject);
        }

        @Test
        @DisplayName("Should be equal to itself")
        void shouldBeEqualToItself() {
            // Given
            InventoryDto dto = new InventoryDto();
            dto.setId(UUID.randomUUID());
            dto.setProductId("product-123");
            dto.setQuantity(100);

            // When & Then
            assertThat(dto).isEqualTo(dto);
        }
    }

    @Nested
    @DisplayName("ToString Method")
    class ToStringMethod {

        @Test
        @DisplayName("Should return string representation with all fields")
        void shouldReturnStringRepresentationWithAllFields() {
            // Given
            UUID id = UUID.fromString("08677325-befe-41fe-8bde-07e00e7df198");
            String productId = "product-123";
            Integer quantity = 100;
            LocalDateTime now = LocalDateTime.now();

            InventoryDto dto = new InventoryDto();
            dto.setId(id);
            dto.setProductId(productId);
            dto.setQuantity(quantity);
            dto.setCreatedAt(now);
            dto.setUpdatedAt(now);

            // When
            String result = dto.toString();

            // Then
            assertThat(result)
                    .contains("InventoryDto")
                    .contains("08677325-befe-41fe-8bde-07e00e7df198")
                    .contains(productId)
                    .contains("100");
        }

        @Test
        @DisplayName("Should handle null values in toString")
        void shouldHandleNullValuesInToString() {
            // Given
            InventoryDto dto = new InventoryDto();
            dto.setId(null);
            dto.setProductId(null);
            dto.setQuantity(null);
            dto.setCreatedAt(null);
            dto.setUpdatedAt(null);

            // When
            String result = dto.toString();

            // Then
            assertThat(result).isNotNull();
            assertThat(result).contains("InventoryDto");
        }
    }
}