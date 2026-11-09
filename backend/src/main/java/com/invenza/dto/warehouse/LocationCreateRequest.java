package com.invenza.dto.warehouse;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class LocationCreateRequest {

    @NotBlank(message = "Location name is required")
    private String name;

    @NotBlank(message = "Short code is required")
    @JsonProperty("short_code")
    private String shortCode;

    @NotNull(message = "Warehouse ID is required")
    @JsonProperty("warehouse_id")
    private UUID warehouseId;

    public LocationCreateRequest() {
    }

    public LocationCreateRequest(String name, String shortCode, UUID warehouseId) {
        this.name = name;
        this.shortCode = shortCode;
        this.warehouseId = warehouseId;
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

