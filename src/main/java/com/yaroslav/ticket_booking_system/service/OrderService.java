package com.yaroslav.ticket_booking_system.service;

import com.yaroslav.ticket_booking_system.dto.OrderRequestDto;
import com.yaroslav.ticket_booking_system.dto.OrderResponseDto;
import com.yaroslav.ticket_booking_system.dto.OrderUpdateDto;
import com.yaroslav.ticket_booking_system.dto.TicketRequestDto;
import com.yaroslav.ticket_booking_system.model.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderResponseDto createOrder(OrderRequestDto requestDto);

    OrderResponseDto getOrderById(UUID id);

    List<OrderResponseDto> getOrdersByStatus(OrderStatus orderStatus);

    List<OrderResponseDto> getOrdersByDeleted(Boolean deleted);

    List<OrderResponseDto> getOrdersByDateTimeBetween(LocalDateTime dateTimeBefore, LocalDateTime dateTimeAfter);

    OrderResponseDto updateOrderById(UUID id, OrderUpdateDto updateDto);

    OrderResponseDto addTicketToOrder(UUID id, TicketRequestDto requestDto);

    OrderResponseDto removeTicketFromOrder(UUID id, UUID ticketId);

    OrderResponseDto softDeleteOrderById(UUID id);

    void deleteOrderById(UUID id);
}
