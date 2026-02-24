package com.yaroslav.ticket_booking_system.service;

import com.yaroslav.ticket_booking_system.dto.TicketRequestDto;
import com.yaroslav.ticket_booking_system.dto.TicketResponseDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface TicketService {
    TicketResponseDto createTicket(TicketRequestDto requestDto);

    TicketResponseDto getTicketById(UUID id);

    List<TicketResponseDto> getTicketsByPriceBetween(
            BigDecimal lowerPrice,
            BigDecimal higherPrice);

    List<TicketResponseDto> getTicketsByEventId(UUID id);

    TicketResponseDto addSeatToTicket(UUID id, UUID seatId);

    TicketResponseDto removeSeatFromTicket(UUID id, UUID seatId);

    void deleteTicketById(UUID id);
}
