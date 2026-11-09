package com.invenza.service.impl;

import com.invenza.dto.dashboard.DashboardStatsResponse;
import com.invenza.dto.dashboard.OperationStatsResponse;
import com.invenza.entity.OperationStatus;
import com.invenza.entity.OperationType;
import com.invenza.repository.OperationRepository;
import com.invenza.repository.ProductRepository;
import com.invenza.repository.StockRepository;
import com.invenza.service.DashboardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final OperationRepository operationRepository;
    private final ProductRepository productRepository;
    private final StockRepository stockRepository;

    public DashboardServiceImpl(OperationRepository operationRepository,
                                ProductRepository productRepository,
                                StockRepository stockRepository) {
        this.operationRepository = operationRepository;
        this.productRepository = productRepository;
        this.stockRepository = stockRepository;
    }

    private OperationStatsResponse computeStatsForType(OperationType type, LocalDate today, UUID warehouseId, OperationStatus statusFilter) {
        List<OperationStatus> openStatuses = List.of(
                OperationStatus.DRAFT,
                OperationStatus.WAITING,
                OperationStatus.READY
        );

        long draft = operationRepository.countByTypeAndWarehouseAndStatus(type, warehouseId, OperationStatus.DRAFT);
        long waiting = operationRepository.countByTypeAndWarehouseAndStatus(type, warehouseId, OperationStatus.WAITING);
        long ready = operationRepository.countByTypeAndWarehouseAndStatus(type, warehouseId, OperationStatus.READY);
        long done = operationRepository.countByTypeAndWarehouseAndStatus(type, warehouseId, OperationStatus.DONE);
        long canceled = operationRepository.countByTypeAndWarehouseAndStatus(type, warehouseId, OperationStatus.CANCELED);

        long toDo;
        if (statusFilter != null) {
            toDo = operationRepository.countByTypeAndWarehouseAndStatus(type, warehouseId, statusFilter);
        } else {
            toDo = draft + waiting + ready;
        }

        long late = operationRepository.countByTypeAndWarehouseAndStatusInAndScheduleDateBefore(type, warehouseId, openStatuses, today);
        long total = operationRepository.countByTypeAndWarehouse(type, warehouseId);

        return new OperationStatsResponse(toDo, late, waiting, draft, ready, done, canceled, total);
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardStatsResponse getDashboardStats(UUID warehouseId, UUID categoryId, OperationStatus status, String documentType) {
        LocalDate today = LocalDate.now();

        OperationStatsResponse receipts = computeStatsForType(OperationType.IN, today, warehouseId, status);
        OperationStatsResponse deliveries = computeStatsForType(OperationType.OUT, today, warehouseId, status);
        OperationStatsResponse adjustments = computeStatsForType(OperationType.ADJUSTMENT, today, warehouseId, status);
        OperationStatsResponse transfers = computeStatsForType(OperationType.TRANSFER, today, warehouseId, status);

        long totalProductsInStock = stockRepository.countDistinctProductsInStockWithFilters(warehouseId, categoryId);
        long lowStockItems = productRepository.countLowStockProductsByCategory(categoryId);
        long totalProducts = productRepository.countProductsByCategory(categoryId);
        long outOfStockCount = Math.max(0, totalProducts - totalProductsInStock);

        BigDecimal totalQtyOnHand = stockRepository.sumQuantityOnHandWithFilters(warehouseId, categoryId);

        DashboardStatsResponse resp = new DashboardStatsResponse();
        resp.setReceipts(receipts);
        resp.setDeliveries(deliveries);
        resp.setAdjustments(adjustments);
        resp.setTransfers(transfers);
        resp.setTotalProductsInStock(totalProductsInStock);
        resp.setLowStockItems(lowStockItems);
        resp.setTotalQuantityOnHand(totalQtyOnHand != null ? totalQtyOnHand.longValue() : 0);

        // Compatibility flat fields
        resp.setTotalProducts(totalProducts);
        resp.setLowStockCount(lowStockItems);
        resp.setOutOfStockCount(outOfStockCount);
        resp.setPendingReceipts(receipts.getToDo());
        resp.setPendingDeliveries(deliveries.getToDo());
        resp.setScheduledTransfers(transfers.getToDo());
        resp.setLateReceipts(receipts.getLate());
        resp.setLateDeliveries(deliveries.getLate());

        return resp;
    }
}
