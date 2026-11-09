package com.invenza.service.impl;

import com.invenza.dto.operation.*;
import com.invenza.entity.*;
import com.invenza.event.OperationValidatedEvent;
import com.invenza.exception.BadRequestException;
import com.invenza.exception.ConflictException;
import com.invenza.exception.ResourceNotFoundException;
import com.invenza.repository.*;
import com.invenza.service.OperationService;
import com.invenza.strategy.operation.OperationHandler;
import com.invenza.strategy.operation.OperationHandlerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OperationServiceImpl implements OperationService {

    private final OperationRepository operationRepository;
    private final OperationLineRepository operationLineRepository;
    private final WarehouseRepository warehouseRepository;
    private final LocationRepository locationRepository;
    private final ProductRepository productRepository;
    private final OperationHandlerFactory operationHandlerFactory;
    private final ApplicationEventPublisher eventPublisher;

    public OperationServiceImpl(OperationRepository operationRepository,
                                OperationLineRepository operationLineRepository,
                                WarehouseRepository warehouseRepository,
                                LocationRepository locationRepository,
                                ProductRepository productRepository,
                                OperationHandlerFactory operationHandlerFactory,
                                ApplicationEventPublisher eventPublisher) {
        this.operationRepository = operationRepository;
        this.operationLineRepository = operationLineRepository;
        this.warehouseRepository = warehouseRepository;
        this.locationRepository = locationRepository;
        this.productRepository = productRepository;
        this.operationHandlerFactory = operationHandlerFactory;
        this.eventPublisher = eventPublisher;
    }

    private String buildReference(String warehouseShortCode, OperationType type, int sequence) {
        String typeCode = switch (type) {
            case IN -> "IN";
            case OUT -> "OUT";
            case TRANSFER -> "TRF";
            case ADJUSTMENT -> "ADJ";
        };
        return String.format("%s/%s/%05d", warehouseShortCode, typeCode, sequence);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<OperationListItemResponse> listOperations(
            OperationType type,
            OperationStatus status,
            String search,
            UUID warehouseId,
            int page,
            int pageSize) {

        int pageIndex = Math.max(0, page - 1);
        int validPageSize = Math.min(100, Math.max(1, pageSize));

        Pageable pageable = PageRequest.of(pageIndex, validPageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        String query = (search != null && !search.isBlank()) ? search.trim() : null;

        Page<Operation> operationPage = operationRepository.searchOperations(type, status, warehouseId, query, pageable);

        List<OperationListItemResponse> items = operationPage.getContent().stream()
                .map(OperationListItemResponse::fromEntity)
                .collect(Collectors.toList());

        return new PaginatedResponse<>(items, operationPage.getTotalElements(), page, validPageSize);
    }

    @Override
    @Transactional
    public OperationResponse createOperation(OperationCreateRequest request, User currentUser) {
        if (request.getScheduleDate() != null && request.getScheduleDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Schedule date cannot be in the past");
        }

        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found"));

        int sequence = (int) operationRepository.countByWarehouseIdAndType(warehouse.getId(), request.getType()) + 1;
        String reference = buildReference(warehouse.getShortCode(), request.getType(), sequence);

        Operation operation = new Operation();
        operation.setReference(reference);
        operation.setType(request.getType());
        operation.setWarehouse(warehouse);
        operation.setSequenceNumber(sequence);
        operation.setContact(request.getContact());
        operation.setScheduleDate(request.getScheduleDate());
        operation.setNotes(request.getNotes());
        operation.setResponsibleUser(currentUser);
        operation.setStatus(OperationStatus.DRAFT);

        if (request.getFromLocationId() != null) {
            Location fromLoc = locationRepository.findById(request.getFromLocationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Source location not found"));
            operation.setFromLocation(fromLoc);
        }

        if (request.getToLocationId() != null) {
            Location toLoc = locationRepository.findById(request.getToLocationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Destination location not found"));
            operation.setToLocation(toLoc);
        }

        for (OperationLineRequest lineReq : request.getLines()) {
            Product product = productRepository.findById(lineReq.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + lineReq.getProductId()));
            OperationLine line = new OperationLine(operation, product, lineReq.getQuantity());
            operation.addLine(line);
        }

        // Calculate initial status via Strategy Pattern
        OperationHandler handler = operationHandlerFactory.getHandler(request.getType());
        OperationStatus initialStatus = handler.calculateInitialStatus(operation);
        operation.setStatus(initialStatus);

        Operation saved = operationRepository.save(operation);
        eventPublisher.publishEvent(new OperationValidatedEvent(this, saved.getId()));
        return getOperation(saved.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public OperationResponse getOperation(UUID id) {
        Operation operation = operationRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Operation not found"));
        return OperationResponse.fromEntity(operation);
    }

    @Override
    @Transactional
    public OperationResponse updateOperation(UUID id, OperationUpdateRequest request) {
        Operation operation = operationRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Operation not found"));

        if (!operation.isModifiable()) {
            throw new ConflictException("Cannot modify a completed or canceled operation");
        }

        if (request.getScheduleDate() != null
                && !request.getScheduleDate().equals(operation.getScheduleDate())
                && request.getScheduleDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Schedule date cannot be in the past");
        }

        if (request.getFromLocationId() != null) {
            Location fromLoc = locationRepository.findById(request.getFromLocationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Source location not found"));
            operation.setFromLocation(fromLoc);
        }

        if (request.getToLocationId() != null) {
            Location toLoc = locationRepository.findById(request.getToLocationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Destination location not found"));
            operation.setToLocation(toLoc);
        }

        if (request.getContact() != null) operation.setContact(request.getContact());
        if (request.getScheduleDate() != null) operation.setScheduleDate(request.getScheduleDate());
        if (request.getNotes() != null) operation.setNotes(request.getNotes());

        if (request.getLines() != null) {
            operation.clearLines();
            for (OperationLineRequest lineReq : request.getLines()) {
                Product product = productRepository.findById(lineReq.getProductId())
                        .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + lineReq.getProductId()));
                OperationLine line = new OperationLine(operation, product, lineReq.getQuantity());
                operation.addLine(line);
            }
        }

        // Recalculate status for OUT operations
        if (operation.getType() == OperationType.OUT) {
            OperationHandler handler = operationHandlerFactory.getHandler(OperationType.OUT);
            operation.setStatus(handler.calculateInitialStatus(operation));
        }

        Operation updated = operationRepository.save(operation);
        eventPublisher.publishEvent(new OperationValidatedEvent(this, updated.getId()));
        return getOperation(updated.getId());
    }

    @Override
    @Transactional
    public OperationResponse validateOperation(UUID id) {
        Operation operation = operationRepository.findWithLockById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Operation not found"));

        operation.markDone(); // Validates not already done or canceled

        // Execute stock mutation strategy polymorphically
        OperationHandler handler = operationHandlerFactory.getHandler(operation.getType());
        handler.process(operation);

        operationRepository.save(operation);

        // Notify observers (WebSocket broadcast, etc.)
        eventPublisher.publishEvent(new OperationValidatedEvent(this, operation.getId()));

        return getOperation(operation.getId());
    }

    @Override
    @Transactional
    public OperationResponse cancelOperation(UUID id) {
        Operation operation = operationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Operation not found"));

        operation.markCanceled();
        Operation saved = operationRepository.save(operation);
        eventPublisher.publishEvent(new OperationValidatedEvent(this, saved.getId()));
        return getOperation(saved.getId());
    }
}

