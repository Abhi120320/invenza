package com.invenza.strategy.operation;

import com.invenza.entity.*;
import com.invenza.repository.MoveHistoryRepository;
import com.invenza.repository.StockRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Concrete strategy handling IN (Receipt) operations.
 * Polymorphically executes stock additions and IN ledger movements.
 */
@Component
public class ReceiptOperationHandler extends AbstractOperationHandler {

    public ReceiptOperationHandler(StockRepository stockRepository,
                                   MoveHistoryRepository moveHistoryRepository) {
        super(stockRepository, moveHistoryRepository);
    }

    @Override
    public OperationType getType() {
        return OperationType.IN;
    }

    @Override
    public void process(Operation operation) {
        Location toLocation = operation.getToLocation();
        if (toLocation == null) {
            throw new IllegalArgumentException("Destination location is required for Receipt (IN) operations");
        }

        for (OperationLine line : operation.getLines()) {
            BigDecimal qty = line.getQuantity();
            Stock stock = getOrCreateStock(line.getProduct(), toLocation);
            stock.addStock(qty);
            stockRepository.save(stock);

            line.setDoneQuantity(qty);

            recordMoveHistory(
                    operation,
                    line.getProduct(),
                    operation.getFromLocation(),
                    toLocation,
                    qty,
                    MoveDirection.IN
            );
        }
    }
}

