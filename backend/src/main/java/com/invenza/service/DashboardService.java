package com.invenza.service;

import com.invenza.dto.dashboard.DashboardStatsResponse;
import com.invenza.entity.OperationStatus;

import java.util.UUID;

public interface DashboardService {

    DashboardStatsResponse getDashboardStats(UUID warehouseId, UUID categoryId, OperationStatus status, String documentType);

    default DashboardStatsResponse getDashboardStats() {
        return getDashboardStats(null, null, null, null);
    }
}
