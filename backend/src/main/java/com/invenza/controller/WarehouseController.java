package com.invenza.controller;

import com.invenza.dto.warehouse.*;
import com.invenza.service.WarehouseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class WarehouseController {

    private final WarehouseService warehouseService;

    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    // ── Warehouses ─────────────────────────────────────────────────────────────

    @GetMapping("/warehouses")
    public ResponseEntity<List<WarehouseResponse>> listWarehouses() {
        return ResponseEntity.ok(warehouseService.listWarehouses());
    }

    @PostMapping("/warehouses")
    @PreAuthorize("hasRole('INVENTORY_MANAGER')")
    public ResponseEntity<WarehouseResponse> createWarehouse(@Valid @RequestBody WarehouseCreateRequest request) {
        WarehouseResponse created = warehouseService.createWarehouse(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/warehouses/{id}")
    public ResponseEntity<WarehouseResponse> getWarehouse(@PathVariable UUID id) {
        return ResponseEntity.ok(warehouseService.getWarehouse(id));
    }

    @PutMapping("/warehouses/{id}")
    @PreAuthorize("hasRole('INVENTORY_MANAGER')")
    public ResponseEntity<WarehouseResponse> updateWarehouse(@PathVariable UUID id,
                                                            @RequestBody WarehouseUpdateRequest request) {
        return ResponseEntity.ok(warehouseService.updateWarehouse(id, request));
    }

    @DeleteMapping("/warehouses/{id}")
    @PreAuthorize("hasRole('INVENTORY_MANAGER')")
    public ResponseEntity<Void> deleteWarehouse(@PathVariable UUID id) {
        warehouseService.deleteWarehouse(id);
        return ResponseEntity.noContent().build();
    }

    // ── Locations ──────────────────────────────────────────────────────────────

    @GetMapping("/locations")
    public ResponseEntity<List<LocationResponse>> listLocations(
            @RequestParam(name = "warehouse_id", required = false) UUID warehouseId) {
        return ResponseEntity.ok(warehouseService.listLocations(warehouseId));
    }

    @PostMapping("/locations")
    @PreAuthorize("hasRole('INVENTORY_MANAGER')")
    public ResponseEntity<LocationResponse> createLocation(@Valid @RequestBody LocationCreateRequest request) {
        LocationResponse created = warehouseService.createLocation(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/locations/{id}")
    @PreAuthorize("hasRole('INVENTORY_MANAGER')")
    public ResponseEntity<LocationResponse> updateLocation(@PathVariable UUID id,
                                                          @RequestBody LocationUpdateRequest request) {
        return ResponseEntity.ok(warehouseService.updateLocation(id, request));
    }

    @DeleteMapping("/locations/{id}")
    @PreAuthorize("hasRole('INVENTORY_MANAGER')")
    public ResponseEntity<Void> deleteLocation(@PathVariable UUID id) {
        warehouseService.deleteLocation(id);
        return ResponseEntity.noContent().build();
    }
}

