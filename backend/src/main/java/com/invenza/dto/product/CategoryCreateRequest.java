package com.invenza.dto.product;

import jakarta.validation.constraints.NotBlank;

public class CategoryCreateRequest {

    @NotBlank(message = "Category name is required")
    private String name;

    public CategoryCreateRequest() {
    }

    public CategoryCreateRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

