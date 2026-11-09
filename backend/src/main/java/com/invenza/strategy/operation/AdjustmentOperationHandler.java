package com.invenza.strategy.operation;

import com.invenza.entity.*;
import com.invenza.repository.MoveHistoryRepository;
import com.invenza.repository.StockRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Concrete strategy handling ADJUSTMENT operations (cycle counts, inventory reconciliation).
 * Computes quantity delta and reconciles stock to physical counted quantity.
 */
@Component
public class AdjustmentOperationHandler extends AbstractOperationHandler {

    public AdjustmentOperationHandler(StockRepository stockRepository,
                                      MoveHistoryRepository moveHistoryRepository) {
        super(stockRepository, moveHistoryRepository);
    }

    @Override
    public OperationType getType() {
        return OperationType.ADJUSTMENT;
    }

    @Override
    public void process(Operation operation) {
        Location location = operation.getToLocation() != null
                ? operation.getToLocation()
                : operation.getFromLocation();

        if (location == null) {
            throw new IllegalArgumentException("Location is required for Adjustment operations");
        }

        for (OperationLine line : operation.getLines()) {
            BigDecimal countedQty = line.getQuantity();
            Stock stock = getOrCreateStock(line.getProduct(), location);

            BigDecimal oldQty = stock.getOnHand() != null ? stock.getOnHand() : BigDecimal.ZERO;
            BigDecimal delta = countedQty.subtract(oldQty);

            stock.adjustStock(countedQty);
            stockRepository.save(stock);

            line.setDoneQuantity(countedQty);

            MoveDirection direction = delta.compareTo(BigDecimal.ZERO) >= 0 ? MoveDirection.IN : MoveDirection.OUT;
            BigDecimal moveQty = delta.abs();

            recordMoveHistory(
                    operation,
                    line.getProduct(),
                    operation.getFromLocation(),
                    operation.getToLocation(),
                    moveQty,
                    direction
            );
        }
    }
}

