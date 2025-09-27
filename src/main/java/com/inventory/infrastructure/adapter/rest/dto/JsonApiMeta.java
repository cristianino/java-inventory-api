package com.inventory.infrastructure.adapter.rest.dto;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Representa la estructura de meta información en JSON:API
 */
public class JsonApiMeta {
    private final Map<String, Object> meta = new HashMap<>();
    
    public JsonApiMeta totalCount(long totalCount) {
        meta.put("totalCount", totalCount);
        return this;
    }
    
    public JsonApiMeta pageSize(int pageSize) {
        meta.put("pageSize", pageSize);
        return this;
    }
    
    public JsonApiMeta currentPage(int currentPage) {
        meta.put("currentPage", currentPage);
        return this;
    }
    
    public JsonApiMeta totalPages(int totalPages) {
        meta.put("totalPages", totalPages);
        return this;
    }
    
    public JsonApiMeta timestamp(LocalDateTime timestamp) {
        meta.put("timestamp", timestamp.toString());
        return this;
    }
    
    public JsonApiMeta apiVersion(String version) {
        meta.put("apiVersion", version);
        return this;
    }
    
    public Map<String, Object> getMeta() {
        return meta;
    }
}