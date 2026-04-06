package com.yaroslav.ticket_booking_system.controller;

import com.yaroslav.ticket_booking_system.dto.VenueRequestDto;
import com.yaroslav.ticket_booking_system.dto.VenueResponseDto;
import com.yaroslav.ticket_booking_system.dto.VenueUpdateDto;
import com.yaroslav.ticket_booking_system.service.VenueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/venues")
@RequiredArgsConstructor
@Tag(name = "Venue Management", description = "APIs for managing venues where events take place")
public class VenueController {

    private final VenueService venueService;

    @PostMapping
    @Operation(summary = "Create a new venue", description = "Creates a new venue with name and address")
    public ResponseEntity<VenueResponseDto> createVenue(@Valid @RequestBody VenueRequestDto requestDto) {

        final VenueResponseDto created = venueService.createVenue(requestDto);

        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get venue by ID", description = "Retrieves detailed information about a specific venue")
    public ResponseEntity<VenueResponseDto> getVenueById(@PathVariable UUID id) {

        final VenueResponseDto venue = venueService.getVenueById(id);

        return ResponseEntity.ok(venue);
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "Get venue by name", description = "Retrieves venue information by its name")
    public ResponseEntity<VenueResponseDto> getVenueByName(@PathVariable String name) {

        final VenueResponseDto venue = venueService.getVenueByName(name);

        return ResponseEntity.ok(venue);
    }

    @GetMapping("/address/{address}")
    @Operation(summary = "Get venue by address", description = "Retrieves venue information by its address")
    public ResponseEntity<VenueResponseDto> getVenueByAddress(@PathVariable String address) {

        final VenueResponseDto venue = venueService.getVenueByAddress(address);

        return ResponseEntity.ok(venue);
    }

    @GetMapping
    @Operation(summary = "Get all venues", description = "Retrieves a list of all venues in the system")
    public ResponseEntity<List<VenueResponseDto>> getAllVenues() {

        final List<VenueResponseDto> venues = venueService.getAllVenues();

        return ResponseEntity.ok(venues);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update venue by ID", description = "Updates venue information (name, address)")
    public ResponseEntity<VenueResponseDto> updateVenueById(
            @PathVariable UUID id,
            @Valid @RequestBody VenueUpdateDto updateDto) {

        final VenueResponseDto venue = venueService.updateVenueById(id, updateDto);

        return ResponseEntity.ok(venue);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete venue by ID", description = "Permanently deletes a venue (only if no events are scheduled)")
    public ResponseEntity<Void> deleteVenueById(@PathVariable UUID id) {

        venueService.deleteVenueById(id);

        return ResponseEntity.noContent().build();
    }
}