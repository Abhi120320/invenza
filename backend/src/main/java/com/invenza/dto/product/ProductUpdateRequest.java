package com.invenza.dto.product;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductUpdateRequest {

    private String name;
    private String sku;

    @JsonProperty("category_id")
    private UUID categoryId;

    private String uom;

    @JsonProperty("reorder_threshold")
    private BigDecimal reorderThreshold;

    @JsonProperty("image_data")
    private String imageData;

    public ProductUpdateRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSku() {
        return sku != null ? sku.trim().toUpperCase() : null;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public BigDecimal getReorderThreshold() {
        return reorderThreshold;
    }

    public void setReorderThreshold(BigDecimal reorderThreshold) {
        this.reorderThreshold = reorderThreshold;
    }

    public String getImageData() {
        return imageData;
    }

    public void setImageData(String imageData) {
        this.imageData = imageData;
    }
}

