package com.invenza.strategy.operation;

import com.invenza.entity.Operation;
import com.invenza.entity.OperationStatus;
import com.invenza.entity.OperationType;

/**
 * Strategy interface defining contracts for processing inventory operations.
 * Demonstrates OOP Abstraction and Strategy Pattern.
 */
public interface OperationHandler {

    /**
     * The operation type this handler handles.
     */
    OperationType getType();

    /**
     * Executes the stock mutation and movement ledger logic.
     *
     * @param operation the operation being validated
     */
    void process(Operation operation);

    /**
     * Evaluates initial or draft operation status (e.g., READY vs WAITING based on stock availability).
     *
     * @param operation the operation to check
     * @return the calculated OperationStatus
     */
    OperationStatus calculateInitialStatus(Operation operation);
}

