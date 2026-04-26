package com.yaroslav.ticket_booking_system;

import com.yaroslav.ticket_booking_system.service.RaceConditionService;
import com.yaroslav.ticket_booking_system.service.SafeCounterService;
import com.yaroslav.ticket_booking_system.service.impl.RaceConditionServiceImpl;
import com.yaroslav.ticket_booking_system.service.impl.SafeCounterServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RaceConditionServiceTest {

    private SafeCounterService safeCounterService;
    private RaceConditionService raceConditionService;

    @BeforeEach
    void setUp() {
        safeCounterService = new SafeCounterServiceImpl();
        raceConditionService = new RaceConditionServiceImpl(safeCounterService);
    }

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

    @Test
    void demonstrateShouldResetSafeCounterBeforeEachRun() {
        raceConditionService.demonstrate(2, 50);
        int firstValue = safeCounterService.getValue();

        raceConditionService.demonstrate(2, 50);
        int secondValue = safeCounterService.getValue();

        assertEquals(firstValue, secondValue);
    }

    @Test
    void demonstrateShouldShowLostOperationsForUnsafeCounter() {
        int threads = 10;
        int incrementsPerThread = 1000;

        final String result = raceConditionService.demonstrate(threads, incrementsPerThread);

        assertTrue(result.contains("lost"));
        assertTrue(result.contains("lost") && result.contains("operations"));
    }
}