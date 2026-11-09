package com.invenza;

import com.invenza.entity.*;
import com.invenza.exception.InsufficientStockException;
import com.invenza.repository.*;
import com.invenza.strategy.operation.OperationHandlerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class OperationStrategyTest {

    @Autowired
    private OperationHandlerFactory operationHandlerFactory;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private MoveHistoryRepository moveHistoryRepository;

    @Autowired
    private OperationRepository operationRepository;

    private Warehouse warehouse;
    private Location locIn;
    private Location locOut;
    private Product product;

    @BeforeEach
    void setUp() {
        warehouse = warehouseRepository.save(new Warehouse("Test WH", "TWH", "Test Address"));
        locIn = locationRepository.save(new Location("Receiving", "TWH-REC", warehouse));
        locOut = locationRepository.save(new Location("Shipping", "TWH-SHP", warehouse));
        product = productRepository.save(new Product("Widget", "WGT-001", null, "pcs", new BigDecimal("10"), null));
    }

    @Test
    void testReceiptStrategyIncreasesStock() {
        Operation op = new Operation();
        op.setReference("TWH/IN/00001");
        op.setType(OperationType.IN);
        op.setWarehouse(warehouse);
        op.setToLocation(locIn);

        OperationLine line = new OperationLine(op, product, new BigDecimal("50"));
        op.addLine(line);
        op = operationRepository.save(op);

        operationHandlerFactory.getHandler(OperationType.IN).process(op);

        Stock stock = stockRepository.findByProductIdAndLocationId(product.getId(), locIn.getId()).orElseThrow();
        assertEquals(0, new BigDecimal("50").compareTo(stock.getOnHand()));
        assertEquals(0, new BigDecimal("50").compareTo(line.getDoneQuantity()));
        assertTrue(moveHistoryRepository.count() > 0);
    }

    @Test
    void testDeliveryStrategyDeductsStock() {
        Stock stock = new Stock(product, locOut, new BigDecimal("100"), BigDecimal.TEN);
        stockRepository.save(stock);

        Operation op = new Operation();
        op.setReference("TWH/OUT/00001");
        op.setType(OperationType.OUT);
        op.setWarehouse(warehouse);
        op.setFromLocation(locOut);

        OperationLine line = new OperationLine(op, product, new BigDecimal("30"));
        op.addLine(line);
        op = operationRepository.save(op);

        operationHandlerFactory.getHandler(OperationType.OUT).process(op);

        Stock updatedStock = stockRepository.findByProductIdAndLocationId(product.getId(), locOut.getId()).orElseThrow();
        assertEquals(0, new BigDecimal("70").compareTo(updatedStock.getOnHand()));
    }

    @Test
    void testDeliveryStrategyFailsOnInsufficientStock() {
        Stock stock = new Stock(product, locOut, new BigDecimal("10"), BigDecimal.TEN);
        stockRepository.save(stock);

        Operation op = new Operation();
        op.setReference("TWH/OUT/00002");
        op.setType(OperationType.OUT);
        op.setWarehouse(warehouse);
        op.setFromLocation(locOut);

        OperationLine line = new OperationLine(op, product, new BigDecimal("25"));
        op.addLine(line);
        Operation savedOp = operationRepository.save(op);

        assertThrows(InsufficientStockException.class, () -> {
            operationHandlerFactory.getHandler(OperationType.OUT).process(savedOp);
        });
    }

    @Test
    void testTransferStrategyMovesStock() {
        Stock srcStock = new Stock(product, locIn, new BigDecimal("50"), BigDecimal.TEN);
        stockRepository.save(srcStock);

        Operation op = new Operation();
        op.setReference("TWH/TRF/00001");
        op.setType(OperationType.TRANSFER);
        op.setWarehouse(warehouse);
        op.setFromLocation(locIn);
        op.setToLocation(locOut);

        OperationLine line = new OperationLine(op, product, new BigDecimal("20"));
        op.addLine(line);
        op = operationRepository.save(op);

        operationHandlerFactory.getHandler(OperationType.TRANSFER).process(op);

        Stock updatedSrc = stockRepository.findByProductIdAndLocationId(product.getId(), locIn.getId()).orElseThrow();
        Stock updatedDst = stockRepository.findByProductIdAndLocationId(product.getId(), locOut.getId()).orElseThrow();

        assertEquals(0, new BigDecimal("30").compareTo(updatedSrc.getOnHand()));
        assertEquals(0, new BigDecimal("20").compareTo(updatedDst.getOnHand()));
    }

    @Test
    void testAdjustmentStrategyReconcilesStock() {
        Stock stock = new Stock(product, locIn, new BigDecimal("15"), BigDecimal.TEN);
        stockRepository.save(stock);

        Operation op = new Operation();
        op.setReference("TWH/ADJ/00001");
        op.setType(OperationType.ADJUSTMENT);
        op.setWarehouse(warehouse);
        op.setToLocation(locIn);

        OperationLine line = new OperationLine(op, product, new BigDecimal("12"));
        op.addLine(line);
        op = operationRepository.save(op);

        operationHandlerFactory.getHandler(OperationType.ADJUSTMENT).process(op);

        Stock updated = stockRepository.findByProductIdAndLocationId(product.getId(), locIn.getId()).orElseThrow();
        assertEquals(0, new BigDecimal("12").compareTo(updated.getOnHand()));
    }
}
