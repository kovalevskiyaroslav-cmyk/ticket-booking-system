package com.yaroslav.ticket_booking_system.service;

import com.yaroslav.ticket_booking_system.dto.TicketResponseDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface TicketService {
    TicketResponseDto getTicketById(UUID id);

    List<TicketResponseDto> getTicketsByPriceBetween(BigDecimal min, BigDecimal max);

    List<TicketResponseDto> getTicketsByEventId(UUID id);
}
