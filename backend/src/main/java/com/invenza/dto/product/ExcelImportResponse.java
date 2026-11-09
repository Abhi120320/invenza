package com.invenza.dto.product;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class ExcelImportResponse {

    @JsonProperty("valid_count")
    private int validCount;

    @JsonProperty("warning_count")
    private int warningCount;

    @JsonProperty("error_count")
    private int errorCount;

    private List<Map<String, Object>> rows;

    public ExcelImportResponse() {
    }

    public ExcelImportResponse(int validCount, int warningCount, int errorCount, List<Map<String, Object>> rows) {
        this.validCount = validCount;
        this.warningCount = warningCount;
        this.errorCount = errorCount;
        this.rows = rows;
    }

    public int getValidCount() {
        return validCount;
    }

    public void setValidCount(int validCount) {
        this.validCount = validCount;
    }

    public int getWarningCount() {
        return warningCount;
    }

    public void setWarningCount(int warningCount) {
        this.warningCount = warningCount;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }

    public List<Map<String, Object>> getRows() {
        return rows;
    }

    public void setRows(List<Map<String, Object>> rows) {
        this.rows = rows;
    }
}

