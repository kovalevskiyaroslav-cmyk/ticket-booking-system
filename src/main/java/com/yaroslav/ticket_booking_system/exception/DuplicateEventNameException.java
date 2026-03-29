package com.yaroslav.ticket_booking_system.exception;

public class DuplicateEventNameException extends RuntimeException {

    public DuplicateEventNameException(String eventName) {
        super("Event with name '" + eventName + "' already exists");
    }
}
