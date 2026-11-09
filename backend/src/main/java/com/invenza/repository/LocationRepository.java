package com.invenza.repository;

import com.invenza.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LocationRepository extends JpaRepository<Location, UUID> {

    @Query("SELECT l FROM Location l LEFT JOIN FETCH l.warehouse WHERE (:warehouseId IS NULL OR l.warehouse.id = :warehouseId)")
    List<Location> findByWarehouseIdWithWarehouse(@Param("warehouseId") UUID warehouseId);

    @Query("SELECT l FROM Location l LEFT JOIN FETCH l.warehouse WHERE l.id = :id")
    Optional<Location> findByIdWithWarehouse(@Param("id") UUID id);

    Optional<Location> findFirstByOrderByIdAsc();
}

