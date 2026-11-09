package com.invenza.repository;

import com.invenza.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    Optional<Product> findBySku(String sku);

    boolean existsBySku(String sku);

    @Query("SELECT DISTINCT p FROM Product p " +
           "LEFT JOIN FETCH p.category " +
           "LEFT JOIN FETCH p.stockEntries " +
           "WHERE (:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:categoryId IS NULL OR p.category.id = :categoryId) " +
           "ORDER BY p.createdAt DESC")
    List<Product> searchProducts(@Param("search") String search, @Param("categoryId") UUID categoryId);

    @Query("SELECT p FROM Product p " +
           "LEFT JOIN FETCH p.category " +
           "LEFT JOIN FETCH p.stockEntries " +
           "WHERE p.id = :id")
    Optional<Product> findByIdWithDetails(@Param("id") UUID id);

    @Query("SELECT COUNT(p) FROM Product p")
    long countTotalProducts();

    @Query("SELECT COUNT(p) FROM Product p WHERE p.reorderThreshold IS NOT NULL AND " +
           "(COALESCE((SELECT SUM(s.onHand) FROM Stock s WHERE s.product.id = p.id), 0) <= p.reorderThreshold)")
    long countLowStockProducts();

    @Query("SELECT COUNT(p) FROM Product p WHERE (:categoryId IS NULL OR p.category.id = :categoryId)")
    long countProductsByCategory(@Param("categoryId") UUID categoryId);

    @Query("SELECT COUNT(p) FROM Product p WHERE (:categoryId IS NULL OR p.category.id = :categoryId) AND " +
           "((p.reorderThreshold IS NOT NULL AND (COALESCE((SELECT SUM(s.onHand) FROM Stock s WHERE s.product.id = p.id), 0) <= p.reorderThreshold)) " +
           "OR COALESCE((SELECT SUM(s.onHand) FROM Stock s WHERE s.product.id = p.id), 0) <= 0)")
    long countLowStockProductsByCategory(@Param("categoryId") UUID categoryId);

}

