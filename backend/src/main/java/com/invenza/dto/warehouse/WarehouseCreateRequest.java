package com.invenza.dto.warehouse;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public class WarehouseCreateRequest {

    @NotBlank(message = "Warehouse name is required")
    private String name;

    @NotBlank(message = "Short code is required")
    @JsonProperty("short_code")
    private String shortCode;

    private String address;

    public WarehouseCreateRequest() {
    }

    public WarehouseCreateRequest(String name, String shortCode, String address) {
        this.name = name;
        this.shortCode = shortCode;
        this.address = address;
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

