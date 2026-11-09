package com.invenza.dto.stock;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.invenza.entity.Stock;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public class StockResponse {

    private UUID id;

    @JsonProperty("product_id")
    private UUID productId;

    @JsonProperty("location_id")
    private UUID locationId;

    @JsonProperty("on_hand")
    private BigDecimal onHand;

    @JsonProperty("reserved")
    private BigDecimal reserved;

    @JsonProperty("free_to_use")
    private BigDecimal freeToUse;

    @JsonProperty("per_unit_cost")
    private BigDecimal perUnitCost;

    private Map<String, Object> product;
    private Map<String, Object> location;

    public StockResponse() {
    }

    public static StockResponse fromEntity(Stock stock) {
        if (stock == null) return null;
        StockResponse resp = new StockResponse();
        resp.setId(stock.getId());
        resp.setProductId(stock.getProduct() != null ? stock.getProduct().getId() : null);
        resp.setLocationId(stock.getLocation() != null ? stock.getLocation().getId() : null);
        resp.setOnHand(stock.getOnHand() != null ? stock.getOnHand() : BigDecimal.ZERO);
        resp.setReserved(stock.getReserved() != null ? stock.getReserved() : BigDecimal.ZERO);
        resp.setFreeToUse(stock.getFreeToUse());
        resp.setPerUnitCost(stock.getPerUnitCost() != null ? stock.getPerUnitCost() : BigDecimal.ZERO);

        if (stock.getProduct() != null) {
            resp.setProduct(Map.of(
                    "id", stock.getProduct().getId().toString(),
                    "name", stock.getProduct().getName(),
                    "sku", stock.getProduct().getSku(),
                    "uom", stock.getProduct().getUom()
            ));
        }

        if (stock.getLocation() != null) {
            resp.setLocation(Map.of(
                    "id", stock.getLocation().getId().toString(),
                    "name", stock.getLocation().getName()
            ));
        }

        return resp;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public UUID getLocationId() {
        return locationId;
    }

    public void setLocationId(UUID locationId) {
        this.locationId = locationId;
    }

    public BigDecimal getOnHand() {
        return onHand;
    }

    public void setOnHand(BigDecimal onHand) {
        this.onHand = onHand;
    }

    public BigDecimal getReserved() {
        return reserved;
    }

    public void setReserved(BigDecimal reserved) {
        this.reserved = reserved;
    }

    public BigDecimal getFreeToUse() {
        return freeToUse;
    }

    public void setFreeToUse(BigDecimal freeToUse) {
        this.freeToUse = freeToUse;
    }

    public BigDecimal getPerUnitCost() {
        return perUnitCost;
    }

    public void setPerUnitCost(BigDecimal perUnitCost) {
        this.perUnitCost = perUnitCost;
    }

    public Map<String, Object> getProduct() {
        return product;
    }

    public void setProduct(Map<String, Object> product) {
        this.product = product;
    }

    public Map<String, Object> getLocation() {
        return location;
    }

    public void setLocation(Map<String, Object> location) {
        this.location = location;
    }
}

