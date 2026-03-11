package com.yaroslav.ticket_booking_system.mapper;

import com.yaroslav.ticket_booking_system.dto.TicketRequestDto;
import com.yaroslav.ticket_booking_system.dto.TicketResponseDto;
import com.yaroslav.ticket_booking_system.model.Ticket;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TicketMapper {

    @Mapping(target = "order", ignore = true)
    @Mapping(target = "event", ignore = true)
    @Mapping(target = "seat", ignore = true)
    Ticket toEntity(TicketRequestDto requestDto);

    @Mapping(source = "event.id", target = "eventId")
    @Mapping(source = "order.id", target = "orderId")
    @Mapping(source = "seat.id", target = "seatId")
    TicketResponseDto toDto(Ticket ticket);
}
