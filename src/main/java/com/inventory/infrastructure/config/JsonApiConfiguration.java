package com.inventory.infrastructure.config;

import com.github.jasminb.jsonapi.ResourceConverter;
import com.inventory.infrastructure.adapter.rest.dto.InventoryDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JsonApiConfiguration {

    @Bean
    public ResourceConverter resourceConverter() {
        return new ResourceConverter(InventoryDto.class);
    }
}