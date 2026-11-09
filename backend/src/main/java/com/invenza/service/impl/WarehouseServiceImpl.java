package com.invenza.service.impl;

import com.invenza.dto.warehouse.*;
import com.invenza.entity.Location;
import com.invenza.entity.Warehouse;
import com.invenza.exception.ResourceNotFoundException;
import com.invenza.repository.LocationRepository;
import com.invenza.repository.WarehouseRepository;
import com.invenza.service.WarehouseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final LocationRepository locationRepository;

    public WarehouseServiceImpl(WarehouseRepository warehouseRepository,
                                LocationRepository locationRepository) {
        this.warehouseRepository = warehouseRepository;
        this.locationRepository = locationRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<WarehouseResponse> listWarehouses() {
        return warehouseRepository.findAll().stream()
                .map(WarehouseResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public WarehouseResponse createWarehouse(WarehouseCreateRequest request) {
        Warehouse warehouse = new Warehouse(
                request.getName(),
                request.getShortCode(),
                request.getAddress()
        );
        Warehouse saved = warehouseRepository.save(warehouse);
        return WarehouseResponse.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public WarehouseResponse getWarehouse(UUID id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found"));
        return WarehouseResponse.fromEntity(warehouse);
    }

    @Override
    @Transactional
    public WarehouseResponse updateWarehouse(UUID id, WarehouseUpdateRequest request) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found"));

        if (request.getName() != null) warehouse.setName(request.getName());
        if (request.getShortCode() != null) warehouse.setShortCode(request.getShortCode());
        if (request.getAddress() != null) warehouse.setAddress(request.getAddress());

        Warehouse updated = warehouseRepository.save(warehouse);
        return WarehouseResponse.fromEntity(updated);
    }

    @Override
    @Transactional
    public void deleteWarehouse(UUID id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found"));
        warehouseRepository.delete(warehouse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocationResponse> listLocations(UUID warehouseId) {
        return locationRepository.findByWarehouseIdWithWarehouse(warehouseId).stream()
                .map(LocationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public LocationResponse createLocation(LocationCreateRequest request) {
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found"));

        Location location = new Location(
                request.getName(),
                request.getShortCode(),
                warehouse
        );

        Location saved = locationRepository.save(location);
        return LocationResponse.fromEntity(saved);
    }

    @Override
    @Transactional
    public LocationResponse updateLocation(UUID id, LocationUpdateRequest request) {
        Location location = locationRepository.findByIdWithWarehouse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found"));

        if (request.getName() != null) location.setName(request.getName());
        if (request.getShortCode() != null) location.setShortCode(request.getShortCode());
        if (request.getWarehouseId() != null) {
            Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found"));
            location.setWarehouse(warehouse);
        }

        Location updated = locationRepository.save(location);
        return LocationResponse.fromEntity(updated);
    }

    @Override
    @Transactional
    public void deleteLocation(UUID id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found"));
        locationRepository.delete(location);
    }
}

