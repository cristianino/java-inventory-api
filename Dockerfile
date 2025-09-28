# Multi-stage build for Java 17 Spring Boot application
FROM maven:3.9.5-amazoncorretto-17 AS builder

WORKDIR /app

# Copy maven files for dependency caching
COPY pom.xml .

# Install dependencies
RUN mvn dependency:go-offline -B

# Copy source code
COPY src src

# Build the application
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Create non-root user
RUN addgroup --system --gid 1001 inventory
RUN adduser --system --uid 1001 --ingroup inventory inventory

# Copy the jar file from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Change ownership
RUN chown inventory:inventory app.jar

# Switch to non-root user
USER inventory

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]