package com.yaroslav.ticket_booking_system.controller;

import com.yaroslav.ticket_booking_system.service.SafeCounterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/safe-counter")
@RequiredArgsConstructor
@CrossOrigin(origins = {"https://ticket-booking-client-phhj.onrender.com", "http://localhost:3000", "http://localhost:5173"})
public class SafeCounterController {

    private final SafeCounterService counterService;

    @PostMapping("/increment")
    public ResponseEntity<Map<String, Integer>> increment() {
        final int value = counterService.increment();
        return ResponseEntity.ok(Map.of("value", value));
    }

    @GetMapping("/value")
    public ResponseEntity<Map<String, Integer>> getValue() {
        return ResponseEntity.ok(Map.of("value", counterService.getValue()));
    }

    @PostMapping("/reset")
    public ResponseEntity<Void> reset() {
        counterService.reset();
        return ResponseEntity.ok().build();
    }
}