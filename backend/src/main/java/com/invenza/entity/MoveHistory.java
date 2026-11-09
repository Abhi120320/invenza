package com.invenza.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Domain entity representing an immutable ledger record of product movement.
 */
@Entity
@Table(name = "move_history", indexes = {
    @Index(name = "ix_move_history_date", columnList = "date"),
    @Index(name = "ix_move_history_operation_id", columnList = "operation_id")
})
public class MoveHistory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operation_id")
    private Operation operation;

    @Column(name = "reference", length = 100, nullable = false)
    private String reference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_location_id")
    private Location fromLocation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_location_id")
    private Location toLocation;

    @Column(name = "quantity", precision = 12, scale = 4, nullable = false)
    private BigDecimal quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "direction", nullable = false)
    private MoveDirection direction;

    @Column(name = "date", nullable = false)
    private Instant date = Instant.now();

    @Column(name = "contact", length = 255)
    private String contact;

    public MoveHistory() {
    }

    public MoveHistory(UUID id) {
        super(id);
    }

    public MoveHistory(Operation operation, String reference, Product product,
                       Location fromLocation, Location toLocation,
                       BigDecimal quantity, MoveDirection direction, String contact) {
        this.operation = operation;
        this.reference = reference;
        this.product = product;
        this.fromLocation = fromLocation;
        this.toLocation = toLocation;
        this.quantity = quantity;
        this.direction = direction;
        this.contact = contact;
        this.date = Instant.now();
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Location getFromLocation() {
        return fromLocation;
    }

    public void setFromLocation(Location fromLocation) {
        this.fromLocation = fromLocation;
    }

    public Location getToLocation() {
        return toLocation;
    }

    public void setToLocation(Location toLocation) {
        this.toLocation = toLocation;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public MoveDirection getDirection() {
        return direction;
    }

    public void setDirection(MoveDirection direction) {
        this.direction = direction;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}

