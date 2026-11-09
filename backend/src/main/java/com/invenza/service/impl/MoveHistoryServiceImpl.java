package com.invenza.service.impl;

import com.invenza.dto.history.MoveHistoryResponse;
import com.invenza.dto.operation.PaginatedResponse;
import com.invenza.entity.MoveHistory;
import com.invenza.repository.MoveHistoryRepository;
import com.invenza.service.MoveHistoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MoveHistoryServiceImpl implements MoveHistoryService {

    private final MoveHistoryRepository moveHistoryRepository;

    public MoveHistoryServiceImpl(MoveHistoryRepository moveHistoryRepository) {
        this.moveHistoryRepository = moveHistoryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<MoveHistoryResponse> listMoveHistory(String search, int page, int pageSize) {
        int pageIndex = Math.max(0, page - 1);
        int validPageSize = Math.min(100, Math.max(1, pageSize));

        Pageable pageable = PageRequest.of(pageIndex, validPageSize, Sort.by(Sort.Direction.DESC, "date"));
        String query = (search != null && !search.isBlank()) ? search.trim() : null;

        Page<MoveHistory> historyPage = moveHistoryRepository.searchMoveHistory(query, pageable);

        List<MoveHistoryResponse> items = historyPage.getContent().stream()
                .map(MoveHistoryResponse::fromEntity)
                .collect(Collectors.toList());

        return new PaginatedResponse<>(items, historyPage.getTotalElements(), page, validPageSize);
    }
}

