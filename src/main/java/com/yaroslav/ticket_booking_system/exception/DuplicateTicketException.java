package com.yaroslav.ticket_booking_system.exception;

import java.util.UUID;

public class DuplicateTicketException extends RuntimeException {

    public DuplicateTicketException(UUID eventId, UUID seatId) {
        super(String.format("Ticket already exists for event ID: %s and seat ID: %s", eventId, seatId));
    }
}
