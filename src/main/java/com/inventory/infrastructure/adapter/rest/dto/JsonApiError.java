package com.inventory.infrastructure.adapter.rest.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * Representa la estructura de error en JSON:API
 */
public class JsonApiError {
    private String id;
    private String status;
    private String code;
    private String title;
    private String detail;
    private Map<String, Object> source;
    private Map<String, Object> meta;
    
    public JsonApiError() {
    }
    
    public JsonApiError id(String id) {
        this.id = id;
        return this;
    }
    
    public JsonApiError status(String status) {
        this.status = status;
        return this;
    }
    
    public JsonApiError code(String code) {
        this.code = code;
        return this;
    }
    
    public JsonApiError title(String title) {
        this.title = title;
        return this;
    }
    
    public JsonApiError detail(String detail) {
        this.detail = detail;
        return this;
    }
    
    public JsonApiError sourcePointer(String pointer) {
        if (this.source == null) {
            this.source = new HashMap<>();
        }
        this.source.put("pointer", pointer);
        return this;
    }
    
    public JsonApiError sourceParameter(String parameter) {
        if (this.source == null) {
            this.source = new HashMap<>();
        }
        this.source.put("parameter", parameter);
        return this;
    }
    
    // Getters para Jackson
    public String getId() {
        return id;
    }
    
    public String getStatus() {
        return status;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getDetail() {
        return detail;
    }
    
    public Map<String, Object> getSource() {
        return source;
    }
    
    public Map<String, Object> getMeta() {
        return meta;
    }
}