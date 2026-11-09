package com.invenza;

import com.invenza.strategy.operation.OperationHandler;
import com.invenza.strategy.operation.OperationHandlerFactory;
import com.invenza.entity.OperationType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class InvenzaApplicationTests {

    @Autowired
    private OperationHandlerFactory operationHandlerFactory;

    @Test
    void contextLoads() {
        assertNotNull(operationHandlerFactory);
    }

    @Test
    void testOperationHandlersRegistered() {
        for (OperationType type : OperationType.values()) {
            OperationHandler handler = operationHandlerFactory.getHandler(type);
            assertNotNull(handler, "Handler should exist for " + type);
        }
    }
}

