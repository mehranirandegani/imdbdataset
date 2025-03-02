package com.example.imdbdataset.dto;

import lombok.Data;
import java.util.List;

@Data
public class PagedResponse<T> {
    private List<T> items;
    private int currentPage;
    private long totalItems;
    private int totalPages;

    public static <T> PagedResponse<T> of(List<T> items, int page, int size, long total) {
        PagedResponse<T> response = new PagedResponse<>();
        response.setItems(items);
        response.setCurrentPage(page);
        response.setTotalItems(total);
        response.setTotalPages((int) Math.ceil((double) total / size));
        return response;
    }
}