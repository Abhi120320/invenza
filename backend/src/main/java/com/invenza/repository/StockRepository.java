package com.invenza.repository;

import com.invenza.entity.Stock;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StockRepository extends JpaRepository<Stock, UUID> {

    Optional<Stock> findByProductIdAndLocationId(UUID productId, UUID locationId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Stock s WHERE s.product.id = :productId AND s.location.id = :locationId")
    Optional<Stock> findWithLockByProductIdAndLocationId(@Param("productId") UUID productId, @Param("locationId") UUID locationId);

    @Query("SELECT s FROM Stock s " +
           "JOIN FETCH s.product p " +
           "LEFT JOIN FETCH p.category " +
           "JOIN FETCH s.location l " +
           "LEFT JOIN FETCH l.warehouse " +
           "WHERE (:locationId IS NULL OR s.location.id = :locationId) " +
           "AND (:productId IS NULL OR s.product.id = :productId) " +
           "AND (:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Stock> searchStock(@Param("locationId") UUID locationId,
                            @Param("productId") UUID productId,
                            @Param("search") String search);

    @Query("SELECT COUNT(DISTINCT s.product.id) FROM Stock s WHERE s.onHand > 0")
    long countDistinctProductsInStock();

    @Query("SELECT COUNT(DISTINCT s.product.id) FROM Stock s WHERE s.onHand <= 0")
    long countDistinctProductsOutOfStock();

    @Query("SELECT COUNT(DISTINCT s.product.id) FROM Stock s " +
           "WHERE s.onHand > 0 " +
           "AND (:warehouseId IS NULL OR s.location.warehouse.id = :warehouseId) " +
           "AND (:categoryId IS NULL OR s.product.category.id = :categoryId)")
    long countDistinctProductsInStockWithFilters(
            @Param("warehouseId") UUID warehouseId,
            @Param("categoryId") UUID categoryId);

    @Query("SELECT COALESCE(SUM(s.onHand), 0) FROM Stock s " +
           "WHERE (:warehouseId IS NULL OR s.location.warehouse.id = :warehouseId) " +
           "AND (:categoryId IS NULL OR s.product.category.id = :categoryId)")
    java.math.BigDecimal sumQuantityOnHandWithFilters(
            @Param("warehouseId") UUID warehouseId,
            @Param("categoryId") UUID categoryId);

}

