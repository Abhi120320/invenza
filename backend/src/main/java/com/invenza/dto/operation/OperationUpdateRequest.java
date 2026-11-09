package com.invenza.dto.operation;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class OperationUpdateRequest {

    @JsonProperty("from_location_id")
    private UUID fromLocationId;

    @JsonProperty("to_location_id")
    private UUID toLocationId;

    private String contact;

    @JsonProperty("schedule_date")
    private LocalDate scheduleDate;

    private String notes;

    @Valid
    private List<OperationLineRequest> lines;

    public OperationUpdateRequest() {
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

