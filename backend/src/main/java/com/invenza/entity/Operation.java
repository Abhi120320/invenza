package com.invenza.entity;

import com.invenza.exception.ConflictException;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Domain entity representing an inventory operation (Receipt, Delivery, Transfer, Adjustment).
 * Demonstrates OOP State Pattern concepts and Lifecycle Encapsulation.
 */
@Entity
@Table(name = "operations", indexes = {
    @Index(name = "ix_operations_reference", columnList = "reference", unique = true),
    @Index(name = "ix_operations_status", columnList = "status"),
    @Index(name = "ix_operations_type", columnList = "type")
})
public class Operation extends BaseEntity {

    @Column(name = "reference", length = 100, nullable = false, unique = true)
    private String reference;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private OperationType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_location_id")
    private Location fromLocation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_location_id")
    private Location toLocation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    @Column(name = "contact", length = 255)
    private String contact;

    @Column(name = "schedule_date")
    private LocalDate scheduleDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsible_user_id")
    private User responsibleUser;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OperationStatus status = OperationStatus.DRAFT;

    @Column(name = "sequence_number", nullable = false)
    private int sequenceNumber = 0;

    @Column(name = "notes", length = 1000)
    private String notes;

    @OneToMany(mappedBy = "operation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OperationLine> lines = new ArrayList<>();

    public Operation() {
    }

    public Operation(UUID id) {
        super(id);
    }

    public void addLine(OperationLine line) {
        if (line != null) {
            lines.add(line);
            line.setOperation(this);
        }
    }

    public void clearLines() {
        lines.clear();
    }

    public void markDone() {
        if (this.status == OperationStatus.DONE) {
            throw new ConflictException("Operation already validated");
        }
        if (this.status == OperationStatus.CANCELED) {
            throw new ConflictException("Cannot validate a canceled operation");
        }
        this.status = OperationStatus.DONE;
    }

    public void markCanceled() {
        if (this.status == OperationStatus.DONE) {
            throw new ConflictException("Cannot cancel a completed operation");
        }
        this.status = OperationStatus.CANCELED;
    }

    public boolean isModifiable() {
        return this.status != OperationStatus.DONE && this.status != OperationStatus.CANCELED;
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

    public Location getFromLocation() {
        return fromLocation;
    }

    public void setFromLocation(Location fromLocation) {
        this.fromLocation = fromLocation;
    }

    public Location getToLocation() {
        return toLocation;
    }

    public void setToLocation(Location toLocation) {
        this.toLocation = toLocation;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
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

    public User getResponsibleUser() {
        return responsibleUser;
    }

    public void setResponsibleUser(User responsibleUser) {
        this.responsibleUser = responsibleUser;
    }

    public OperationStatus getStatus() {
        return status;
    }

    public void setStatus(OperationStatus status) {
        this.status = status;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<OperationLine> getLines() {
        return lines;
    }

    public void setLines(List<OperationLine> lines) {
        this.lines = lines;
    }
}

