package com.invenza.strategy.operation;

import com.invenza.entity.*;
import com.invenza.repository.MoveHistoryRepository;
import com.invenza.repository.StockRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Concrete strategy handling TRANSFER operations between warehouse locations.
 * Atomically shifts stock from source to destination without changing total inventory.
 */
@Component
public class TransferOperationHandler extends AbstractOperationHandler {

    public TransferOperationHandler(StockRepository stockRepository,
                                    MoveHistoryRepository moveHistoryRepository) {
        super(stockRepository, moveHistoryRepository);
    }

    @Override
    public OperationType getType() {
        return OperationType.TRANSFER;
    }

    @Override
    public void process(Operation operation) {
        Location fromLocation = operation.getFromLocation();
        Location toLocation = operation.getToLocation();

        if (fromLocation == null || toLocation == null) {
            throw new IllegalArgumentException("Both source and destination locations are required for Transfer operations");
        }

        for (OperationLine line : operation.getLines()) {
            BigDecimal qty = line.getQuantity();

            Stock sourceStock = getOrCreateStock(line.getProduct(), fromLocation);
            sourceStock.deductStock(qty);
            stockRepository.save(sourceStock);

            Stock destStock = getOrCreateStock(line.getProduct(), toLocation);
            destStock.addStock(qty);
            stockRepository.save(destStock);

            line.setDoneQuantity(qty);

            recordMoveHistory(
                    operation,
                    line.getProduct(),
                    fromLocation,
                    toLocation,
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

