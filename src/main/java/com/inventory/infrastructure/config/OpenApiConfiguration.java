package com.inventory.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfiguration {

    private static final String API_KEY_SECURITY_SCHEME = "X-API-Key";

    @Bean
    public OpenAPI inventoryOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Java Inventory API")
                        .description("""
                            Spring Boot 3 microservice for inventory management using Hexagonal Architecture.
                            
                            **API Versioning Strategy:**
                            - **Default endpoints** (`/api/inventory`, `/api/system`): Always point to the latest version
                            - **Versioned endpoints** (`/api/v1/inventory`, `/api/v1/system`): Specific version endpoints
                            
                            **Current Version:** v1.0.0
                            
                            **Authentication:** Use 'your-secret-api-key-here' as the API key for testing.
                            """)
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Inventory Team")
                                .email("inventory@example.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Development Server"),
                        new Server().url("https://api.inventory.example.com").description("Production Server")
                ))
                .addSecurityItem(new SecurityRequirement().addList(API_KEY_SECURITY_SCHEME))
                .components(new Components()
                        .addSecuritySchemes(API_KEY_SECURITY_SCHEME,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                                        .name("X-API-Key")
                                        .description("API key for authentication. Use: your-secret-api-key-here")));
    }
}