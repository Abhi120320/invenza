package com.invenza.controller;

import com.invenza.dto.history.MoveHistoryResponse;
import com.invenza.dto.operation.PaginatedResponse;
import com.invenza.service.MoveHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/move-history")
public class MoveHistoryController {

    private final MoveHistoryService moveHistoryService;

    public MoveHistoryController(MoveHistoryService moveHistoryService) {
        this.moveHistoryService = moveHistoryService;
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse<MoveHistoryResponse>> getMoveHistory(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(name = "page_size", defaultValue = "20") int pageSize) {
        return ResponseEntity.ok(moveHistoryService.listMoveHistory(search, page, pageSize));
    }
}

