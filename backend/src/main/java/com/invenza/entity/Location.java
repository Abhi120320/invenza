package com.invenza.entity;

import jakarta.persistence.*;
import java.util.UUID;

/**
 * Domain entity representing a location inside a warehouse.
 * Demonstrates OOP Association with Warehouse.
 */
@Entity
@Table(name = "locations")
public class Location extends BaseEntity {

    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @Column(name = "short_code", length = 20, nullable = false)
    private String shortCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    public Location() {
    }

    public Location(UUID id) {
        super(id);
    }

    public Location(String name, String shortCode, Warehouse warehouse) {
        this.name = name;
        this.shortCode = shortCode;
        this.warehouse = warehouse;
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

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }
}

