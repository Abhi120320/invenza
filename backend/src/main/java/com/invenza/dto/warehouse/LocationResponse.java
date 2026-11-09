package com.invenza.dto.warehouse;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.invenza.entity.Location;

import java.util.UUID;

public class LocationResponse {

    private UUID id;
    private String name;

    @JsonProperty("short_code")
    private String shortCode;

    @JsonProperty("warehouse_id")
    private UUID warehouseId;

    private WarehouseResponse warehouse;

    public LocationResponse() {
    }

    public LocationResponse(UUID id, String name, String shortCode, UUID warehouseId, WarehouseResponse warehouse) {
        this.id = id;
        this.name = name;
        this.shortCode = shortCode;
        this.warehouseId = warehouseId;
        this.warehouse = warehouse;
    }

    public static LocationResponse fromEntity(Location location) {
        if (location == null) return null;
        UUID whId = location.getWarehouse() != null ? location.getWarehouse().getId() : null;
        WarehouseResponse whResp = location.getWarehouse() != null ? WarehouseResponse.fromEntity(location.getWarehouse()) : null;
        return new LocationResponse(
                location.getId(),
                location.getName(),
                location.getShortCode(),
                whId,
                whResp
        );
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public WarehouseResponse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(WarehouseResponse warehouse) {
        this.warehouse = warehouse;
    }
}

