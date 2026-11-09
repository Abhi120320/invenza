package com.invenza.dto.operation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.invenza.dto.product.ProductResponse;
import com.invenza.entity.OperationLine;

import java.math.BigDecimal;
import java.util.UUID;

public class OperationLineResponse {

    private UUID id;

    @JsonProperty("product_id")
    private UUID productId;

    private BigDecimal quantity;

    @JsonProperty("done_quantity")
    private BigDecimal doneQuantity;

    private ProductResponse product;

    public OperationLineResponse() {
    }

    public static OperationLineResponse fromEntity(OperationLine line) {
        if (line == null) return null;
        OperationLineResponse resp = new OperationLineResponse();
        resp.setId(line.getId());
        resp.setProductId(line.getProduct() != null ? line.getProduct().getId() : null);
        resp.setQuantity(line.getQuantity());
        resp.setDoneQuantity(line.getDoneQuantity() != null ? line.getDoneQuantity() : BigDecimal.ZERO);
        if (line.getProduct() != null) {
            resp.setProduct(ProductResponse.fromEntity(line.getProduct()));
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

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getDoneQuantity() {
        return doneQuantity;
    }

    public void setDoneQuantity(BigDecimal doneQuantity) {
        this.doneQuantity = doneQuantity;
    }

    public ProductResponse getProduct() {
        return product;
    }

    public void setProduct(ProductResponse product) {
        this.product = product;
    }
}

