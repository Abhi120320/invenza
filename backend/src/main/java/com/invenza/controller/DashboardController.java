package com.invenza.controller;

import com.invenza.dto.dashboard.DashboardStatsResponse;
import com.invenza.entity.OperationStatus;
import com.invenza.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/kpis")
    public ResponseEntity<DashboardStatsResponse> getDashboardKPIs(
            @RequestParam(name = "warehouse_id", required = false) UUID warehouseId,
            @RequestParam(name = "category_id", required = false) UUID categoryId,
            @RequestParam(required = false) OperationStatus status,
            @RequestParam(name = "document_type", required = false) String documentType) {
        return ResponseEntity.ok(dashboardService.getDashboardStats(warehouseId, categoryId, status, documentType));
    }
}
