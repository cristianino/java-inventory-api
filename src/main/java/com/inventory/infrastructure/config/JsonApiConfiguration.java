package com.inventory.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jasminb.jsonapi.ResourceConverter;
import com.inventory.infrastructure.adapter.rest.dto.InventoryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JsonApiConfiguration {

    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    public ResourceConverter resourceConverter() {
        ResourceConverter converter = new ResourceConverter(objectMapper, InventoryDto.class);
        return converter;
    }
}