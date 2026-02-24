package com.yaroslav.ticket_booking_system.mapper;

import com.yaroslav.ticket_booking_system.dto.UserRequestDto;
import com.yaroslav.ticket_booking_system.dto.UserResponseDto;
import com.yaroslav.ticket_booking_system.model.Event;
import com.yaroslav.ticket_booking_system.model.Order;
import com.yaroslav.ticket_booking_system.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "active", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "favoriteEvents", ignore = true)
    User toEntity(UserRequestDto requestDto);

    @Mapping(source = "orders", target = "orderIds")
    @Mapping(source = "favoriteEvents", target = "favouriteEventIds")
    UserResponseDto toDto(User user);

    default List<UUID> mapOrders(List<Order> orders) {
        return orders.stream()
                .map(Order::getId)
                .toList();
    }

    default Set<UUID> mapFavoriteEvents(Set<Event> favoriteEvents) {
        return favoriteEvents.stream()
                .map(Event::getId)
                .collect(Collectors.toSet());
    }
}
