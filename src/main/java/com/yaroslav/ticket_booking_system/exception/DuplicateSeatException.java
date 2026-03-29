package com.yaroslav.ticket_booking_system.exception;

import java.util.UUID;

public class DuplicateSeatException extends RuntimeException {

    public DuplicateSeatException(UUID venueId, Integer section, Integer number) {
        super(String.format("Seat already exists in venue ID: %s, section: %d, number: %d",
                venueId, section, number));
    }
}
