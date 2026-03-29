package com.yaroslav.ticket_booking_system.exception;

import java.util.UUID;

public class OrderAlreadyDeletedException extends RuntimeException {

    public OrderAlreadyDeletedException(UUID orderId) {
        super("Order with ID " + orderId + " has already been deleted");
    }
}
