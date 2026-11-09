package com.invenza.dto.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public class CreateOrderRequest {

    @NotNull(message = "operation_id is required")
    @JsonProperty("operation_id")
    private UUID operationId;

    private BigDecimal amount;

    private String currency;

    public CreateOrderRequest() {
    }

    public CreateOrderRequest(UUID operationId, BigDecimal amount, String currency) {
        this.operationId = operationId;
        this.amount = amount;
        this.currency = currency;
    }

    public UUID getOperationId() {
        return operationId;
    }

    public void setOperationId(UUID operationId) {
        this.operationId = operationId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}

