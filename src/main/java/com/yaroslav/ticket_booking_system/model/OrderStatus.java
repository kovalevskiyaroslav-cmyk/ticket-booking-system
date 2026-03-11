package com.yaroslav.ticket_booking_system.model;

import java.util.Set;

public enum OrderStatus {
    CANCELLED(Set.of()),
    REFUNDED(Set.of()),
    PAID(Set.of(REFUNDED)),
    CREATED(Set.of(PAID, CANCELLED));

    private final Set<OrderStatus> allowedNext;

    OrderStatus(Set<OrderStatus> allowedNext) {
        this.allowedNext = allowedNext;
    }

    boolean cannotTransitionTo(OrderStatus next) {
        return !allowedNext.contains(next);
    }
}
