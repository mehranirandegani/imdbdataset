package com.example.imdbdataset.exception;

public class DataImportException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DataImportException(String message) {
        super(message);
    }

    public DataImportException(String message, Throwable cause) {
        super(message, cause);
    }
}