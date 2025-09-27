package com.inventory.infrastructure.adapter.rest.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Representa la respuesta completa JSON:API
 */
public class JsonApiResponse {
    private Object data;
    private Map<String, Object> meta;
    private Map<String, String> links;
    private List<JsonApiError> errors;
    
    public JsonApiResponse() {
    }
    
    public JsonApiResponse data(Object data) {
        this.data = data;
        return this;
    }
    
    public JsonApiResponse meta(JsonApiMeta meta) {
        this.meta = meta.getMeta();
        return this;
    }
    
    public JsonApiResponse links(JsonApiLinks links) {
        this.links = links.getLinks();
        return this;
    }
    
    public JsonApiResponse errors(List<JsonApiError> errors) {
        this.errors = errors;
        this.data = null; // En JSON:API, no puede haber data y errors al mismo tiempo
        return this;
    }
    
    public JsonApiResponse addError(JsonApiError error) {
        if (this.errors == null) {
            this.errors = new ArrayList<>();
        }
        this.errors.add(error);
        this.data = null; // En JSON:API, no puede haber data y errors al mismo tiempo
        return this;
    }
    
    // Getters para Jackson
    public Object getData() {
        return data;
    }
    
    public Map<String, Object> getMeta() {
        return meta;
    }
    
    public Map<String, String> getLinks() {
        return links;
    }
    
    public List<JsonApiError> getErrors() {
        return errors;
    }
}