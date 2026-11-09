package com.invenza;

import com.invenza.dto.dashboard.DashboardStatsResponse;
import com.invenza.entity.OperationStatus;
import com.invenza.entity.OperationType;
import com.invenza.repository.OperationRepository;
import com.invenza.repository.ProductRepository;
import com.invenza.repository.StockRepository;
import com.invenza.service.DashboardService;
import com.invenza.service.impl.DashboardServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DashboardServiceTest {

    @Mock
    private OperationRepository operationRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private StockRepository stockRepository;

    private DashboardService dashboardService;

    @BeforeEach
    void setUp() {
        dashboardService = new DashboardServiceImpl(operationRepository, productRepository, stockRepository);
    }

    @Test
    @DisplayName("Should return accurate dashboard KPI metrics with breakdown")
    void testGetDashboardStats() {
        UUID warehouseId = UUID.randomUUID();

        lenient().when(operationRepository.countByTypeAndWarehouseAndStatus(eq(OperationType.IN), eq(warehouseId), eq(OperationStatus.DONE)))
                .thenReturn(5L);
        lenient().when(operationRepository.countByTypeAndWarehouseAndStatus(eq(OperationType.IN), eq(warehouseId), eq(OperationStatus.READY)))
                .thenReturn(2L);
        lenient().when(operationRepository.countByTypeAndWarehouse(eq(OperationType.IN), eq(warehouseId)))
                .thenReturn(7L);

        lenient().when(stockRepository.countDistinctProductsInStockWithFilters(eq(warehouseId), any()))
                .thenReturn(10L);
        lenient().when(productRepository.countProductsByCategory(any()))
                .thenReturn(15L);
        lenient().when(productRepository.countLowStockProductsByCategory(any()))
                .thenReturn(3L);
        lenient().when(stockRepository.sumQuantityOnHandWithFilters(eq(warehouseId), any()))
                .thenReturn(BigDecimal.valueOf(250));

        DashboardStatsResponse stats = dashboardService.getDashboardStats(warehouseId, null, null, null);

        assertNotNull(stats);
        assertEquals(15L, stats.getTotalProducts());
        assertEquals(10L, stats.getTotalProductsInStock());
        assertEquals(5L, stats.getOutOfStockCount());
        assertEquals(3L, stats.getLowStockItems());
        assertEquals(250L, stats.getTotalQuantityOnHand());

        assertNotNull(stats.getReceipts());
        assertEquals(5L, stats.getReceipts().getDone());
        assertEquals(2L, stats.getReceipts().getReady());
        assertEquals(7L, stats.getReceipts().getTotalOperations());
    }
}
