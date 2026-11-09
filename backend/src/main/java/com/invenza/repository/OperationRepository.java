package com.invenza.repository;

import com.invenza.entity.Operation;
import com.invenza.entity.OperationStatus;
import com.invenza.entity.OperationType;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OperationRepository extends JpaRepository<Operation, UUID> {

    long countByWarehouseIdAndType(UUID warehouseId, OperationType type);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM Operation o WHERE o.id = :id")
    Optional<Operation> findWithLockById(@Param("id") UUID id);

    @Query(value = "SELECT DISTINCT o FROM Operation o " +
           "LEFT JOIN FETCH o.fromLocation " +
           "LEFT JOIN FETCH o.toLocation " +
           "LEFT JOIN FETCH o.warehouse " +
           "WHERE (:type IS NULL OR o.type = :type) " +
           "AND (:status IS NULL OR o.status = :status) " +
           "AND (:warehouseId IS NULL OR o.warehouse.id = :warehouseId) " +
           "AND (:search IS NULL OR LOWER(o.reference) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(o.contact) LIKE LOWER(CONCAT('%', :search, '%')))",
           countQuery = "SELECT COUNT(o) FROM Operation o " +
           "WHERE (:type IS NULL OR o.type = :type) " +
           "AND (:status IS NULL OR o.status = :status) " +
           "AND (:warehouseId IS NULL OR o.warehouse.id = :warehouseId) " +
           "AND (:search IS NULL OR LOWER(o.reference) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(o.contact) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Operation> searchOperations(@Param("type") OperationType type,
                                     @Param("status") OperationStatus status,
                                     @Param("warehouseId") UUID warehouseId,
                                     @Param("search") String search,
                                     Pageable pageable);

    @Query("SELECT o FROM Operation o " +
           "LEFT JOIN FETCH o.fromLocation " +
           "LEFT JOIN FETCH o.toLocation " +
           "LEFT JOIN FETCH o.warehouse " +
           "LEFT JOIN FETCH o.lines l " +
           "LEFT JOIN FETCH l.product p " +
           "LEFT JOIN FETCH p.category " +
           "WHERE o.id = :id")
    Optional<Operation> findByIdWithDetails(@Param("id") UUID id);

    long countByTypeAndStatusIn(OperationType type, Collection<OperationStatus> statuses);

    long countByTypeAndStatusInAndScheduleDateBefore(OperationType type, Collection<OperationStatus> statuses, LocalDate date);

    long countByTypeAndStatus(OperationType type, OperationStatus status);

    long countByType(OperationType type);

    long countByTypeAndStatusNotAndStatusNot(OperationType type, OperationStatus s1, OperationStatus s2);

    @Query("SELECT COUNT(o) FROM Operation o WHERE o.type = :type " +
           "AND (:warehouseId IS NULL OR o.warehouse.id = :warehouseId) " +
           "AND (:status IS NULL OR o.status = :status)")
    long countByTypeAndWarehouseAndStatus(
            @Param("type") OperationType type,
            @Param("warehouseId") UUID warehouseId,
            @Param("status") OperationStatus status);

    @Query("SELECT COUNT(o) FROM Operation o WHERE o.type = :type " +
           "AND (:warehouseId IS NULL OR o.warehouse.id = :warehouseId) " +
           "AND o.status IN :statuses")
    long countByTypeAndWarehouseAndStatusIn(
            @Param("type") OperationType type,
            @Param("warehouseId") UUID warehouseId,
            @Param("statuses") Collection<OperationStatus> statuses);

    @Query("SELECT COUNT(o) FROM Operation o WHERE o.type = :type " +
           "AND (:warehouseId IS NULL OR o.warehouse.id = :warehouseId) " +
           "AND o.status IN :statuses AND o.scheduleDate IS NOT NULL AND o.scheduleDate < :date")
    long countByTypeAndWarehouseAndStatusInAndScheduleDateBefore(
            @Param("type") OperationType type,
            @Param("warehouseId") UUID warehouseId,
            @Param("statuses") Collection<OperationStatus> statuses,
            @Param("date") LocalDate date);

    @Query("SELECT COUNT(o) FROM Operation o WHERE o.type = :type " +
           "AND (:warehouseId IS NULL OR o.warehouse.id = :warehouseId)")
    long countByTypeAndWarehouse(
            @Param("type") OperationType type,
            @Param("warehouseId") UUID warehouseId);

}

