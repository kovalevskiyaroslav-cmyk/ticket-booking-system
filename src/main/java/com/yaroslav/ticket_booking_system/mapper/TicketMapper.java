package com.yaroslav.ticket_booking_system.mapper;

import com.yaroslav.ticket_booking_system.dto.TicketRequestDto;
import com.yaroslav.ticket_booking_system.dto.TicketResponseDto;
import com.yaroslav.ticket_booking_system.model.Seat;
import com.yaroslav.ticket_booking_system.model.Ticket;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface TicketMapper {

    @Mapping(target = "order", ignore = true)
    @Mapping(target = "event", ignore = true)
    @Mapping(target = "seats", ignore = true)
    Ticket toEntity(TicketRequestDto requestDto);

    @Mapping(source = "event.id", target = "eventId")
    @Mapping(source = "order.id", target = "orderId")
    @Mapping(source = "seats", target = "seatIds")
    TicketResponseDto toDto(Ticket ticket);

    default List<UUID> mapSeats(List<Seat> seats) {
        return seats.stream()
                .map(Seat::getId)
                .toList();
    }
}
