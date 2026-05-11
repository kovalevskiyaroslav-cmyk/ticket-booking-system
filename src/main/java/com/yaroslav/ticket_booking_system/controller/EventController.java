package com.yaroslav.ticket_booking_system.controller;

import com.yaroslav.ticket_booking_system.dto.EventRequestDto;
import com.yaroslav.ticket_booking_system.dto.EventResponseDto;
import com.yaroslav.ticket_booking_system.dto.EventUpdateDto;
import com.yaroslav.ticket_booking_system.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
@CrossOrigin(origins = {"https://ticket-booking-client-phhj.onrender.com", "http://localhost:3000", "http://localhost:5173"})
@Tag(name = "Event Management", description = "APIs for managing events in the ticket booking system")
public class EventController {

    private final EventService eventService;

    @PostMapping
    @Operation(summary = "Create a new event", description = "Creates a new event with venue and date/time information")
    public ResponseEntity<EventResponseDto> createEvent(@Valid @RequestBody EventRequestDto requestDto) {

        final EventResponseDto created = eventService.createEvent(requestDto);

        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get event by ID", description = "Retrieves detailed information about a specific event")
    public ResponseEntity<EventResponseDto> getEventById(@PathVariable UUID id) {

        final EventResponseDto event = eventService.getEventById(id);

        return ResponseEntity.ok(event);
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "Get event by name", description = "Retrieves event information by its name")
    public ResponseEntity<EventResponseDto> getEventByName(@PathVariable String name) {

        final EventResponseDto event = eventService.getEventByName(name);

        return ResponseEntity.ok(event);
    }

    @GetMapping("/by-date")
    @Operation(summary = "Get events by date range", description = "Retrieves all events occurring between two dates")
    public ResponseEntity<List<EventResponseDto>> getEventsByDateTimeBetween(
            @RequestParam("before") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam("after") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        final List<EventResponseDto> events = eventService.getEventsByDateTimeBetween(start, end);

        return ResponseEntity.ok(events);
    }

    @GetMapping
    @Operation(summary = "Get all events", description = "Retrieves a list of all events in the system")
    public ResponseEntity<List<EventResponseDto>> getAllEvents() {

        final List<EventResponseDto> events = eventService.getAllEvents();

        return ResponseEntity.ok(events);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update event by ID", description = "Updates event information (name, description, date/time, venue)")
    public ResponseEntity<EventResponseDto> updateEventById(@PathVariable UUID id, @Valid @RequestBody EventUpdateDto updateDto) {

        final EventResponseDto event = eventService.updateById(id, updateDto);

        return ResponseEntity.ok(event);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete event by ID", description = "Permanently deletes an event (only if no tickets are sold)")
    ResponseEntity<Void> deleteEventById(@PathVariable UUID id) {

        eventService.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}