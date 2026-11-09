package com.invenza.dto.product;

import com.invenza.entity.Category;

import java.util.UUID;

public class CategoryResponse {

    private UUID id;
    private String name;

    public CategoryResponse() {
    }

    public CategoryResponse(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public static CategoryResponse fromEntity(Category category) {
        if (category == null) return null;
        return new CategoryResponse(category.getId(), category.getName());
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

