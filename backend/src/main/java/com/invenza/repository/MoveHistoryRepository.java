package com.invenza.repository;

import com.invenza.entity.MoveHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MoveHistoryRepository extends JpaRepository<MoveHistory, UUID> {

    @Query(value = "SELECT m FROM MoveHistory m " +
           "LEFT JOIN FETCH m.product " +
           "LEFT JOIN FETCH m.fromLocation " +
           "LEFT JOIN FETCH m.toLocation " +
           "WHERE (:search IS NULL OR LOWER(m.reference) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(m.contact) LIKE LOWER(CONCAT('%', :search, '%')))",
           countQuery = "SELECT COUNT(m) FROM MoveHistory m " +
           "WHERE (:search IS NULL OR LOWER(m.reference) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(m.contact) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<MoveHistory> searchMoveHistory(@Param("search") String search, Pageable pageable);
}

