package com.invenza.controller;

import com.invenza.dto.stock.StockResponse;
import com.invenza.dto.stock.StockUpdateRequest;
import com.invenza.service.StockService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/stock")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping
    public ResponseEntity<List<StockResponse>> listStock(
            @RequestParam(name = "location_id", required = false) UUID locationId,
            @RequestParam(name = "product_id", required = false) UUID productId,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(stockService.listStock(locationId, productId, search));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateStock(@PathVariable UUID id,
                                                          @RequestBody StockUpdateRequest request) {
        return ResponseEntity.ok(stockService.updateStock(id, request));
    }
}

