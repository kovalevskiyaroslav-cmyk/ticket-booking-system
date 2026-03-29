package com.yaroslav.ticket_booking_system.exception;

import java.util.UUID;

public class FavoriteEventNotFoundException extends RuntimeException {

    public FavoriteEventNotFoundException(UUID eventId) {
        super("Event " + eventId + " is not in user's favorites");
    }
}
