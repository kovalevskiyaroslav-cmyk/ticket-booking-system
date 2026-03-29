package com.yaroslav.ticket_booking_system.service.impl;

import com.yaroslav.ticket_booking_system.cache.QueryCacheService;
import com.yaroslav.ticket_booking_system.cache.QueryKey;
import com.yaroslav.ticket_booking_system.dto.OrderRequestDto;
import com.yaroslav.ticket_booking_system.dto.OrderResponseDto;
import com.yaroslav.ticket_booking_system.dto.OrderUpdateDto;
import com.yaroslav.ticket_booking_system.dto.TicketRequestDto;
import com.yaroslav.ticket_booking_system.exception.DuplicateTicketException;
import com.yaroslav.ticket_booking_system.exception.EventNotFoundException;
import com.yaroslav.ticket_booking_system.exception.InvalidOrderStatusTransitionException;
import com.yaroslav.ticket_booking_system.exception.OrderAlreadyDeletedException;
import com.yaroslav.ticket_booking_system.exception.OrderNotFoundException;
import com.yaroslav.ticket_booking_system.exception.SeatNotFoundException;
import com.yaroslav.ticket_booking_system.exception.TicketNotFoundException;
import com.yaroslav.ticket_booking_system.exception.UserNotFoundException;
import com.yaroslav.ticket_booking_system.mapper.OrderMapper;
import com.yaroslav.ticket_booking_system.model.Event;
import com.yaroslav.ticket_booking_system.model.Order;
import com.yaroslav.ticket_booking_system.model.OrderStatus;
import com.yaroslav.ticket_booking_system.model.Payment;
import com.yaroslav.ticket_booking_system.model.PaymentStatus;
import com.yaroslav.ticket_booking_system.model.Seat;
import com.yaroslav.ticket_booking_system.model.Ticket;
import com.yaroslav.ticket_booking_system.model.User;
import com.yaroslav.ticket_booking_system.repository.EventRepository;
import com.yaroslav.ticket_booking_system.repository.OrderRepository;
import com.yaroslav.ticket_booking_system.repository.SeatRepository;
import com.yaroslav.ticket_booking_system.repository.TicketRepository;
import com.yaroslav.ticket_booking_system.repository.UserRepository;
import com.yaroslav.ticket_booking_system.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;
    private final TicketRepository ticketRepository;
    private final SeatRepository seatRepository;
    private final EventRepository eventRepository;
    private final QueryCacheService cacheService;

    public static final String ORDERS_BY_VENUE = "getOrdersByVenue";
    public static final String CACHE_HIT = "[CACHE HIT] ";

    @Override
    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto requestDto) {

        final User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new UserNotFoundException(requestDto.getUserId()));

        final Order order = new Order(user);
        final Order savedOrder = orderRepository.save(order);

        if (requestDto.getTicketDtos() != null && !requestDto.getTicketDtos().isEmpty()) {
            for (TicketRequestDto ticketDto : requestDto.getTicketDtos()) {

                final boolean seatTaken = ticketRepository.existsByEventIdAndSeatId(
                        ticketDto.getEventId(),
                        ticketDto.getSeatId()
                );
                if (seatTaken) {
                    throw new DuplicateTicketException(ticketDto.getEventId(), ticketDto.getSeatId());
                }

                final Event event = eventRepository.findById(ticketDto.getEventId())
                        .orElseThrow(() -> new EventNotFoundException(ticketDto.getEventId()));

                final Seat seat = seatRepository.findById(ticketDto.getSeatId())
                        .orElseThrow(() -> new SeatNotFoundException(ticketDto.getSeatId()));

                final Ticket ticket = new Ticket();
                ticket.setEvent(event);
                ticket.setSeat(seat);
                ticket.setPrice(seat.getPrice());

                savedOrder.addTicket(ticket);

                ticketRepository.save(ticket);
            }
        }

        final Payment payment = new Payment();
        payment.setAmount(order.getTotalPrice());
        payment.setStatus(PaymentStatus.PENDING);
        order.setPayment(payment);

        final OrderResponseDto createdOrder = orderMapper.toDto(orderRepository.save(savedOrder));

        cacheService.evictByPattern(ORDERS_BY_VENUE);

        return createdOrder;
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDto getOrderById(UUID id) {

        final Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

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
    public List<OrderResponseDto> getOrdersByCompletedAtBetween(LocalDateTime start, LocalDateTime end) {

        return orderRepository.findByCompletedAtBetween(start, end)
                .stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getAllOrders() {

        return orderRepository.findAll()
                .stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponseDto> getOrdersByVenueName(String name, Pageable pageable) {

        final QueryKey key = new QueryKey(
                ORDERS_BY_VENUE,
                name,
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort()
        );

        if (cacheService.containsKey(key)) {
            log.info("{} {}: {} page {}", CACHE_HIT, ORDERS_BY_VENUE, name, pageable.getPageNumber());
            return cacheService.getPage(key, OrderResponseDto.class);
        }

        final Page<OrderResponseDto> orders = orderRepository.findOrdersByVenueName(name, pageable)
                .map(orderMapper::toDto);

        cacheService.put(key, orders);

        return orders;
    }

    @Override
    @Transactional
    public OrderResponseDto updateOrderById(UUID id, OrderUpdateDto updateDto) {

        final Order order = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
        if (order.isDeleted()) {
            throw new OrderAlreadyDeletedException(id);
        }

        if (updateDto.getStatus() != null) {
            if (order.getStatus().cannotTransitionTo(updateDto.getStatus())) {
                throw new InvalidOrderStatusTransitionException(order.getStatus(), updateDto.getStatus());
            }

            order.setStatus(updateDto.getStatus());
        }
        if (updateDto.getCompletedAt() != null) {
            order.setCompletedAt(updateDto.getCompletedAt());
        }

        final OrderResponseDto updatedOrder = orderMapper.toDto(order);

        cacheService.evictByPattern(ORDERS_BY_VENUE);

        return updatedOrder;
    }

    @Override
    @Transactional
    public OrderResponseDto addTicketToOrder(UUID id, TicketRequestDto requestDto) {

        final Order order = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
        if (order.isDeleted()) {
            throw new OrderAlreadyDeletedException(id);
        }

        final Event event = eventRepository.findById(requestDto.getEventId())
                .orElseThrow(() -> new EventNotFoundException(requestDto.getEventId()));

        final Seat seat = seatRepository.findById(requestDto.getSeatId())
                .orElseThrow(() -> new SeatNotFoundException(requestDto.getSeatId()));

        final boolean seatTaken = ticketRepository.existsByEventIdAndSeatId(
                requestDto.getEventId(),
                requestDto.getSeatId()
        );

        if (seatTaken) {
            throw new IllegalStateException("Seat is already reserved for this event");
        }

        final Ticket ticket = new Ticket();
        ticket.setEvent(event);
        ticket.setSeat(seat);
        ticket.setPrice(seat.getPrice());

        order.addTicket(ticket);

        return getOrderResponseDtoWithCacheEviction(order);
    }

    @Override
    @Transactional
    public OrderResponseDto removeTicketFromOrder(UUID id, UUID ticketId) {

        final Order order = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
        if (order.isDeleted()) {
            throw new OrderAlreadyDeletedException(id);
        }

        final Ticket ticket = order.getTickets().stream()
                .filter(t -> t.getId().equals(ticketId))
                .findFirst()
                .orElseThrow(() -> new TicketNotFoundException(ticketId));

        order.removeTicket(ticket);

        return getOrderResponseDtoWithCacheEviction(order);
    }

    private OrderResponseDto getOrderResponseDtoWithCacheEviction(Order order) {
        final Order savedOrder = orderRepository.save(order);
        final OrderResponseDto updatedOrder = orderMapper.toDto(savedOrder);

        cacheService.evictByPattern(ORDERS_BY_VENUE);

        return updatedOrder;
    }

    @Override
    @Transactional
    public OrderResponseDto softDeleteOrderById(UUID id) {

        final Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        order.setDeleted(true);

        final OrderResponseDto updatedOrder = orderMapper.toDto(order);

        cacheService.evictByPattern(ORDERS_BY_VENUE);

        return updatedOrder;
    }

    @Override
    @Transactional
    public void deleteOrderById(UUID id) {

        final Order order = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));

        orderRepository.delete(order);

        cacheService.evictByPattern(ORDERS_BY_VENUE);
    }
}