package com.invenza.strategy.operation;

import com.invenza.entity.OperationType;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Factory and registry resolving OperationHandler strategies at runtime.
 * Demonstrates OOP Factory Pattern and the Open/Closed Principle (OCP).
 */
@Component
public class OperationHandlerFactory {

    private final Map<OperationType, OperationHandler> handlerMap = new EnumMap<>(OperationType.class);

    public OperationHandlerFactory(List<OperationHandler> handlers) {
        for (OperationHandler handler : handlers) {
            handlerMap.put(handler.getType(), handler);
        }
    }

    /**
     * Resolves the strategy for the given operation type.
     *
     * @param type the operation type
     * @return the corresponding OperationHandler
     * @throws IllegalArgumentException if no handler is registered
     */
    public OperationHandler getHandler(OperationType type) {
        OperationHandler handler = handlerMap.get(type);
        if (handler == null) {
            throw new IllegalArgumentException("No handler registered for operation type: " + type);
        }
        return handler;
    }
}

