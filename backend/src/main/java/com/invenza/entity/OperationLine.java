package com.invenza.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Domain entity representing a line item in an inventory operation.
 */
@Entity
@Table(name = "operation_lines", indexes = {
    @Index(name = "ix_operation_lines_operation_id", columnList = "operation_id")
})
public class OperationLine extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operation_id", nullable = false)
    private Operation operation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "quantity", nullable = false)
    private BigDecimal quantity;

    @Column(name = "done_quantity", nullable = false)
    private BigDecimal doneQuantity = BigDecimal.ZERO;

    public OperationLine() {
    }

    public OperationLine(UUID id) {
        super(id);
    }

    public OperationLine(Operation operation, Product product, BigDecimal quantity) {
        this.operation = operation;
        this.product = product;
        this.quantity = quantity;
        this.doneQuantity = BigDecimal.ZERO;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
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
        this.doneQuantity = doneQuantity != null ? doneQuantity : BigDecimal.ZERO;
    }
}

