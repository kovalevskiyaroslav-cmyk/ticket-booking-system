package com.yaroslav.ticket_booking_system.service.impl;

import com.yaroslav.ticket_booking_system.service.SafeCounterService;
import org.springframework.stereotype.Service;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class SafeCounterServiceImpl implements SafeCounterService {

    private final AtomicInteger counter = new AtomicInteger(0);

    @Override
    public int increment() {
        return counter.incrementAndGet();
    }

    @Override
    public int getValue() {
        return counter.get();
    }

    @Override
    public void reset() {
        counter.set(0);
    }
}