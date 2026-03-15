package com.yaroslav.ticket_booking_system.controller;

import com.yaroslav.ticket_booking_system.dto.TicketResponseDto;
import com.yaroslav.ticket_booking_system.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponseDto> getTicketById(@PathVariable UUID id) {

        final TicketResponseDto ticket = ticketService.getTicketById(id);

        return ResponseEntity.ok(ticket);
    }

    @GetMapping("/by-price")
    public ResponseEntity<List<TicketResponseDto>> getTicketsByPriceBetween(
            @RequestParam("lower") BigDecimal min,
            @RequestParam("higher") BigDecimal max) {

        final List<TicketResponseDto> tickets = ticketService.getTicketsByPriceBetween(min, max);

        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<TicketResponseDto>> getTicketsByEventId(@PathVariable UUID eventId) {

        final List<TicketResponseDto> tickets = ticketService.getTicketsByEventId(eventId);

        return ResponseEntity.ok(tickets);
    }

    @GetMapping
    public ResponseEntity<List<TicketResponseDto>> getAllTickets() {

        final List<TicketResponseDto> tickets = ticketService.getAllTickets();

        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/by-venue/{venueId}")
    public ResponseEntity<Page<TicketResponseDto>> getTicketsByVenue(
            @PathVariable UUID venueId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        final Pageable pageable = PageRequest.of(page, size, Sort.by("price").ascending());
        final Page<TicketResponseDto> ticketsPage = ticketService.getTicketsByVenue(venueId, pageable);

        return ResponseEntity.ok(ticketsPage);
    }
}
