package com.yaroslav.ticket_booking_system.controller;

import com.yaroslav.ticket_booking_system.dto.SeatRequestDto;
import com.yaroslav.ticket_booking_system.dto.SeatResponseDto;
import com.yaroslav.ticket_booking_system.dto.SeatUpdateDto;
import com.yaroslav.ticket_booking_system.service.SeatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/seats")
@RequiredArgsConstructor
@CrossOrigin(origins = {"https://ticket-booking-client-phhj.onrender.com", "http://localhost:3000", "http://localhost:5173"})
@Tag(name = "Seat Management", description = "APIs for managing seats in venues")
public class SeatController {

    private final SeatService seatService;

    @PostMapping
    @Operation(summary = "Create a new seat", description = "Creates a new seat in a specific venue")
    public ResponseEntity<SeatResponseDto> createSeat(@Valid @RequestBody SeatRequestDto requestDto) {

        final SeatResponseDto created = seatService.createSeat(requestDto);

        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get seat by ID", description = "Retrieves detailed information about a specific seat")
    public ResponseEntity<SeatResponseDto> getSeatById(@PathVariable UUID id) {

        final SeatResponseDto seat = seatService.getSeatById(id);

        return ResponseEntity.ok(seat);
    }

    @GetMapping("/num/{number}/venue/{venueId}")
    @Operation(summary = "Get seat by venue ID and seat number", description = "Retrieves a specific seat by venue and seat number")
    public ResponseEntity<SeatResponseDto> getSeatByVenueIdAndNumber(
            @PathVariable Integer number,
            @PathVariable UUID venueId) {

        final SeatResponseDto seat = seatService.getSeatByVenueIdAndNumber(venueId, number);

        return ResponseEntity.ok(seat);
    }

    @GetMapping("/section/{section}/venue/{venueId}")
    @Operation(summary = "Get seats by venue ID and section", description = "Retrieves all seats in a specific section of a venue")
    public ResponseEntity<List<SeatResponseDto>> getSeatsByVenueIdAndSection(
            @PathVariable Integer section,
            @PathVariable UUID venueId) {

        final List<SeatResponseDto> seats = seatService.getSeatsByVenueIdAndSection(venueId, section);

        return ResponseEntity.ok(seats);
    }

    @GetMapping("/by-price")
    @Operation(summary = "Get seats by price range", description = "Retrieves all seats within a specified price range")
    public ResponseEntity<List<SeatResponseDto>> getSeatsByPriceBetween(
            @RequestParam("lower") BigDecimal min,
            @RequestParam("higher") BigDecimal max) {

        final List<SeatResponseDto> seats = seatService.getSeatsByPriceBetween(min, max);

        return ResponseEntity.ok(seats);
    }

    @GetMapping
    @Operation(summary = "Get all seats", description = "Retrieves a list of all seats in the system")
    public ResponseEntity<List<SeatResponseDto>> getAllSeats() {

        final List<SeatResponseDto> seats = seatService.getAllSeats();

        return ResponseEntity.ok(seats);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update seat by ID", description = "Updates seat information (number, section, price)")
    public ResponseEntity<SeatResponseDto> updateSeatById(
            @PathVariable UUID id,
            @Valid @RequestBody SeatUpdateDto updateDto) {

        final SeatResponseDto seat = seatService.updateSeatById(id, updateDto);

        return ResponseEntity.ok(seat);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete seat by ID", description = "Permanently deletes a seat (only if no tickets are associated)")
    public ResponseEntity<Void> deleteSeatById(@PathVariable UUID id) {

        seatService.deleteSeatById(id);

        return ResponseEntity.noContent().build();
    }
}