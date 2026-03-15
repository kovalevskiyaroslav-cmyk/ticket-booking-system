package com.yaroslav.ticket_booking_system.service.impl;

import com.yaroslav.ticket_booking_system.cache.QueryCacheService;
import com.yaroslav.ticket_booking_system.cache.QueryKey;
import com.yaroslav.ticket_booking_system.dto.OrderRequestDto;
import com.yaroslav.ticket_booking_system.dto.OrderResponseDto;
import com.yaroslav.ticket_booking_system.dto.OrderUpdateDto;
import com.yaroslav.ticket_booking_system.dto.TicketRequestDto;
import com.yaroslav.ticket_booking_system.exception.EventNotFoundException;
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

    public static final String ORDER_BY_ID = "getOrderById";
    public static final String ORDERS_BY_STATUS = "getOrdersByStatus";
    public static final String ORDERS_BY_DELETED = "getOrdersByDeleted";
    public static final String ORDERS_BY_COMPLETED_AT = "getOrdersByCompletedAtBetween";
    public static final String ALL_ORDERS = "getAllOrders";
    public static final String ORDERS_BY_VENUE = "getOrdersByVenue";
    public static final String CACHE_HIT = "[CACHE HIT] ";
    private static final String LOG_FORMAT = "{} {}: {}";

    @Override
    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto requestDto) {

        final User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new UserNotFoundException(requestDto.getUserId()));

        final Order order = new Order(user);
        final Order savedOrder = orderRepository.save(order);

        if (requestDto.getTicketDtos() != null && !requestDto.getTicketDtos().isEmpty()) {
            for (TicketRequestDto ticketDto : requestDto.getTicketDtos()) {

                final Event event = eventRepository.findById(ticketDto.getEventId())
                        .orElseThrow(() -> new EventNotFoundException(ticketDto.getEventId()));

                final Seat seat = seatRepository.findById(ticketDto.getSeatId())
                        .orElseThrow(() -> new SeatNotFoundException(ticketDto.getSeatId()));

                final boolean seatTaken = ticketRepository.existsByEventIdAndSeatId(
                        ticketDto.getEventId(),
                        ticketDto.getSeatId()
                );

                if (seatTaken) {
                    throw new IllegalStateException(
                            "Seat " + seat.getNumber() + " is already reserved for event: " + event.getName()
                    );
                }

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

        cacheService.evict(new QueryKey(ORDERS_BY_STATUS, OrderStatus.CREATED));
        cacheService.evict(new QueryKey(ORDERS_BY_DELETED, false));
        cacheService.evictByPattern(ALL_ORDERS);
        cacheService.evictByPattern(ORDERS_BY_VENUE);

        return createdOrder;
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDto getOrderById(UUID id) {

        final QueryKey key = new QueryKey(ORDER_BY_ID, id);

        if (cacheService.containsKey(key)) {
            log.info(LOG_FORMAT, CACHE_HIT, ORDER_BY_ID, id);
            return cacheService.get(key, OrderResponseDto.class);
        }

        final Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        final OrderResponseDto dto = orderMapper.toDto(order);
        cacheService.put(key, dto);

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getOrdersByStatus(OrderStatus orderStatus) {

        final QueryKey key = new QueryKey(ORDERS_BY_STATUS, orderStatus);

        if (cacheService.containsKey(key)) {
            log.info(LOG_FORMAT, CACHE_HIT, ORDERS_BY_STATUS, orderStatus);
            return cacheService.getList(key, OrderResponseDto.class);
        }

        final List<OrderResponseDto> orders = orderRepository.findByStatus(orderStatus)
                .stream()
                .map(orderMapper::toDto)
                .toList();

        cacheService.put(key, orders);

        return orders;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getOrdersByDeleted(Boolean deleted) {

        final QueryKey key = new QueryKey(ORDERS_BY_DELETED, deleted);

        if (cacheService.containsKey(key)) {
            log.info(LOG_FORMAT, CACHE_HIT, ORDERS_BY_DELETED, deleted);
            return cacheService.getList(key, OrderResponseDto.class);
        }

        final List<OrderResponseDto> orders = orderRepository.findByDeleted(deleted)
                .stream()
                .map(orderMapper::toDto)
                .toList();

        cacheService.put(key, orders);

        return orders;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getOrdersByCompletedAtBetween(LocalDateTime start, LocalDateTime end) {

        final QueryKey key = new QueryKey(ORDERS_BY_COMPLETED_AT, start, end);

        if (cacheService.containsKey(key)) {
            log.info("{} {}: {} to {}", CACHE_HIT, ORDERS_BY_COMPLETED_AT, start, end);
            return cacheService.getList(key, OrderResponseDto.class);
        }

        final List<OrderResponseDto> orders = orderRepository.findByCompletedAtBetween(start, end)
                .stream()
                .map(orderMapper::toDto)
                .toList();

        cacheService.put(key, orders);

        return orders;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getAllOrders() {

        final QueryKey key = new QueryKey(ALL_ORDERS);

        if (cacheService.containsKey(key)) {
            log.info("{} {}", CACHE_HIT, ALL_ORDERS);
            return cacheService.getList(key, OrderResponseDto.class);
        }

        final List<OrderResponseDto> orders = orderRepository.findAll()
                .stream()
                .map(orderMapper::toDto)
                .toList();

        cacheService.put(key, orders);

        return orders;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponseDto> getOrdersByVenue(UUID venueId, Pageable pageable) {

        final QueryKey key = new QueryKey(
                ORDERS_BY_VENUE,
                venueId,
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort()
        );

        if (cacheService.containsKey(key)) {
            log.info("{} {}: {} page {}", CACHE_HIT, ORDERS_BY_VENUE, venueId, pageable.getPageNumber());
            return cacheService.getPage(key, OrderResponseDto.class);
        }

        final Page<OrderResponseDto> orders = orderRepository.findOrdersByVenueId(venueId, pageable)
                .map(orderMapper::toDto);

        cacheService.put(key, orders);

        return orders;
    }

    @Override
    @Transactional
    public OrderResponseDto updateOrderById(UUID id, OrderUpdateDto updateDto) {

        final Order order = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));

        if (updateDto.getStatus() != null) {
            final OrderStatus oldStatus = order.getStatus();
            order.setStatus(updateDto.getStatus());

            cacheService.evict(new QueryKey(ORDERS_BY_STATUS, oldStatus));
            cacheService.evict(new QueryKey(ORDERS_BY_STATUS, updateDto.getStatus()));
        }
        if (updateDto.getCompletedAt() != null) {
            order.setCompletedAt(updateDto.getCompletedAt());

            cacheService.evictByPattern(ORDERS_BY_COMPLETED_AT);
        }

        final OrderResponseDto updatedOrder = orderMapper.toDto(order);

        cacheService.evict(new QueryKey(ORDER_BY_ID, id));
        cacheService.evict(new QueryKey(ORDERS_BY_DELETED, order.getDeleted()));
        cacheService.evictByPattern(ALL_ORDERS);
        cacheService.evictByPattern(ORDERS_BY_VENUE);

        return updatedOrder;
    }

    @Override
    @Transactional
    public OrderResponseDto addTicketToOrder(UUID id, TicketRequestDto requestDto) {

        final Order order = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));

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

        return getOrderResponseDtoWithCacheEviction(id, order);
    }

    @Override
    @Transactional
    public OrderResponseDto removeTicketFromOrder(UUID id, UUID ticketId) {

        final Order order = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));

        final Ticket ticket = order.getTickets().stream()
                .filter(t -> t.getId().equals(ticketId))
                .findFirst()
                .orElseThrow(() -> new TicketNotFoundException(ticketId));

        order.removeTicket(ticket);

        return getOrderResponseDtoWithCacheEviction(id, order);
    }

    private OrderResponseDto getOrderResponseDtoWithCacheEviction(UUID id, Order order) {
        final Order savedOrder = orderRepository.save(order);
        final OrderResponseDto updatedOrder = orderMapper.toDto(savedOrder);

        cacheService.evict(new QueryKey(ORDER_BY_ID, id));
        cacheService.evict(new QueryKey(ORDERS_BY_STATUS, savedOrder.getStatus()));
        cacheService.evict(new QueryKey(ORDERS_BY_DELETED, savedOrder.getDeleted()));
        cacheService.evictByPattern(ALL_ORDERS);
        cacheService.evictByPattern(ORDERS_BY_COMPLETED_AT);
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

        cacheService.evict(new QueryKey(ORDER_BY_ID, id));
        cacheService.evict(new QueryKey(ORDERS_BY_STATUS, order.getStatus()));
        cacheService.evictByPattern(ORDERS_BY_DELETED);
        cacheService.evictByPattern(ALL_ORDERS);
        cacheService.evictByPattern(ORDERS_BY_COMPLETED_AT);
        cacheService.evictByPattern(ORDERS_BY_VENUE);

        return updatedOrder;
    }

    @Override
    @Transactional
    public void deleteOrderById(UUID id) {

        final Order order = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));

        final OrderStatus orderStatus = order.getStatus();
        final Boolean orderDeleted = order.getDeleted();

        orderRepository.delete(order);

        cacheService.evict(new QueryKey(ORDER_BY_ID, id));
        cacheService.evict(new QueryKey(ORDERS_BY_STATUS, orderStatus));
        cacheService.evict(new QueryKey(ORDERS_BY_DELETED, orderDeleted));
        cacheService.evictByPattern(ALL_ORDERS);
        cacheService.evictByPattern(ORDERS_BY_COMPLETED_AT);
        cacheService.evictByPattern(ORDERS_BY_VENUE);
    }
}