package com.invenza.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.invenza.service.WebSocketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WebSocketServiceImpl implements WebSocketService {

    private static final Logger log = LoggerFactory.getLogger(WebSocketServiceImpl.class);

    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();
    private final ObjectMapper objectMapper;

    public WebSocketServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void addSession(WebSocketSession session) {
        sessions.add(session);
        log.info("WebSocket client connected: {}", session.getId());
    }

    @Override
    public void removeSession(WebSocketSession session) {
        sessions.remove(session);
        log.info("WebSocket client disconnected: {}", session.getId());
    }

    @Override
    public void broadcast(Object payload) {
        try {
            String json = objectMapper.writeValueAsString(payload);
            TextMessage message = new TextMessage(json);
            for (WebSocketSession session : sessions) {
                if (session.isOpen()) {
                    try {
                        session.sendMessage(message);
                    } catch (IOException e) {
                        log.warn("Failed to send WebSocket message to {}: {}", session.getId(), e.getMessage());
                        sessions.remove(session);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error serializing WebSocket broadcast payload: {}", e.getMessage());
        }
    }

    @Override
    public void broadcastOperationValidated(UUID operationId) {
        broadcast(Map.of(
                "event", "operation_validated",
                "operation_id", operationId.toString()
        ));
    }

    @Override
    public void broadcastInventoryChanged() {
        broadcast(Map.of(
                "event", "inventory_updated",
                "timestamp", System.currentTimeMillis()
        ));
    }
}

