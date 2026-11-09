package com.invenza.service;

import com.invenza.dto.warehouse.*;

import java.util.List;
import java.util.UUID;

public interface WarehouseService {
    List<WarehouseResponse> listWarehouses();
    WarehouseResponse createWarehouse(WarehouseCreateRequest request);
    WarehouseResponse getWarehouse(UUID id);
    WarehouseResponse updateWarehouse(UUID id, WarehouseUpdateRequest request);
    void deleteWarehouse(UUID id);

    List<LocationResponse> listLocations(UUID warehouseId);
    LocationResponse createLocation(LocationCreateRequest request);
    LocationResponse updateLocation(UUID id, LocationUpdateRequest request);
    void deleteLocation(UUID id);
}

