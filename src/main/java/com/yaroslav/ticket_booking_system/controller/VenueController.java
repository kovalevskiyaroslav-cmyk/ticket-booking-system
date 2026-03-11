package com.yaroslav.ticket_booking_system.controller;

import com.yaroslav.ticket_booking_system.dto.VenueRequestDto;
import com.yaroslav.ticket_booking_system.dto.VenueResponseDto;
import com.yaroslav.ticket_booking_system.dto.VenueUpdateDto;
import com.yaroslav.ticket_booking_system.service.VenueService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/venues")
@RequiredArgsConstructor
public class VenueController {

    private final VenueService venueService;

    @PostMapping
    public ResponseEntity<VenueResponseDto> createVenue(@RequestBody VenueRequestDto requestDto) {

        final VenueResponseDto created = venueService.createVenue(requestDto);

        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VenueResponseDto> getVenueById(@PathVariable UUID id) {

        final VenueResponseDto venue = venueService.getVenueById(id);

        return ResponseEntity.ok(venue);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<VenueResponseDto> getVenueByName(@PathVariable String name) {

        final VenueResponseDto venue = venueService.getVenueByName(name);

        return ResponseEntity.ok(venue);
    }

    @GetMapping("/address/{address}")
    public ResponseEntity<VenueResponseDto> getVenueByAddress(@PathVariable String address) {

        final VenueResponseDto venue = venueService.getVenueByAddress(address);

        return ResponseEntity.ok(venue);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<VenueResponseDto> updateVenueById(@PathVariable UUID id, @RequestBody VenueUpdateDto updateDto) {

        final VenueResponseDto venue = venueService.updateVenueById(id, updateDto);

        return ResponseEntity.ok(venue);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVenueById(@PathVariable UUID id) {

        venueService.deleteVenueById(id);

        return ResponseEntity.noContent().build();
    }
}
