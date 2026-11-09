package com.invenza.dto.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.UUID;

public class CreateOrderResponse {

    @JsonProperty("order_id")
    private String orderId;

    private BigDecimal amount;

    @JsonProperty("amount_paise")
    private long amountPaise;

    private String currency;

    @JsonProperty("key_id")
    private String keyId;

    @JsonProperty("operation_id")
    private UUID operationId;

    private String reference;

    public CreateOrderResponse() {
    }

    public CreateOrderResponse(String orderId, BigDecimal amount, long amountPaise, String currency,
                               String keyId, UUID operationId, String reference) {
        this.orderId = orderId;
        this.amount = amount;
        this.amountPaise = amountPaise;
        this.currency = currency;
        this.keyId = keyId;
        this.operationId = operationId;
        this.reference = reference;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public long getAmountPaise() {
        return amountPaise;
    }

    public void setAmountPaise(long amountPaise) {
        this.amountPaise = amountPaise;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public UUID getOperationId() {
        return operationId;
    }

    public void setOperationId(UUID operationId) {
        this.operationId = operationId;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}

