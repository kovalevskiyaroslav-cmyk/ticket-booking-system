package com.yaroslav.ticket_booking_system.service;

public interface SafeCounterService {
    int increment();

    int getValue();

    void reset();
}