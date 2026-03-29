package com.yaroslav.ticket_booking_system.exception;

import java.util.UUID;

public class FavoriteEventAlreadyExistsException extends RuntimeException {

    public FavoriteEventAlreadyExistsException(UUID eventId) {
        super("Event " + eventId + " is already in user's favorites");
    }
}
