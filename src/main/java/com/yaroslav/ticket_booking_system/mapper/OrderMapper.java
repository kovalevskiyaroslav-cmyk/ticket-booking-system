package com.yaroslav.ticket_booking_system.mapper;

import com.yaroslav.ticket_booking_system.dto.OrderRequestDto;
import com.yaroslav.ticket_booking_system.dto.OrderResponseDto;
import com.yaroslav.ticket_booking_system.model.Order;
import com.yaroslav.ticket_booking_system.model.Ticket;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "dateTime", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "totalPrice", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "tickets", ignore = true)
    Order toEntity(OrderRequestDto requestDto);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "tickets", target = "ticketIds")
    OrderResponseDto toDto(Order order);

    default List<UUID> mapTickets(List<Ticket> tickets) {
        return tickets.stream()
                .map(Ticket::getId)
                .toList();
    }
}
