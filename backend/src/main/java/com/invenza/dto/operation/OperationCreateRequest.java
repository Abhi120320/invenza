package com.invenza.dto.operation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.invenza.entity.OperationType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class OperationCreateRequest {

    @NotNull(message = "Operation type is required")
    private OperationType type;

    @NotNull(message = "Warehouse ID is required")
    @JsonProperty("warehouse_id")
    private UUID warehouseId;

    @JsonProperty("from_location_id")
    private UUID fromLocationId;

    @JsonProperty("to_location_id")
    private UUID toLocationId;

    private String contact;

    @JsonProperty("schedule_date")
    private LocalDate scheduleDate;

    private String notes;

    @NotEmpty(message = "Operation must have at least one line item")
    @Valid
    private List<OperationLineRequest> lines;

    public OperationCreateRequest() {
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<OperationLineRequest> getLines() {
        return lines;
    }

    public void setLines(List<OperationLineRequest> lines) {
        this.lines = lines;
    }
}

