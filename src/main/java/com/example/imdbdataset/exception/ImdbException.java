package com.example.imdbdataset.exception;

public class ImdbException extends RuntimeException {
    public ImdbException(String message) {
        super(message);
    }

    public ImdbException(String message, Throwable cause) {
        super(message, cause);
    }
}