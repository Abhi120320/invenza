package com.invenza.dto.dashboard;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DashboardStatsResponse {

    private OperationStatsResponse receipts;
    private OperationStatsResponse deliveries;
    private OperationStatsResponse adjustments;
    private OperationStatsResponse transfers;

    @JsonProperty("total_products_in_stock")
    private long totalProductsInStock;

    @JsonProperty("low_stock_items")
    private long lowStockItems;

    @JsonProperty("total_quantity_on_hand")
    private long totalQuantityOnHand;


    // Flat compatibility fields
    @JsonProperty("total_products")
    private long totalProducts;

    @JsonProperty("low_stock_count")
    private long lowStockCount;

    @JsonProperty("out_of_stock_count")
    private long outOfStockCount;

    @JsonProperty("pending_receipts")
    private long pendingReceipts;

    @JsonProperty("pending_deliveries")
    private long pendingDeliveries;

    @JsonProperty("scheduled_transfers")
    private long scheduledTransfers;

    @JsonProperty("late_receipts")
    private long lateReceipts;

    @JsonProperty("late_deliveries")
    private long lateDeliveries;

    public DashboardStatsResponse() {
    }

    public OperationStatsResponse getReceipts() {
        return receipts;
    }

    public void setReceipts(OperationStatsResponse receipts) {
        this.receipts = receipts;
    }

    public OperationStatsResponse getDeliveries() {
        return deliveries;
    }

    public void setDeliveries(OperationStatsResponse deliveries) {
        this.deliveries = deliveries;
    }

    public OperationStatsResponse getAdjustments() {
        return adjustments;
    }

    public void setAdjustments(OperationStatsResponse adjustments) {
        this.adjustments = adjustments;
    }

    public OperationStatsResponse getTransfers() {
        return transfers;
    }

    public void setTransfers(OperationStatsResponse transfers) {
        this.transfers = transfers;
    }

    public long getTotalProductsInStock() {
        return totalProductsInStock;
    }

    public void setTotalProductsInStock(long totalProductsInStock) {
        this.totalProductsInStock = totalProductsInStock;
    }

    public long getLowStockItems() {
        return lowStockItems;
    }

    public void setLowStockItems(long lowStockItems) {
        this.lowStockItems = lowStockItems;
    }

    public long getTotalQuantityOnHand() {
        return totalQuantityOnHand;
    }

    public void setTotalQuantityOnHand(long totalQuantityOnHand) {
        this.totalQuantityOnHand = totalQuantityOnHand;
    }


    public long getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(long totalProducts) {
        this.totalProducts = totalProducts;
    }

    public long getLowStockCount() {
        return lowStockCount;
    }

    public void setLowStockCount(long lowStockCount) {
        this.lowStockCount = lowStockCount;
    }

    public long getOutOfStockCount() {
        return outOfStockCount;
    }

    public void setOutOfStockCount(long outOfStockCount) {
        this.outOfStockCount = outOfStockCount;
    }

    public long getPendingReceipts() {
        return pendingReceipts;
    }

    public void setPendingReceipts(long pendingReceipts) {
        this.pendingReceipts = pendingReceipts;
    }

    public long getPendingDeliveries() {
        return pendingDeliveries;
    }

    public void setPendingDeliveries(long pendingDeliveries) {
        this.pendingDeliveries = pendingDeliveries;
    }

    public long getScheduledTransfers() {
        return scheduledTransfers;
    }

    public void setScheduledTransfers(long scheduledTransfers) {
        this.scheduledTransfers = scheduledTransfers;
    }

    public long getLateReceipts() {
        return lateReceipts;
    }

    public void setLateReceipts(long lateReceipts) {
        this.lateReceipts = lateReceipts;
    }

    public long getLateDeliveries() {
        return lateDeliveries;
    }

    public void setLateDeliveries(long lateDeliveries) {
        this.lateDeliveries = lateDeliveries;
    }
}

