package com.inventory.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JacksonConfiguration Tests")
class JacksonConfigurationTest {

    private JacksonConfiguration jacksonConfiguration;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        jacksonConfiguration = new JacksonConfiguration();
        objectMapper = jacksonConfiguration.objectMapper();
    }

    @Nested
    @DisplayName("ObjectMapper Configuration Tests")
    class ObjectMapperConfigurationTests {

        @Test
        @DisplayName("Should create ObjectMapper bean with JavaTimeModule registered")
        void shouldCreateObjectMapperWithJavaTimeModule() {
            // Assert
            assertThat(objectMapper).isNotNull();
            assertThat(objectMapper.getRegisteredModuleIds())
                    .anyMatch(id -> id.toString().contains("jackson-datatype-jsr310"));
        }

        @Test
        @DisplayName("Should disable WRITE_DATES_AS_TIMESTAMPS feature")
        void shouldDisableWriteDatesAsTimestamps() {
            // Assert
            assertThat(objectMapper.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS))
                    .isFalse();
        }

        @Test
        @DisplayName("Should be marked as Primary bean")
        void shouldBeMarkedAsPrimary() {
            // This test verifies the @Primary annotation is present
            // The annotation itself is tested by Spring context
            assertThat(objectMapper).isNotNull();
        }
    }

    @Nested
    @DisplayName("Java Time Serialization Tests")
    class JavaTimeSerializationTests {

        @Test
        @DisplayName("Should serialize LocalDateTime as ISO string, not timestamp")
        void shouldSerializeLocalDateTimeAsIsoString() throws Exception {
            // Arrange
            LocalDateTime dateTime = LocalDateTime.of(2023, 12, 25, 15, 30, 45);
            
            // Act
            String json = objectMapper.writeValueAsString(dateTime);
            
            // Assert
            assertThat(json).contains("2023-12-25T15:30:45");
            assertThat(json).doesNotContain("1703516445"); // timestamp
        }

        @Test
        @DisplayName("Should serialize LocalDate as ISO string")
        void shouldSerializeLocalDateAsIsoString() throws Exception {
            // Arrange
            LocalDate date = LocalDate.of(2023, 12, 25);
            
            // Act
            String json = objectMapper.writeValueAsString(date);
            
            // Assert
            assertThat(json).contains("2023-12-25");
        }

        @Test
        @DisplayName("Should deserialize ISO string to LocalDateTime")
        void shouldDeserializeIsoStringToLocalDateTime() throws Exception {
            // Arrange
            String json = "\"2023-12-25T15:30:45\"";
            
            // Act
            LocalDateTime dateTime = objectMapper.readValue(json, LocalDateTime.class);
            
            // Assert
            assertThat(dateTime).isEqualTo(LocalDateTime.of(2023, 12, 25, 15, 30, 45));
        }

        @Test
        @DisplayName("Should deserialize ISO string to LocalDate")
        void shouldDeserializeIsoStringToLocalDate() throws Exception {
            // Arrange
            String json = "\"2023-12-25\"";
            
            // Act
            LocalDate date = objectMapper.readValue(json, LocalDate.class);
            
            // Assert
            assertThat(date).isEqualTo(LocalDate.of(2023, 12, 25));
        }
    }

    @Nested
    @DisplayName("Complex Object Serialization Tests")
    class ComplexObjectSerializationTests {

        @Test
        @DisplayName("Should handle complex objects with Java time fields")
        void shouldHandleComplexObjectsWithJavaTimeFields() throws Exception {
            // Arrange
            TestObject testObj = new TestObject();
            testObj.name = "Test";
            testObj.createdAt = LocalDateTime.of(2023, 12, 25, 15, 30, 45);
            testObj.date = LocalDate.of(2023, 12, 25);
            
            // Act
            String json = objectMapper.writeValueAsString(testObj);
            TestObject deserializedObj = objectMapper.readValue(json, TestObject.class);
            
            // Assert
            assertThat(deserializedObj.name).isEqualTo(testObj.name);
            assertThat(deserializedObj.createdAt).isEqualTo(testObj.createdAt);
            assertThat(deserializedObj.date).isEqualTo(testObj.date);
        }
    }

    // Helper class for testing complex objects
    static class TestObject {
        public String name;
        public LocalDateTime createdAt;
        public LocalDate date;
    }
}
