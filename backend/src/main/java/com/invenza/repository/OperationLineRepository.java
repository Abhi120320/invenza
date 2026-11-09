package com.invenza.repository;

import com.invenza.entity.OperationLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OperationLineRepository extends JpaRepository<OperationLine, UUID> {
    List<OperationLine> findByOperationId(UUID operationId);
    void deleteByOperationId(UUID operationId);
}

