package com.yaroslav.ticket_booking_system.controller;

import com.yaroslav.ticket_booking_system.service.RaceConditionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/race-demo")
@RequiredArgsConstructor
@CrossOrigin(origins = {"https://ticket-booking-client-phhj.onrender.com", "http://localhost:3000", "http://localhost:5173"})
public class RaceConditionController {

    private final RaceConditionService demoService;

    @PostMapping("/run")
    public ResponseEntity<Map<String, String>> runDemo(
            @RequestParam(defaultValue = "50") int threads,
            @RequestParam(defaultValue = "1000") int increments) {

        final String result = demoService.demonstrate(threads, increments);
        return ResponseEntity.ok(Map.of("result", result));
    }
}