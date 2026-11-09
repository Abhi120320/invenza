package com.invenza.dto.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductCreateRequest {

    @NotBlank(message = "Product name is required")
    private String name;

    @NotBlank(message = "SKU cannot be empty")
    private String sku;

    @JsonProperty("category_id")
    private UUID categoryId;

    private String uom = "pcs";

    @JsonProperty("reorder_threshold")
    private BigDecimal reorderThreshold;

    @JsonProperty("initial_stock")
    private BigDecimal initialStock;

    @JsonProperty("initial_location_id")
    private UUID initialLocationId;

    @JsonProperty("per_unit_cost")
    private BigDecimal perUnitCost = BigDecimal.ZERO;

    @JsonProperty("image_data")
    private String imageData;

    public ProductCreateRequest() {
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
        return uom != null && !uom.isBlank() ? uom : "pcs";
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

    public BigDecimal getInitialStock() {
        return initialStock;
    }

    public void setInitialStock(BigDecimal initialStock) {
        this.initialStock = initialStock;
    }

    public UUID getInitialLocationId() {
        return initialLocationId;
    }

    public void setInitialLocationId(UUID initialLocationId) {
        this.initialLocationId = initialLocationId;
    }

    public BigDecimal getPerUnitCost() {
        return perUnitCost != null ? perUnitCost : BigDecimal.ZERO;
    }

    public void setPerUnitCost(BigDecimal perUnitCost) {
        this.perUnitCost = perUnitCost;
    }

    public String getImageData() {
        return imageData;
    }

    public void setImageData(String imageData) {
        this.imageData = imageData;
    }
}

