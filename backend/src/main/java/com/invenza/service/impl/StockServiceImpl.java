package com.invenza.service.impl;

import com.invenza.dto.stock.StockResponse;
import com.invenza.dto.stock.StockUpdateRequest;
import com.invenza.entity.Stock;
import com.invenza.exception.ResourceNotFoundException;
import com.invenza.repository.StockRepository;
import com.invenza.service.StockService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;

    public StockServiceImpl(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockResponse> listStock(UUID locationId, UUID productId, String search) {
        String query = (search != null && !search.isBlank()) ? search.trim() : null;
        return stockRepository.searchStock(locationId, productId, query).stream()
                .map(StockResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Map<String, Object> updateStock(UUID stockId, StockUpdateRequest request) {
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock not found"));

        if (request.getOnHand() != null) {
            stock.setOnHand(request.getOnHand());
        }
        if (request.getPerUnitCost() != null) {
            stock.setPerUnitCost(request.getPerUnitCost());
        }

        Stock updated = stockRepository.save(stock);

        return Map.of(
                "status", "success",
                "on_hand", updated.getOnHand(),
                "per_unit_cost", updated.getPerUnitCost()
        );
    }
}

