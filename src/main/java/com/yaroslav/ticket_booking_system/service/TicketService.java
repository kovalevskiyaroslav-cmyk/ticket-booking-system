package com.yaroslav.ticket_booking_system.service;

import com.yaroslav.ticket_booking_system.dto.TicketResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface TicketService {
    TicketResponseDto getTicketById(UUID id);

    List<TicketResponseDto> getTicketsByPriceBetween(BigDecimal min, BigDecimal max);

    List<TicketResponseDto> getTicketsByEventId(UUID id);

    List<TicketResponseDto> getAllTickets();

    Page<TicketResponseDto> getTicketsByVenue(UUID venueId, Pageable pageable);
}
