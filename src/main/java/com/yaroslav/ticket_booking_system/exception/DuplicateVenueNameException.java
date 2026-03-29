package com.yaroslav.ticket_booking_system.exception;

public class DuplicateVenueNameException extends RuntimeException {

    public DuplicateVenueNameException(String venueName) {
        super("Venue with name '" + venueName + "' already exists");
    }
}
