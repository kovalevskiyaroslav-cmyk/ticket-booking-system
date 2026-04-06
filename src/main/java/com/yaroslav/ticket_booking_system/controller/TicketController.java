package com.yaroslav.ticket_booking_system.controller;

import com.yaroslav.ticket_booking_system.dto.TicketResponseDto;
import com.yaroslav.ticket_booking_system.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
@Tag(name = "Ticket Management", description = "APIs for retrieving ticket information")
public class TicketController {

    private final TicketService ticketService;

    @GetMapping("/{id}")
    @Operation(summary = "Get ticket by ID", description = "Retrieves detailed information about a specific ticket")
    public ResponseEntity<TicketResponseDto> getTicketById(@PathVariable UUID id) {

        final TicketResponseDto ticket = ticketService.getTicketById(id);

        return ResponseEntity.ok(ticket);
    }

    @GetMapping("/by-price")
    @Operation(summary = "Get tickets by price range", description = "Retrieves all tickets within a specified price range")
    public ResponseEntity<List<TicketResponseDto>> getTicketsByPriceBetween(
            @RequestParam("lower") BigDecimal min,
            @RequestParam("higher") BigDecimal max) {

        final List<TicketResponseDto> tickets = ticketService.getTicketsByPriceBetween(min, max);

        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/event/{eventId}")
    @Operation(summary = "Get tickets by event ID", description = "Retrieves all tickets for a specific event")
    public ResponseEntity<List<TicketResponseDto>> getTicketsByEventId(@PathVariable UUID eventId) {

        final List<TicketResponseDto> tickets = ticketService.getTicketsByEventId(eventId);

        return ResponseEntity.ok(tickets);
    }

    @GetMapping
    @Operation(summary = "Get all tickets", description = "Retrieves a list of all tickets in the system")
    public ResponseEntity<List<TicketResponseDto>> getAllTickets() {

        final List<TicketResponseDto> tickets = ticketService.getAllTickets();

        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/by-event/{name}")
    @Operation(summary = "Get tickets by event name", description = "Retrieves paginated tickets for a specific event name")
    public ResponseEntity<Page<TicketResponseDto>> getTicketsByEventName(
            @PathVariable String name,
            @ParameterObject Pageable pageable) {

        final Page<TicketResponseDto> ticketsPage = ticketService.getTicketsByEventName(name, pageable);

        return ResponseEntity.ok(ticketsPage);
    }
}