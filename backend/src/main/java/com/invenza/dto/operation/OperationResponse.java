package com.invenza.dto.operation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.invenza.entity.Operation;
import com.invenza.entity.OperationStatus;
import com.invenza.entity.OperationType;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class OperationResponse {

    private UUID id;
    private String reference;
    private OperationType type;

    @JsonProperty("warehouse_id")
    private UUID warehouseId;

    @JsonProperty("from_location_id")
    private UUID fromLocationId;

    @JsonProperty("to_location_id")
    private UUID toLocationId;

    private String contact;

    @JsonProperty("schedule_date")
    private LocalDate scheduleDate;

    @JsonProperty("responsible_user_id")
    private UUID responsibleUserId;

    private OperationStatus status;
    private String notes;

    @JsonProperty("created_at")
    private Instant createdAt;

    @JsonProperty("updated_at")
    private Instant updatedAt;

    private List<OperationLineResponse> lines = new ArrayList<>();

    public OperationResponse() {
    }

    public static OperationResponse fromEntity(Operation op) {
        if (op == null) return null;
        OperationResponse resp = new OperationResponse();
        resp.setId(op.getId());
        resp.setReference(op.getReference());
        resp.setType(op.getType());
        resp.setWarehouseId(op.getWarehouse() != null ? op.getWarehouse().getId() : null);
        resp.setFromLocationId(op.getFromLocation() != null ? op.getFromLocation().getId() : null);
        resp.setToLocationId(op.getToLocation() != null ? op.getToLocation().getId() : null);
        resp.setContact(op.getContact());
        resp.setScheduleDate(op.getScheduleDate());
        resp.setResponsibleUserId(op.getResponsibleUser() != null ? op.getResponsibleUser().getId() : null);
        resp.setStatus(op.getStatus());
        resp.setNotes(op.getNotes());
        resp.setCreatedAt(op.getCreatedAt());
        resp.setUpdatedAt(op.getUpdatedAt());

        if (op.getLines() != null) {
            resp.setLines(op.getLines().stream()
                    .map(OperationLineResponse::fromEntity)
                    .collect(Collectors.toList()));
        }

        return resp;
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

    public UUID getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(UUID warehouseId) {
        this.warehouseId = warehouseId;
    }

    public UUID getFromLocationId() {
        return fromLocationId;
    }

    public void setFromLocationId(UUID fromLocationId) {
        this.fromLocationId = fromLocationId;
    }

    public UUID getToLocationId() {
        return toLocationId;
    }

    public void setToLocationId(UUID toLocationId) {
        this.toLocationId = toLocationId;
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

    public UUID getResponsibleUserId() {
        return responsibleUserId;
    }

    public void setResponsibleUserId(UUID responsibleUserId) {
        this.responsibleUserId = responsibleUserId;
    }

    public OperationStatus getStatus() {
        return status;
    }

    public void setStatus(OperationStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<OperationLineResponse> getLines() {
        return lines;
    }

    public void setLines(List<OperationLineResponse> lines) {
        this.lines = lines;
    }
}

