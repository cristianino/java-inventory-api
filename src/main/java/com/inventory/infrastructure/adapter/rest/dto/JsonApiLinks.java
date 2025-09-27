package com.inventory.infrastructure.adapter.rest.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * Representa la estructura de links en JSON:API
 */
public class JsonApiLinks {
    private final Map<String, String> links = new HashMap<>();
    
    public JsonApiLinks self(String self) {
        links.put("self", self);
        return this;
    }
    
    public JsonApiLinks first(String first) {
        links.put("first", first);
        return this;
    }
    
    public JsonApiLinks last(String last) {
        links.put("last", last);
        return this;
    }
    
    public JsonApiLinks next(String next) {
        if (next != null) {
            links.put("next", next);
        }
        return this;
    }
    
    public JsonApiLinks prev(String prev) {
        if (prev != null) {
            links.put("prev", prev);
        }
        return this;
    }
    
    public Map<String, String> getLinks() {
        return links;
    }
}