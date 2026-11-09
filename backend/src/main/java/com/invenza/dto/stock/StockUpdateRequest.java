package com.invenza.dto.stock;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public class StockUpdateRequest {

    @JsonProperty("on_hand")
    private BigDecimal onHand;

    @JsonProperty("per_unit_cost")
    private BigDecimal perUnitCost;

    public StockUpdateRequest() {
    }

    public BigDecimal getOnHand() {
        return onHand;
    }

    public void setOnHand(BigDecimal onHand) {
        this.onHand = onHand;
    }

    public BigDecimal getPerUnitCost() {
        return perUnitCost;
    }

    public void setPerUnitCost(BigDecimal perUnitCost) {
        this.perUnitCost = perUnitCost;
    }
}

