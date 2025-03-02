package com.example.imdbdataset.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ErrorResponse {
    private int statusCode;
    private String message;
    private String path;
    private LocalDateTime timestamp;

    public ErrorResponse(int statusCode, String message, String path, LocalDateTime timestamp) {
        this.statusCode = statusCode;
        this.message = message;
        this.path = path;
        this.timestamp = timestamp;
    }

}