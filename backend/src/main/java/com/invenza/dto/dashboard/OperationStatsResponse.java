package com.invenza.dto.dashboard;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OperationStatsResponse {

    @JsonProperty("to_do")
    private long toDo;

    private long late;
    private long waiting;
    private long draft;
    private long ready;
    private long done;
    private long canceled;

    @JsonProperty("total_operations")
    private long totalOperations;

    public OperationStatsResponse() {
    }

    public OperationStatsResponse(long toDo, long late, long waiting, long totalOperations) {
        this.toDo = toDo;
        this.late = late;
        this.waiting = waiting;
        this.totalOperations = totalOperations;
    }

    public OperationStatsResponse(long toDo, long late, long waiting, long draft, long ready, long done, long canceled, long totalOperations) {
        this.toDo = toDo;
        this.late = late;
        this.waiting = waiting;
        this.draft = draft;
        this.ready = ready;
        this.done = done;
        this.canceled = canceled;
        this.totalOperations = totalOperations;
    }

    public long getToDo() {
        return toDo;
    }

    public void setToDo(long toDo) {
        this.toDo = toDo;
    }

    public long getLate() {
        return late;
    }

    public void setLate(long late) {
        this.late = late;
    }

    public long getWaiting() {
        return waiting;
    }

    public void setWaiting(long waiting) {
        this.waiting = waiting;
    }

    public long getDraft() {
        return draft;
    }

    public void setDraft(long draft) {
        this.draft = draft;
    }

    public long getReady() {
        return ready;
    }

    public void setReady(long ready) {
        this.ready = ready;
    }

    public long getDone() {
        return done;
    }

    public void setDone(long done) {
        this.done = done;
    }

    public long getCanceled() {
        return canceled;
    }

    public void setCanceled(long canceled) {
        this.canceled = canceled;
    }

    public long getTotalOperations() {
        return totalOperations;
    }

    public void setTotalOperations(long totalOperations) {
        this.totalOperations = totalOperations;
    }
}
