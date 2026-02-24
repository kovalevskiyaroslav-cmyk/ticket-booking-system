package com.yaroslav.ticket_booking_system.controller;

import com.yaroslav.ticket_booking_system.dto.EventRequestDto;
import com.yaroslav.ticket_booking_system.dto.EventResponseDto;
import com.yaroslav.ticket_booking_system.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    public ResponseEntity<EventResponseDto> createEvent(@RequestBody EventRequestDto requestDto) {

        final EventResponseDto created = eventService.createEvent(requestDto);

        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDto> getEventById(@PathVariable UUID id) {

        final EventResponseDto event = eventService.getEventById(id);

        return ResponseEntity.ok(event);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<EventResponseDto> getEventByName(@PathVariable String name) {

        final EventResponseDto event = eventService.getEventByName(name);

        return ResponseEntity.ok(event);
    }

    @GetMapping("/by-date")
    public ResponseEntity<List<EventResponseDto>> getEventsByDateTimeBetween(
            @RequestParam("after") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTimeAfter,
            @RequestParam("before") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTimeBefore) {

        final List<EventResponseDto> events = eventService.getEventsByDateTimeBetween(dateTimeBefore, dateTimeAfter);

        return ResponseEntity.ok(events);
    }

}
