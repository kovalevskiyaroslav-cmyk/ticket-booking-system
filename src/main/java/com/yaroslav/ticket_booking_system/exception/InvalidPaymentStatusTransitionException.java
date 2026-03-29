package com.yaroslav.ticket_booking_system.exception;

import com.yaroslav.ticket_booking_system.model.PaymentStatus;

public class InvalidPaymentStatusTransitionException extends RuntimeException {

    public InvalidPaymentStatusTransitionException(PaymentStatus currentStatus, PaymentStatus requestedStatus) {
        super("Cannot transition payment from " + currentStatus + " to " + requestedStatus);
    }
}
