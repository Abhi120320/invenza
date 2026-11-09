package com.invenza.dto.operation;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class PaginatedResponse<T> {

    private List<T> items;
    private long total;
    private int page;

    @JsonProperty("page_size")
    private int pageSize;

    public PaginatedResponse() {
    }

    public PaginatedResponse(List<T> items, long total, int page, int pageSize) {
        this.items = items;
        this.total = total;
        this.page = page;
        this.pageSize = pageSize;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}

