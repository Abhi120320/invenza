package com.invenza.entity;

import com.invenza.exception.InsufficientStockException;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Domain entity representing stock of a specific product at a specific location.
 * Encapsulates stock mutations, reservations, and inventory consistency rules.
 */
@Entity
@Table(name = "stock", uniqueConstraints = {
    @UniqueConstraint(name = "uq_stock_product_location", columnNames = {"product_id", "location_id"})
}, indexes = {
    @Index(name = "ix_stock_product_id", columnList = "product_id"),
    @Index(name = "ix_stock_location_id", columnList = "location_id")
})
public class Stock extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Column(name = "per_unit_cost", precision = 12, scale = 4, nullable = false)
    private BigDecimal perUnitCost = BigDecimal.ZERO;

    @Column(name = "on_hand", precision = 12, scale = 4, nullable = false)
    private BigDecimal onHand = BigDecimal.ZERO;

    @Column(name = "reserved", precision = 12, scale = 4, nullable = false)
    private BigDecimal reserved = BigDecimal.ZERO;

    public Stock() {
    }

    public Stock(UUID id) {
        super(id);
    }

    public Stock(Product product, Location location) {
        this.product = product;
        this.location = location;
        this.onHand = BigDecimal.ZERO;
        this.reserved = BigDecimal.ZERO;
        this.perUnitCost = BigDecimal.ZERO;
    }

    public Stock(Product product, Location location, BigDecimal onHand, BigDecimal perUnitCost) {
        this.product = product;
        this.location = location;
        this.onHand = onHand != null ? onHand : BigDecimal.ZERO;
        this.perUnitCost = perUnitCost != null ? perUnitCost : BigDecimal.ZERO;
        this.reserved = BigDecimal.ZERO;
    }

    /**
     * Free stock available for allocation.
     */
    public BigDecimal getFreeToUse() {
        BigDecimal currentOnHand = this.onHand != null ? this.onHand : BigDecimal.ZERO;
        BigDecimal currentReserved = this.reserved != null ? this.reserved : BigDecimal.ZERO;
        return currentOnHand.subtract(currentReserved);
    }

    /**
     * Domain business method to add stock.
     */
    public void addStock(BigDecimal quantity) {
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Quantity to add must be non-negative");
        }
        this.onHand = (this.onHand != null ? this.onHand : BigDecimal.ZERO).add(quantity);
    }

    /**
     * Domain business method to deduct stock with availability guard.
     */
    public void deductStock(BigDecimal quantity) {
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Quantity to deduct must be non-negative");
        }
        BigDecimal available = getFreeToUse();
        if (available.compareTo(quantity) < 0) {
            String prodName = product != null ? product.getName() : "Product";
            throw new InsufficientStockException(
                String.format("Insufficient stock for %s. Available: %s, Requested: %s",
                    prodName, available.stripTrailingZeros().toPlainString(), quantity.stripTrailingZeros().toPlainString())
            );
        }
        this.onHand = this.onHand.subtract(quantity);
    }

    /**
     * Domain business method to adjust stock directly.
     */
    public void adjustStock(BigDecimal newQuantity) {
        if (newQuantity == null || newQuantity.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Quantity must be non-negative");
        }
        this.onHand = newQuantity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public BigDecimal getPerUnitCost() {
        return perUnitCost;
    }

    public void setPerUnitCost(BigDecimal perUnitCost) {
        this.perUnitCost = perUnitCost != null ? perUnitCost : BigDecimal.ZERO;
    }

    public BigDecimal getOnHand() {
        return onHand;
    }

    public void setOnHand(BigDecimal onHand) {
        this.onHand = onHand != null ? onHand : BigDecimal.ZERO;
    }

    public BigDecimal getReserved() {
        return reserved;
    }

    public void setReserved(BigDecimal reserved) {
        this.reserved = reserved != null ? reserved : BigDecimal.ZERO;
    }
}

