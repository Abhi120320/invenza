package com.invenza.service;

import com.invenza.dto.history.MoveHistoryResponse;
import com.invenza.dto.operation.PaginatedResponse;

public interface MoveHistoryService {
    PaginatedResponse<MoveHistoryResponse> listMoveHistory(String search, int page, int pageSize);
}

