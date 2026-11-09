package com.invenza.dto.history;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.invenza.entity.MoveDirection;
import com.invenza.entity.MoveHistory;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class MoveHistoryResponse {

    private UUID id;
    private String reference;

    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("from_location_name")
    private String fromLocationName;

    @JsonProperty("to_location_name")
    private String toLocationName;

    private BigDecimal quantity;
    private MoveDirection direction;
    private Instant date;
    private String contact;

    public MoveHistoryResponse() {
    }

    public static MoveHistoryResponse fromEntity(MoveHistory history) {
        if (history == null) return null;
        MoveHistoryResponse resp = new MoveHistoryResponse();
        resp.setId(history.getId());
        resp.setReference(history.getReference());
        resp.setProductName(history.getProduct() != null ? history.getProduct().getName() : null);
        resp.setFromLocationName(history.getFromLocation() != null ? history.getFromLocation().getName() : null);
        resp.setToLocationName(history.getToLocation() != null ? history.getToLocation().getName() : null);
        resp.setQuantity(history.getQuantity());
        resp.setDirection(history.getDirection());
        resp.setDate(history.getDate());
        resp.setContact(history.getContact());
        return resp;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getFromLocationName() {
        return fromLocationName;
    }

    public void setFromLocationName(String fromLocationName) {
        this.fromLocationName = fromLocationName;
    }

    public String getToLocationName() {
        return toLocationName;
    }

    public void setToLocationName(String toLocationName) {
        this.toLocationName = toLocationName;
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

