package com.yaroslav.ticket_booking_system.service.impl;

import com.yaroslav.ticket_booking_system.service.RaceConditionService;
import com.yaroslav.ticket_booking_system.service.SafeCounterService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@SuppressWarnings("ResultOfMethodCallIgnored")
public class RaceConditionServiceImpl implements RaceConditionService {

    private final SafeCounterService safeCounterService;

    @Autowired
    public RaceConditionServiceImpl(SafeCounterService safeCounterService) {
        this.safeCounterService = safeCounterService;
    }

    @Override
    public String demonstrate(int threads, int increments) {
        safeCounterService.reset();

        if (threads <= 0) {
            return String.format(
                    "Threads: %d, Increments per thread: %d " +
                            "Expected value: %d " +
                            "Unsafe counter: 0 (lost 0 operations) " +
                            "Safe counter with AtomicInteger: 0 " +
                            "Execution time: 0 ms " +
                            "Conclusion: No threads to demonstrate race condition.",
                    threads, increments, 0
            );
        }

        final UnsafeCounter unsafeCounter = new UnsafeCounter();

        final ExecutorService executor = Executors.newFixedThreadPool(threads);

        final long startTime = System.currentTimeMillis();

        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                for (int j = 0; j < increments; j++) {
                    unsafeCounter.increment();
                    safeCounterService.increment();
                }
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        final long duration = System.currentTimeMillis() - startTime;
        final int expected = threads * increments;

        return String.format(
                "Threads: %d, Increments per thread: %d " +
                        "Expected value: %d " +
                        "Unsafe counter: %d (lost %d operations) " +
                        "Safe counter with AtomicInteger: %d " +
                        "Execution time: %d ms " +
                        "Conclusion: Race condition causes data loss. AtomicInteger provides thread-safe operations.",
                threads, increments, expected,
                unsafeCounter.getValue(), expected - unsafeCounter.getValue(),
                safeCounterService.getValue(), duration
        );
    }

    @Getter
    static class UnsafeCounter {
        private int value = 0;

        public void increment() {
            value++;
        }
    }
}