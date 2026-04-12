package com.yaroslav.ticket_booking_system;

import com.yaroslav.ticket_booking_system.service.RaceConditionService;
import com.yaroslav.ticket_booking_system.service.impl.RaceConditionServiceImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RaceConditionServiceTest {

    private final RaceConditionService raceConditionService = new RaceConditionServiceImpl();

    @Test
    void demonstrateShouldReturnFormattedString() {
        final String result = raceConditionService.demonstrate(2, 100);

        assertNotNull(result);
        assertTrue(result.contains("Threads: 2"));
        assertTrue(result.contains("Expected value: 200"));
        assertTrue(result.contains("Unsafe counter"));
        assertTrue(result.contains("Safe counter"));
    }

    @Test
    void demonstrateZeroThreadsShouldWork() {
        final String result = raceConditionService.demonstrate(0, 100);

        assertTrue(result.contains("Expected value: 0"));
    }

    @Test
    @SuppressWarnings("ResultOfMethodCallIgnored")
    void demonstrateShouldHandleInterruptedException() {
        Thread.currentThread().interrupt();

        final String result = raceConditionService.demonstrate(1, 1);

        assertNotNull(result);

        Thread.interrupted();
    }
}