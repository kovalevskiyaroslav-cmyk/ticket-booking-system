package com.yaroslav.ticket_booking_system.service;

import com.yaroslav.ticket_booking_system.dto.SeatRequestDto;
import com.yaroslav.ticket_booking_system.dto.SeatResponseDto;
import com.yaroslav.ticket_booking_system.dto.SeatUpdateDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface SeatService {
    SeatResponseDto createSeat(SeatRequestDto requestDto);

    SeatResponseDto getSeatById(UUID id);

    SeatResponseDto getSeatByVenueIdAndNumber(UUID venueId, Integer number);

    List<SeatResponseDto> getSeatsByVenueIdAndSection(UUID venueId, Integer section);

    List<SeatResponseDto> getSeatsByPriceBetween(BigDecimal min, BigDecimal max);

    SeatResponseDto updateSeatById(UUID id, SeatUpdateDto updateDto);

    void deleteSeatById(UUID id);
}
