package com.invenza.service;

import com.invenza.dto.operation.*;
import com.invenza.entity.OperationStatus;
import com.invenza.entity.OperationType;
import com.invenza.entity.User;

import java.util.UUID;

public interface OperationService {
    PaginatedResponse<OperationListItemResponse> listOperations(
            OperationType type,
            OperationStatus status,
            String search,
            UUID warehouseId,
            int page,
            int pageSize
    );

    OperationResponse createOperation(OperationCreateRequest request, User currentUser);
    OperationResponse getOperation(UUID id);
    OperationResponse updateOperation(UUID id, OperationUpdateRequest request);
    OperationResponse validateOperation(UUID id);
    OperationResponse cancelOperation(UUID id);
}

