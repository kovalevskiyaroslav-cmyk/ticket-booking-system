package com.yaroslav.ticket_booking_system.exception;

public class DuplicatePhoneException extends RuntimeException {

    public DuplicatePhoneException(String phone) {
        super("User with phone " + phone + " already exists");
    }
}
