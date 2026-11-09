package com.invenza.service;

import org.springframework.web.socket.WebSocketSession;
import java.util.UUID;

public interface WebSocketService {
    void addSession(WebSocketSession session);
    void removeSession(WebSocketSession session);
    void broadcast(Object payload);
    void broadcastOperationValidated(UUID operationId);
    void broadcastInventoryChanged();
}

