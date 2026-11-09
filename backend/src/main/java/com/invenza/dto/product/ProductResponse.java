package com.invenza.dto.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.invenza.entity.Product;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductResponse {

    private UUID id;
    private String name;
    private String sku;
    private String uom;

    @JsonProperty("reorder_threshold")
    private BigDecimal reorderThreshold;

    @JsonProperty("current_stock")
    private BigDecimal currentStock = BigDecimal.ZERO;

    private CategoryResponse category;

    @JsonProperty("image_data")
    private String imageData;

    public ProductResponse() {
    }

    public ProductResponse(UUID id, String name, String sku, String uom, BigDecimal reorderThreshold,
                           BigDecimal currentStock, CategoryResponse category, String imageData) {
        this.id = id;
        this.name = name;
        this.sku = sku;
        this.uom = uom;
        this.reorderThreshold = reorderThreshold;
        this.currentStock = currentStock != null ? currentStock : BigDecimal.ZERO;
        this.category = category;
        this.imageData = imageData;
    }

    public static ProductResponse fromEntity(Product product) {
        if (product == null) return null;
        CategoryResponse catResp = product.getCategory() != null ? CategoryResponse.fromEntity(product.getCategory()) : null;
        BigDecimal stock = product.calculateCurrentStock();
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getSku(),
                product.getUom(),
                product.getReorderThreshold(),
                stock,
                catResp,
                product.getImageData()
        );
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

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
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

    public BigDecimal getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(BigDecimal currentStock) {
        this.currentStock = currentStock;
    }

    public CategoryResponse getCategory() {
        return category;
    }

    public void setCategory(CategoryResponse category) {
        this.category = category;
    }

    public String getImageData() {
        return imageData;
    }

    public void setImageData(String imageData) {
        this.imageData = imageData;
    }
}

