package com.invenza.event;

import org.springframework.context.ApplicationEvent;

import java.util.UUID;

/**
 * Domain event published when an inventory operation is successfully validated.
 * Demonstrates OOP Observer / Event-Driven Architecture.
 */
public class OperationValidatedEvent extends ApplicationEvent {

    private final UUID operationId;

    public OperationValidatedEvent(Object source, UUID operationId) {
        super(source);
        this.operationId = operationId;
    }

    public UUID getOperationId() {
        return operationId;
    }
}

