package com.yaroslav.ticket_booking_system.model;

import java.util.Set;

public enum PaymentStatus {
    FAILED(Set.of()),
    REFUNDED(Set.of()),
    COMPLETED(Set.of(REFUNDED)),
    PENDING(Set.of(COMPLETED, FAILED));

    private final Set<PaymentStatus> allowedNext;

    PaymentStatus(Set<PaymentStatus> allowedNext) {
        this.allowedNext = allowedNext;
    }

    public boolean cannotTransitionTo(PaymentStatus next) {
        return !allowedNext.contains(next);
    }
}