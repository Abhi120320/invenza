package com.invenza.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Domain entity representing an inventory product.
 * Encapsulates SKU validation and current stock computation.
 */
@Entity
@Table(name = "products", indexes = {
    @Index(name = "ix_products_sku", columnList = "sku", unique = true)
})
public class Product extends BaseEntity {

    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @Column(name = "sku", length = 100, nullable = false, unique = true)
    private String sku;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", foreignKey = @ForeignKey(name = "fk_product_category"))
    private Category category;

    @Column(name = "uom", length = 50, nullable = false)
    private String uom = "pcs";

    @Column(name = "reorder_threshold", precision = 12, scale = 4)
    private BigDecimal reorderThreshold;

    @Column(name = "image_data", columnDefinition = "TEXT")
    private String imageData;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Stock> stockEntries = new ArrayList<>();

    public Product() {
    }

    public Product(UUID id) {
        super(id);
    }

    public Product(String name, String sku, Category category, String uom, BigDecimal reorderThreshold, String imageData) {
        this.name = name;
        this.sku = sku != null ? sku.trim().toUpperCase() : null;
        this.category = category;
        this.uom = (uom != null && !uom.isBlank()) ? uom : "pcs";
        this.reorderThreshold = reorderThreshold;
        this.imageData = imageData;
    }

    /**
     * Domain business method calculating total stock on hand across all locations.
     */
    public BigDecimal calculateCurrentStock() {
        if (stockEntries == null || stockEntries.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return stockEntries.stream()
                .map(Stock::getOnHand)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
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
        this.sku = sku != null ? sku.trim().toUpperCase() : null;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
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

    public List<Stock> getStockEntries() {
        return stockEntries;
    }

    public void setStockEntries(List<Stock> stockEntries) {
        this.stockEntries = stockEntries;
    }
}

