package com.yaroslav.ticket_booking_system.service.impl;

import com.yaroslav.ticket_booking_system.dto.OrderRequestDto;
import com.yaroslav.ticket_booking_system.dto.OrderResponseDto;
import com.yaroslav.ticket_booking_system.dto.OrderUpdateDto;
import com.yaroslav.ticket_booking_system.dto.TicketRequestDto;
import com.yaroslav.ticket_booking_system.exception.OrderNotFoundException;
import com.yaroslav.ticket_booking_system.exception.TicketNotFoundException;
import com.yaroslav.ticket_booking_system.exception.UserNotFoundException;
import com.yaroslav.ticket_booking_system.mapper.OrderMapper;
import com.yaroslav.ticket_booking_system.mapper.TicketMapper;
import com.yaroslav.ticket_booking_system.model.Order;
import com.yaroslav.ticket_booking_system.model.OrderStatus;
import com.yaroslav.ticket_booking_system.model.Ticket;
import com.yaroslav.ticket_booking_system.repository.OrderRepository;
import com.yaroslav.ticket_booking_system.repository.UserRepository;
import com.yaroslav.ticket_booking_system.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;
    private final TicketMapper ticketMapper;

    @Override
    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto requestDto) {

        final Order order = orderMapper.toEntity(requestDto);
        order.setDateTime(LocalDateTime.now());
        order.setDeleted(false);
        order.setStatus(OrderStatus.CREATED);
        order.setTotalPrice(BigDecimal.ZERO);
        order.setUser(userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new UserNotFoundException(requestDto.getUserId())));

        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDto getOrderById(UUID id) {

        final Order order = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));

        return orderMapper.toDto(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getOrdersByStatus(OrderStatus orderStatus) {

        return orderRepository.findByStatus(orderStatus)
                .stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getOrdersByDeleted(Boolean deleted) {

        return orderRepository.findByDeleted(deleted)
                .stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getOrdersByDateTimeBetween(LocalDateTime dateTimeBefore, LocalDateTime dateTimeAfter) {

        return orderRepository.findByDateTimeBetween(dateTimeBefore, dateTimeAfter)
                .stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public OrderResponseDto updateOrderById(UUID id, OrderUpdateDto updateDto) {

        final Order order = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
        order.setStatus(updateDto.getStatus());

        return orderMapper.toDto(order);
    }

    @Override
    @Transactional
    public OrderResponseDto addTicketToOrder(UUID id, TicketRequestDto requestDto) {

        final Order order = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));

        final Ticket ticket = ticketMapper.toEntity(requestDto);

        order.addTicket(ticket);

        return orderMapper.toDto(order);
    }

    @Override
    @Transactional
    public OrderResponseDto removeTicketFromOrder(UUID id, UUID ticketId) {

        final Order order = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));

        final Ticket ticket = order.getTickets().stream()
                .filter(t -> t.getId().equals(ticketId))
                .findFirst()
                .orElseThrow(() -> new TicketNotFoundException(id));

        order.removeTicket(ticket);

        return orderMapper.toDto(order);
    }

    @Override
    @Transactional
    public OrderResponseDto softDeleteOrderById(UUID id) {

        final Order order = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
        order.setDeleted(true);

        return orderMapper.toDto(order);
    }

    @Override
    @Transactional
    public void deleteOrderById(UUID id) {

        final Order order = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
        orderRepository.delete(order);
    }
}
