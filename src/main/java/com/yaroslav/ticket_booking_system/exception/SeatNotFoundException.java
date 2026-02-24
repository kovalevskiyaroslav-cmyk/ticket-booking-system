package com.yaroslav.ticket_booking_system.exception;

import java.util.UUID;

public class SeatNotFoundException extends RuntimeException {

    public SeatNotFoundException(UUID id) {
        super("Seat not found with id: " + id);
    }

    public SeatNotFoundException(String message) {
        super(message);
    }
}
