package com.invenza.controller;

import com.invenza.dto.operation.*;
import com.invenza.entity.OperationStatus;
import com.invenza.entity.OperationType;
import com.invenza.entity.User;
import com.invenza.service.OperationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/operations")
public class OperationController {

    private final OperationService operationService;

    public OperationController(OperationService operationService) {
        this.operationService = operationService;
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse<OperationListItemResponse>> listOperations(
            @RequestParam(required = false) OperationType type,
            @RequestParam(required = false) OperationStatus status,
            @RequestParam(required = false) String search,
            @RequestParam(name = "warehouse_id", required = false) UUID warehouseId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(name = "page_size", defaultValue = "20") int pageSize) {
        PaginatedResponse<OperationListItemResponse> result =
                operationService.listOperations(type, status, search, warehouseId, page, pageSize);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<OperationResponse> createOperation(
            @Valid @RequestBody OperationCreateRequest request,
            @AuthenticationPrincipal User currentUser) {
        OperationResponse created = operationService.createOperation(request, currentUser);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OperationResponse> getOperation(@PathVariable UUID id) {
        return ResponseEntity.ok(operationService.getOperation(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OperationResponse> updateOperation(
            @PathVariable UUID id,
            @Valid @RequestBody OperationUpdateRequest request) {
        return ResponseEntity.ok(operationService.updateOperation(id, request));
    }

    @PostMapping("/{id}/validate")
    public ResponseEntity<OperationResponse> validateOperation(@PathVariable UUID id) {
        return ResponseEntity.ok(operationService.validateOperation(id));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<OperationResponse> cancelOperation(@PathVariable UUID id) {
        return ResponseEntity.ok(operationService.cancelOperation(id));
    }
}

