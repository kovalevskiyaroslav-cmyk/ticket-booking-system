package com.yaroslav.ticket_booking_system.controller;

import com.yaroslav.ticket_booking_system.dto.TicketRequestDto;
import com.yaroslav.ticket_booking_system.dto.TicketResponseDto;
import com.yaroslav.ticket_booking_system.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping
    public ResponseEntity<TicketResponseDto> createTicket(@RequestBody TicketRequestDto requestDto) {

        final TicketResponseDto created = ticketService.createTicket(requestDto);

        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponseDto> getTicketById(@PathVariable UUID id) {

        final TicketResponseDto ticket = ticketService.getTicketById(id);

        return ResponseEntity.ok(ticket);
    }

    @GetMapping("/by-date")
    public ResponseEntity<List<TicketResponseDto>> getTicketsByPurchaseDateTimeBetween(
            @RequestParam("after") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTimeAfter,
            @RequestParam("before") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTimeBefore) {

        final List<TicketResponseDto> tickets = ticketService.getTicketsByPurchaseDateTimeBetween(
                dateTimeBefore,
                dateTimeAfter);

        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<TicketResponseDto>> getTicketsByEventId(@PathVariable UUID eventId) {

        final List<TicketResponseDto> tickets = ticketService.getTicketsByEventId(eventId);

        return ResponseEntity.ok(tickets);
    }

    @PutMapping("/{id}/seats/{seatId}")
    public ResponseEntity<TicketResponseDto> addSeatToTicket(@PathVariable UUID id, @PathVariable UUID seatId) {

        final TicketResponseDto ticket = ticketService.addSeatToTicket(id, seatId);

        return ResponseEntity.ok(ticket);
    }

    @DeleteMapping("/{id}/seats/{seatId}")
    public ResponseEntity<TicketResponseDto> removeSeatFromTicket(@PathVariable UUID id, @PathVariable UUID seatId) {

        final TicketResponseDto ticket = ticketService.removeSeatFromTicket(id, seatId);

        return ResponseEntity.ok(ticket);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicketById(@PathVariable UUID id) {

        ticketService.deleteTicketById(id);

        return ResponseEntity.noContent().build();
    }
}
