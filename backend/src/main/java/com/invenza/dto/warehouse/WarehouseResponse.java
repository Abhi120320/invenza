package com.invenza.dto.warehouse;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.invenza.entity.Warehouse;

import java.util.UUID;

public class WarehouseResponse {

    private UUID id;
    private String name;

    @JsonProperty("short_code")
    private String shortCode;

    private String address;

    public WarehouseResponse() {
    }

    public WarehouseResponse(UUID id, String name, String shortCode, String address) {
        this.id = id;
        this.name = name;
        this.shortCode = shortCode;
        this.address = address;
    }

    public static WarehouseResponse fromEntity(Warehouse warehouse) {
        if (warehouse == null) return null;
        return new WarehouseResponse(
                warehouse.getId(),
                warehouse.getName(),
                warehouse.getShortCode(),
                warehouse.getAddress()
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

