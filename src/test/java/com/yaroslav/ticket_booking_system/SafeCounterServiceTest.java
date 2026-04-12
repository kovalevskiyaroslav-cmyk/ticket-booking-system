package com.yaroslav.ticket_booking_system;

import com.yaroslav.ticket_booking_system.service.SafeCounterService;
import com.yaroslav.ticket_booking_system.service.impl.SafeCounterServiceImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SafeCounterServiceTest {

    private final SafeCounterService safeCounterService = new SafeCounterServiceImpl();

    @Test
    void incrementShouldIncreaseValue() {
        safeCounterService.reset();
        final int value = safeCounterService.increment();
        assertEquals(1, value);
    }

    @Test
    void getValueShouldReturnCurrentValue() {
        safeCounterService.reset();
        safeCounterService.increment();
        safeCounterService.increment();

        assertEquals(2, safeCounterService.getValue());
    }

    @Test
    void resetShouldSetToZero() {
        safeCounterService.increment();
        safeCounterService.reset();

        assertEquals(0, safeCounterService.getValue());
    }
}