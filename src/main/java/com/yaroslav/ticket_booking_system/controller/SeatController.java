package com.yaroslav.ticket_booking_system.controller;

import com.yaroslav.ticket_booking_system.dto.SeatRequestDto;
import com.yaroslav.ticket_booking_system.dto.SeatResponseDto;
import com.yaroslav.ticket_booking_system.dto.SeatUpdateDto;
import com.yaroslav.ticket_booking_system.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
public class SeatController {

    private final SeatService seatService;

    @PostMapping
    public ResponseEntity<SeatResponseDto> createSeat(@RequestBody SeatRequestDto requestDto) {

        final SeatResponseDto created = seatService.createSeat(requestDto);

        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SeatResponseDto> getSeatById(@PathVariable UUID id) {

        final SeatResponseDto seat = seatService.getSeatById(id);

        return ResponseEntity.ok(seat);
    }

    @GetMapping("/num/{number}/venue/{venueId}")
    public ResponseEntity<SeatResponseDto> getSeatByVenueIdAndNumber(
            @PathVariable Integer number,
            @PathVariable UUID venueId) {

        final SeatResponseDto seat = seatService.getSeatByVenueIdAndNumber(venueId, number);

        return ResponseEntity.ok(seat);
    }

    @GetMapping("/section/{section}/venue/{venueId}")
    public ResponseEntity<List<SeatResponseDto>> getSeatsByVenueIdAndSection(
            @PathVariable Integer section,
            @PathVariable UUID venueId) {

        final List<SeatResponseDto> seats = seatService.getSeatsByVenueIdAndSection(venueId, section);

        return ResponseEntity.ok(seats);
    }

    @GetMapping("/by-price")
    public ResponseEntity<List<SeatResponseDto>> getSeatsByPriceBetween(
            @RequestParam("lower") BigDecimal min,
            @RequestParam("higher") BigDecimal max) {

        final List<SeatResponseDto> seats = seatService.getSeatsByPriceBetween(min, max);

        return ResponseEntity.ok(seats);
    }

    @GetMapping
    public ResponseEntity<List<SeatResponseDto>> getAllSeats() {

        final List<SeatResponseDto> seats = seatService.getAllSeats();

        return ResponseEntity.ok(seats);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<SeatResponseDto> updateSeatById(@PathVariable UUID id, @RequestBody SeatUpdateDto updateDto) {

        final SeatResponseDto seat = seatService.updateSeatById(id, updateDto);

        return ResponseEntity.ok(seat);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeatById(@PathVariable UUID id) {

        seatService.deleteSeatById(id);

        return ResponseEntity.noContent().build();
    }
}
