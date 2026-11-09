package com.invenza.strategy.operation;

import com.invenza.entity.*;
import com.invenza.repository.MoveHistoryRepository;
import com.invenza.repository.StockRepository;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Abstract base class providing common persistence logic and helper methods for operation handlers.
 * Demonstrates Template Method Pattern, Inheritance, and Encapsulation.
 */
public abstract class AbstractOperationHandler implements OperationHandler {

    protected final StockRepository stockRepository;
    protected final MoveHistoryRepository moveHistoryRepository;

    protected AbstractOperationHandler(StockRepository stockRepository,
                                       MoveHistoryRepository moveHistoryRepository) {
        this.stockRepository = stockRepository;
        this.moveHistoryRepository = moveHistoryRepository;
    }

    /**
     * Retrieves an existing Stock record with pessimistic lock or creates a new one.
     */
    protected Stock getOrCreateStock(Product product, Location location) {
        if (product == null || location == null) {
            throw new IllegalArgumentException("Product and Location cannot be null when accessing stock");
        }
        return stockRepository.findWithLockByProductIdAndLocationId(product.getId(), location.getId())
                .orElseGet(() -> {
                    Stock newStock = new Stock(product, location);
                    return stockRepository.save(newStock);
                });
    }

    /**
     * Records a move in the immutable ledger.
     */
    protected MoveHistory recordMoveHistory(Operation operation, Product product,
                                            Location fromLocation, Location toLocation,
                                            BigDecimal quantity, MoveDirection direction) {
        MoveHistory history = new MoveHistory(
                operation,
                operation.getReference(),
                product,
                fromLocation,
                toLocation,
                quantity,
                direction,
                operation.getContact()
        );
        history.setDate(Instant.now());
        return moveHistoryRepository.save(history);
    }

    @Override
    public OperationStatus calculateInitialStatus(Operation operation) {
        return OperationStatus.READY;
    }
}

