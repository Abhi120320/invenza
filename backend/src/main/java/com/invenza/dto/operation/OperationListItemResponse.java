package com.invenza.dto.operation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.invenza.entity.Operation;
import com.invenza.entity.OperationStatus;
import com.invenza.entity.OperationType;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class OperationListItemResponse {

    private UUID id;
    private String reference;
    private OperationType type;
    private String contact;

    @JsonProperty("schedule_date")
    private LocalDate scheduleDate;

    private OperationStatus status;

    @JsonProperty("created_at")
    private Instant createdAt;

    @JsonProperty("from_location")
    private Map<String, Object> fromLocation;

    @JsonProperty("to_location")
    private Map<String, Object> toLocation;

    private List<OperationLineResponse> lines = new ArrayList<>();

    public OperationListItemResponse() {
    }

    public static OperationListItemResponse fromEntity(Operation op) {
        if (op == null) return null;
        OperationListItemResponse item = new OperationListItemResponse();
        item.setId(op.getId());
        item.setReference(op.getReference());
        item.setType(op.getType());
        item.setContact(op.getContact());
        item.setScheduleDate(op.getScheduleDate());
        item.setStatus(op.getStatus());
        item.setCreatedAt(op.getCreatedAt());

        if (op.getFromLocation() != null) {
            item.setFromLocation(Map.of(
                    "id", op.getFromLocation().getId().toString(),
                    "name", op.getFromLocation().getName()
            ));
        }

        if (op.getToLocation() != null) {
            item.setToLocation(Map.of(
                    "id", op.getToLocation().getId().toString(),
                    "name", op.getToLocation().getName()
            ));
        }

        if (op.getLines() != null) {
            item.setLines(op.getLines().stream()
                    .map(OperationLineResponse::fromEntity)
                    .collect(Collectors.toList()));
        }

        return item;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public OperationType getType() {
        return type;
    }

    public void setType(OperationType type) {
        this.type = type;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public LocalDate getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(LocalDate scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public OperationStatus getStatus() {
        return status;
    }

    public void setStatus(OperationStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Map<String, Object> getFromLocation() {
        return fromLocation;
    }

    public void setFromLocation(Map<String, Object> fromLocation) {
        this.fromLocation = fromLocation;
    }

    public Map<String, Object> getToLocation() {
        return toLocation;
    }

    public void setToLocation(Map<String, Object> toLocation) {
        this.toLocation = toLocation;
    }

    public List<OperationLineResponse> getLines() {
        return lines;
    }

    public void setLines(List<OperationLineResponse> lines) {
        this.lines = lines;
    }
}

