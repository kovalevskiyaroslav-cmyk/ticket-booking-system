package com.yaroslav.ticket_booking_system.service;

import com.yaroslav.ticket_booking_system.dto.VenueRequestDto;
import com.yaroslav.ticket_booking_system.dto.VenueResponseDto;
import com.yaroslav.ticket_booking_system.dto.VenueUpdateDto;

import java.util.List;
import java.util.UUID;

public interface VenueService {
    VenueResponseDto createVenue(VenueRequestDto requestDto);

    VenueResponseDto getVenueById(UUID id);

    VenueResponseDto getVenueByName(String name);

    VenueResponseDto getVenueByAddress(String address);

    List<VenueResponseDto> getVenuesByCity(String city);

    VenueResponseDto updateVenueById(UUID id, VenueUpdateDto updateDto);

    void deleteVenueById(UUID id);
}
