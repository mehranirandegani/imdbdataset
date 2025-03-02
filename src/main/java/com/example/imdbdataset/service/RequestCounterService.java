package com.example.imdbdataset.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

/**
 * This service is responsible for counting the number of requests made to the application.
 * It uses an AtomicLong to ensure thread-safe increment operations.
 */
@Service
public class RequestCounterService {
    private final AtomicLong counter = new AtomicLong(0);

    public void incrementCounter() {
        counter.incrementAndGet();
    }

    public long getCount() {
        return counter.get();
    }
}