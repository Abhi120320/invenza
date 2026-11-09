package com.invenza.event;

import com.invenza.service.WebSocketService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Event listener handling domain events asynchronously.
 * Demonstrates OOP Observer Pattern.
 */
@Component
public class OperationEventListener {

    private final WebSocketService webSocketService;

    public OperationEventListener(WebSocketService webSocketService) {
        this.webSocketService = webSocketService;
    }

    @EventListener
    public void onOperationValidated(OperationValidatedEvent event) {
        webSocketService.broadcastOperationValidated(event.getOperationId());
        webSocketService.broadcastInventoryChanged();
    }
}

