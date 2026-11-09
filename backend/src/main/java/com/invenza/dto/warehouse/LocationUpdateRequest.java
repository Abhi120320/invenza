package com.invenza.dto.warehouse;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public class LocationUpdateRequest {

    private String name;

    @JsonProperty("short_code")
    private String shortCode;

    @JsonProperty("warehouse_id")
    private UUID warehouseId;

    public LocationUpdateRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public UUID getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(UUID warehouseId) {
        this.warehouseId = warehouseId;
    }
}

