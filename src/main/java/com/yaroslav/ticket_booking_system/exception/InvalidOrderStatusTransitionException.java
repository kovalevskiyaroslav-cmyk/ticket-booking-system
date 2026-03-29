package com.yaroslav.ticket_booking_system.exception;

import com.yaroslav.ticket_booking_system.model.OrderStatus;

public class InvalidOrderStatusTransitionException extends RuntimeException {

    public InvalidOrderStatusTransitionException(OrderStatus currentStatus, OrderStatus requestedStatus) {
        super("Cannot transition from " + currentStatus + " to " + requestedStatus);
    }
}
