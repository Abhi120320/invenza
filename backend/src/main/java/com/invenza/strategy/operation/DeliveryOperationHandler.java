package com.invenza.strategy.operation;

import com.invenza.entity.*;
import com.invenza.repository.MoveHistoryRepository;
import com.invenza.repository.StockRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Concrete strategy handling OUT (Delivery) operations.
 * Validates stock sufficiency and executes stock deductions.
 */
@Component
public class DeliveryOperationHandler extends AbstractOperationHandler {

    public DeliveryOperationHandler(StockRepository stockRepository,
                                    MoveHistoryRepository moveHistoryRepository) {
        super(stockRepository, moveHistoryRepository);
    }

    @Override
    public OperationType getType() {
        return OperationType.OUT;
    }

    @Override
    public void process(Operation operation) {
        Location fromLocation = operation.getFromLocation();
        if (fromLocation == null) {
            throw new IllegalArgumentException("Source location is required for Delivery (OUT) operations");
        }

        for (OperationLine line : operation.getLines()) {
            BigDecimal qty = line.getQuantity();
            Stock stock = getOrCreateStock(line.getProduct(), fromLocation);
            stock.deductStock(qty);
            stockRepository.save(stock);

            line.setDoneQuantity(qty);

            recordMoveHistory(
                    operation,
                    line.getProduct(),
                    fromLocation,
                    operation.getToLocation(),
                    qty,
                    MoveDirection.OUT
            );
        }
    }

    @Override
    public OperationStatus calculateInitialStatus(Operation operation) {
        Location fromLoc = operation.getFromLocation();
        if (fromLoc == null) {
            return OperationStatus.WAITING;
        }

        for (OperationLine line : operation.getLines()) {
            if (line.getProduct() == null) continue;
            Optional<Stock> stockOpt = stockRepository.findByProductIdAndLocationId(
                    line.getProduct().getId(),
                    fromLoc.getId()
            );

            if (stockOpt.isEmpty() || stockOpt.get().getFreeToUse().compareTo(line.getQuantity()) < 0) {
                return OperationStatus.WAITING;
            }
        }
        return OperationStatus.READY;
    }
}

