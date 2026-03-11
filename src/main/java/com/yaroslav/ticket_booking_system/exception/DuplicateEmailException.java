package com.yaroslav.ticket_booking_system.exception;

public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String email) {
        super("User with email " + email + " already exists");
    }
}
