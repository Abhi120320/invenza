package com.invenza.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Domain entity representing a physical warehouse.
 * Demonstrates OOP Composition and Domain Methods.
 */
@Entity
@Table(name = "warehouses", uniqueConstraints = {
    @UniqueConstraint(name = "uq_warehouse_short_code", columnNames = {"short_code"})
})
public class Warehouse extends BaseEntity {

    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @Column(name = "short_code", length = 20, nullable = false, unique = true)
    private String shortCode;

    @Column(name = "address", length = 500)
    private String address;

    @OneToMany(mappedBy = "warehouse", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Location> locations = new ArrayList<>();

    public Warehouse() {
    }

    public Warehouse(UUID id) {
        super(id);
    }

    public Warehouse(String name, String shortCode, String address) {
        this.name = name;
        this.shortCode = shortCode;
        this.address = address;
    }

    public void addLocation(Location location) {
        if (location != null) {
            locations.add(location);
            location.setWarehouse(this);
        }
    }

    public void removeLocation(Location location) {
        if (location != null) {
            locations.remove(location);
            location.setWarehouse(null);
        }
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

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }
}

