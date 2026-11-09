package com.invenza.service;

import com.invenza.dto.stock.StockResponse;
import com.invenza.dto.stock.StockUpdateRequest;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface StockService {
    List<StockResponse> listStock(UUID locationId, UUID productId, String search);
    Map<String, Object> updateStock(UUID stockId, StockUpdateRequest request);
}

