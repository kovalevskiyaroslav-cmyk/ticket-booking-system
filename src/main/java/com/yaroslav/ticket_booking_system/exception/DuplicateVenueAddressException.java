package com.yaroslav.ticket_booking_system.exception;

public class DuplicateVenueAddressException extends RuntimeException {

    public DuplicateVenueAddressException(String address) {
        super("Venue with address '" + address + "' already exists");
    }
}
