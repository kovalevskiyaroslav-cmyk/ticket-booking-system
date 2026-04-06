package com.yaroslav.ticket_booking_system;

import com.yaroslav.ticket_booking_system.cache.QueryCacheService;
import com.yaroslav.ticket_booking_system.cache.QueryKey;
import com.yaroslav.ticket_booking_system.dto.OrderRequestDto;
import com.yaroslav.ticket_booking_system.dto.OrderResponseDto;
import com.yaroslav.ticket_booking_system.dto.OrderUpdateDto;
import com.yaroslav.ticket_booking_system.dto.TicketRequestDto;
import com.yaroslav.ticket_booking_system.exception.DuplicateTicketException;
import com.yaroslav.ticket_booking_system.exception.EventNotFoundException;
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
import com.yaroslav.ticket_booking_system.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OrderServiceTest {

    private OrderService orderService;
    private OrderRepository orderRepository;
    private UserRepository userRepository;
    private OrderMapper orderMapper;
    private TicketRepository ticketRepository;
    private SeatRepository seatRepository;
    private EventRepository eventRepository;
    private QueryCacheService cacheService;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        userRepository = mock(UserRepository.class);
        orderMapper = mock(OrderMapper.class);
        ticketRepository = mock(TicketRepository.class);
        seatRepository = mock(SeatRepository.class);
        eventRepository = mock(EventRepository.class);
        cacheService = mock(QueryCacheService.class);

        orderService = new OrderServiceImpl(
                orderRepository,
                userRepository,
                orderMapper,
                ticketRepository,
                seatRepository,
                eventRepository,
                cacheService
        );
    }

    private UUID sampleUserId() {
        return UUID.fromString("2bee94e4-e13c-42b6-bcd6-4c418decfc30");
    }

    private UUID sampleEventId() {
        return UUID.fromString("ae6e48c9-c229-4d28-bf3c-82b6d4310d28");
    }

    private UUID sampleSeatId() {
        return UUID.fromString("8d7c6b5a-4f3e-2d1c-0b9a-8f7e6d5c4b3a");
    }

    private UUID sampleOrderId() {
        return UUID.fromString("83ba4bbd-9588-497b-a04a-1036edc3e0a2");
    }

    private UUID sampleTicketId() {
        return UUID.fromString("a6aae4f0-da7d-453f-97c0-d7cfbad11f19");
    }

    private User sampleUser() {
        final User user = new User();
        user.setId(sampleUserId());
        user.setName("Alice Johnson");
        user.setEmail("alice.johnson@example.com");
        return user;
    }

    private Event sampleEvent() {
        final Event event = new Event();
        event.setId(sampleEventId());
        event.setName("Legends of Rock Live");
        return event;
    }

    private Seat sampleSeat() {
        final Seat seat = new Seat();
        seat.setId(sampleSeatId());
        seat.setNumber(7);
        seat.setSection(1);
        seat.setPrice(new BigDecimal("110.00"));
        return seat;
    }

    private Order sampleOrder() {
        final Order order = new Order(sampleUser());
        order.setId(sampleOrderId());
        order.setStatus(OrderStatus.CREATED);
        order.setTotalPrice(new BigDecimal("110.00"));
        order.setDeleted(false);
        return order;
    }

    private void samplePayment() {
        final Payment payment = new Payment();
        payment.setAmount(new BigDecimal("110.00"));
        payment.setStatus(PaymentStatus.PENDING);
    }

    private Ticket sampleTicket() {
        final Ticket ticket = new Ticket();
        ticket.setId(sampleTicketId());
        ticket.setEvent(sampleEvent());
        ticket.setSeat(sampleSeat());
        ticket.setPrice(new BigDecimal("110.00"));
        return ticket;
    }

    private TicketRequestDto sampleTicketRequestDto() {
        final TicketRequestDto dto = new TicketRequestDto();
        dto.setEventId(sampleEventId());
        dto.setSeatId(sampleSeatId());
        return dto;
    }

    private OrderRequestDto sampleOrderRequestDto() {
        final OrderRequestDto dto = new OrderRequestDto();
        dto.setUserId(sampleUserId());
        dto.setTicketDtos(List.of(sampleTicketRequestDto()));
        return dto;
    }

    private OrderResponseDto sampleOrderResponseDto() {
        final OrderResponseDto dto = new OrderResponseDto();
        dto.setId(sampleOrderId());
        dto.setStatus(OrderStatus.CREATED);
        dto.setTotalPrice(new BigDecimal("110.00"));
        dto.setUserId(sampleUserId());
        return dto;
    }

    @Test
    void createOrderSuccess() {
        final OrderRequestDto request = sampleOrderRequestDto();
        final User user = sampleUser();
        new Order(user);
        final Order savedOrder = sampleOrder();
        samplePayment();
        final OrderResponseDto response = sampleOrderResponseDto();

        when(userRepository.findById(sampleUserId())).thenReturn(Optional.of(user));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(ticketRepository.existsByEventIdAndSeatId(sampleEventId(), sampleSeatId())).thenReturn(false);
        when(eventRepository.findById(sampleEventId())).thenReturn(Optional.of(sampleEvent()));
        when(seatRepository.findById(sampleSeatId())).thenReturn(Optional.of(sampleSeat()));
        when(orderRepository.save(savedOrder)).thenReturn(savedOrder);
        when(orderMapper.toDto(savedOrder)).thenReturn(response);

        final OrderResponseDto result = orderService.createOrder(request);

        assertThat(result).isEqualTo(response);
        verify(cacheService).evictByPattern(OrderServiceImpl.ORDERS_BY_VENUE);
    }

    @Test
    void createOrderUserNotFound() {
        final OrderRequestDto request = sampleOrderRequestDto();
        when(userRepository.findById(sampleUserId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.createOrder(request))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void createOrderDuplicateTicket() {
        final OrderRequestDto request = sampleOrderRequestDto();
        final User user = sampleUser();

        when(userRepository.findById(sampleUserId())).thenReturn(Optional.of(user));
        when(orderRepository.save(any(Order.class))).thenReturn(sampleOrder());
        when(ticketRepository.existsByEventIdAndSeatId(sampleEventId(), sampleSeatId())).thenReturn(true);

        assertThatThrownBy(() -> orderService.createOrder(request))
                .isInstanceOf(DuplicateTicketException.class);
    }

    @Test
    void createOrderEventNotFound() {
        final OrderRequestDto request = sampleOrderRequestDto();
        final User user = sampleUser();

        when(userRepository.findById(sampleUserId())).thenReturn(Optional.of(user));
        when(orderRepository.save(any(Order.class))).thenReturn(sampleOrder());
        when(ticketRepository.existsByEventIdAndSeatId(sampleEventId(), sampleSeatId())).thenReturn(false);
        when(eventRepository.findById(sampleEventId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.createOrder(request))
                .isInstanceOf(EventNotFoundException.class);
    }

    @Test
    void createOrderSeatNotFound() {
        final OrderRequestDto request = sampleOrderRequestDto();
        final User user = sampleUser();

        when(userRepository.findById(sampleUserId())).thenReturn(Optional.of(user));
        when(orderRepository.save(any(Order.class))).thenReturn(sampleOrder());
        when(ticketRepository.existsByEventIdAndSeatId(sampleEventId(), sampleSeatId())).thenReturn(false);
        when(eventRepository.findById(sampleEventId())).thenReturn(Optional.of(sampleEvent()));
        when(seatRepository.findById(sampleSeatId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.createOrder(request))
                .isInstanceOf(SeatNotFoundException.class);
    }

    @Test
    void createOrdersBulkSuccess() {
        final List<OrderRequestDto> requests = List.of(sampleOrderRequestDto(), sampleOrderRequestDto());
        final User user = sampleUser();
        final Order savedOrder = sampleOrder();
        final OrderResponseDto response = sampleOrderResponseDto();

        when(userRepository.findById(sampleUserId())).thenReturn(Optional.of(user));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(ticketRepository.existsByEventIdAndSeatId(any(), any())).thenReturn(false);
        when(eventRepository.findById(any())).thenReturn(Optional.of(sampleEvent()));
        when(seatRepository.findById(any())).thenReturn(Optional.of(sampleSeat()));
        when(orderRepository.save(savedOrder)).thenReturn(savedOrder);
        when(orderMapper.toDto(savedOrder)).thenReturn(response);

        final List<OrderResponseDto> result = orderService.createOrdersBulk(requests);

        assertThat(result).hasSize(2);
        assertThat(result.get(0)).isEqualTo(response);
        assertThat(result.get(1)).isEqualTo(response);
        verify(cacheService).evictByPattern(OrderServiceImpl.ORDERS_BY_VENUE);
    }

    @Test
    void createOrdersBulkEmptyList() {
        List<OrderResponseDto> result = orderService.createOrdersBulk(null);
        assertThat(result).isEmpty();

        result = orderService.createOrdersBulk(Collections.emptyList());
        assertThat(result).isEmpty();

        verify(cacheService, never()).evictByPattern(anyString());
    }

    @Test
    void createOrdersBulkSecondOrderFailsWithDuplicateTicket() {
        final List<OrderRequestDto> requests = List.of(sampleOrderRequestDto(), sampleOrderRequestDto());
        final User user = sampleUser();

        when(userRepository.findById(sampleUserId())).thenReturn(Optional.of(user));
        when(orderRepository.save(any(Order.class))).thenReturn(sampleOrder());
        when(ticketRepository.existsByEventIdAndSeatId(any(), any()))
                .thenReturn(false)
                .thenReturn(true);
        when(eventRepository.findById(any())).thenReturn(Optional.of(sampleEvent()));
        when(seatRepository.findById(any())).thenReturn(Optional.of(sampleSeat()));

        assertThatThrownBy(() -> orderService.createOrdersBulk(requests))
                .isInstanceOf(DuplicateTicketException.class);
    }

    @Test
    void getOrderByIdSuccess() {
        final Order order = sampleOrder();
        final OrderResponseDto response = sampleOrderResponseDto();

        when(orderRepository.findById(sampleOrderId())).thenReturn(Optional.of(order));
        when(orderMapper.toDto(order)).thenReturn(response);

        final OrderResponseDto result = orderService.getOrderById(sampleOrderId());

        assertThat(result).isEqualTo(response);
    }

    @Test
    void getOrderByIdNotFound() {
        final UUID orderId = sampleOrderId();

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrderById(orderId))
                .isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    void getOrdersByStatusSuccess() {
        final Order order = sampleOrder();
        final OrderResponseDto response = sampleOrderResponseDto();
        final List<Order> orders = List.of(order);

        when(orderRepository.findByStatus(OrderStatus.CREATED)).thenReturn(orders);
        when(orderMapper.toDto(order)).thenReturn(response);

        final List<OrderResponseDto> result = orderService.getOrdersByStatus(OrderStatus.CREATED);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(response);
    }

    @Test
    void getOrdersByStatusEmpty() {
        when(orderRepository.findByStatus(OrderStatus.CREATED)).thenReturn(Collections.emptyList());

        final List<OrderResponseDto> result = orderService.getOrdersByStatus(OrderStatus.CREATED);

        assertThat(result).isEmpty();
    }

    @Test
    void getOrdersByDeletedSuccess() {
        final Order order = sampleOrder();
        final OrderResponseDto response = sampleOrderResponseDto();
        final List<Order> orders = List.of(order);

        when(orderRepository.findByDeleted(false)).thenReturn(orders);
        when(orderMapper.toDto(order)).thenReturn(response);

        final List<OrderResponseDto> result = orderService.getOrdersByDeleted(false);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(response);
    }

    @Test
    void getOrdersByCompletedAtBetweenSuccess() {
        final LocalDateTime start = LocalDateTime.of(2026, 3, 1, 0, 0);
        final LocalDateTime end = LocalDateTime.of(2026, 3, 31, 23, 59);
        final Order order = sampleOrder();
        final OrderResponseDto response = sampleOrderResponseDto();
        final List<Order> orders = List.of(order);

        when(orderRepository.findByCompletedAtBetween(start, end)).thenReturn(orders);
        when(orderMapper.toDto(order)).thenReturn(response);

        final List<OrderResponseDto> result = orderService.getOrdersByCompletedAtBetween(start, end);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(response);
    }

    @Test
    void getAllOrdersSuccess() {
        final Order order = sampleOrder();
        final OrderResponseDto response = sampleOrderResponseDto();
        final List<Order> orders = List.of(order);

        when(orderRepository.findAll()).thenReturn(orders);
        when(orderMapper.toDto(order)).thenReturn(response);

        final List<OrderResponseDto> result = orderService.getAllOrders();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(response);
    }

    @Test
    void getOrdersByVenueNameCacheHit() {
        final String venueName = "Grand City Concert Hall";
        final Pageable pageable = PageRequest.of(0, 10);
        final QueryKey key = new QueryKey(
                OrderServiceImpl.ORDERS_BY_VENUE,
                venueName,
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort()
        );
        final Page<OrderResponseDto> cachedPage = new PageImpl<>(List.of(sampleOrderResponseDto()));

        when(cacheService.containsKey(key)).thenReturn(true);
        when(cacheService.getPage(key, OrderResponseDto.class)).thenReturn(cachedPage);

        final Page<OrderResponseDto> result = orderService.getOrdersByVenueName(venueName, pageable);

        assertThat(result).isEqualTo(cachedPage);
        verify(orderRepository, never()).findOrdersByVenueName(anyString(), any());
    }

    @Test
    void getOrdersByVenueNameCacheMiss() {
        final String venueName = "Grand City Concert Hall";
        final Pageable pageable = PageRequest.of(0, 10);
        final Order order = sampleOrder();
        final OrderResponseDto response = sampleOrderResponseDto();
        final Page<Order> orderPage = new PageImpl<>(List.of(order));
        new PageImpl<>(List.of(response));

        when(cacheService.containsKey(any(QueryKey.class))).thenReturn(false);
        when(orderRepository.findOrdersByVenueName(venueName, pageable)).thenReturn(orderPage);
        when(orderMapper.toDto(order)).thenReturn(response);

        final Page<OrderResponseDto> result = orderService.getOrdersByVenueName(venueName, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst()).isEqualTo(response);
        verify(cacheService).put(any(QueryKey.class), any(Page.class));
    }

    @Test
    void updateOrderByIdSuccess() {
        final OrderUpdateDto updateDto = new OrderUpdateDto();
        updateDto.setStatus(OrderStatus.PAID);
        updateDto.setCompletedAt(LocalDateTime.now());

        final Order order = sampleOrder();
        final OrderResponseDto response = sampleOrderResponseDto();
        response.setStatus(OrderStatus.PAID);

        when(orderRepository.findById(sampleOrderId())).thenReturn(Optional.of(order));
        when(orderMapper.toDto(order)).thenReturn(response);

        final OrderResponseDto result = orderService.updateOrderById(sampleOrderId(), updateDto);

        assertThat(result.getStatus()).isEqualTo(OrderStatus.PAID);
        verify(cacheService).evictByPattern(OrderServiceImpl.ORDERS_BY_VENUE);
    }

    @Test
    void updateOrderByIdNotFound() {
        final OrderUpdateDto updateDto = new OrderUpdateDto();
        updateDto.setStatus(OrderStatus.PAID);

        final UUID orderId = sampleOrderId();

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrderById(orderId))
                .isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    void updateOrderByIdAlreadyDeleted() {
        final OrderUpdateDto updateDto = new OrderUpdateDto();
        updateDto.setStatus(OrderStatus.PAID);

        final Order order = sampleOrder();
        order.setDeleted(true);

        final UUID orderId = sampleOrderId();

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrderById(orderId))
                .isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    void updateOrderByIdInvalidStatusTransition() {
        final OrderUpdateDto updateDto = new OrderUpdateDto();
        updateDto.setStatus(OrderStatus.CANCELLED);

        final Order order = sampleOrder();
        order.setStatus(OrderStatus.PAID);

        final UUID orderId = sampleOrderId();

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrderById(orderId))
                .isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    void addTicketToOrderSuccess() {
        final TicketRequestDto ticketDto = sampleTicketRequestDto();
        final Order order = sampleOrder();
        final Event event = sampleEvent();
        final Seat seat = sampleSeat();
        final Order savedOrder = sampleOrder();
        final OrderResponseDto response = sampleOrderResponseDto();

        when(orderRepository.findById(sampleOrderId())).thenReturn(Optional.of(order));
        when(eventRepository.findById(sampleEventId())).thenReturn(Optional.of(event));
        when(seatRepository.findById(sampleSeatId())).thenReturn(Optional.of(seat));
        when(ticketRepository.existsByEventIdAndSeatId(sampleEventId(), sampleSeatId())).thenReturn(false);
        when(orderRepository.save(order)).thenReturn(savedOrder);
        when(orderMapper.toDto(savedOrder)).thenReturn(response);

        final OrderResponseDto result = orderService.addTicketToOrder(sampleOrderId(), ticketDto);

        assertThat(result).isEqualTo(response);
        verify(cacheService).evictByPattern(OrderServiceImpl.ORDERS_BY_VENUE);
    }

    @Test
    void addTicketToOrderOrderNotFound() {
        final TicketRequestDto ticketDto = sampleTicketRequestDto();
        final UUID orderId = sampleOrderId();

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.addTicketToOrder(orderId, ticketDto))
                .isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    void addTicketToOrderOrderDeleted() {
        final TicketRequestDto ticketDto = sampleTicketRequestDto();
        final Order order = sampleOrder();
        order.setDeleted(true);
        final UUID orderId = sampleOrderId();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.addTicketToOrder(orderId, ticketDto))
                .isInstanceOf(OrderAlreadyDeletedException.class);
    }

    @Test
    void addTicketToOrderSeatAlreadyTaken() {
        final TicketRequestDto ticketDto = sampleTicketRequestDto();
        final Order order = sampleOrder();
        final UUID orderId = sampleOrderId();

        when(orderRepository.findById(sampleOrderId())).thenReturn(Optional.of(order));
        when(eventRepository.findById(sampleEventId())).thenReturn(Optional.of(sampleEvent()));
        when(seatRepository.findById(sampleSeatId())).thenReturn(Optional.of(sampleSeat()));
        when(ticketRepository.existsByEventIdAndSeatId(sampleEventId(), sampleSeatId())).thenReturn(true);

        assertThatThrownBy(() -> orderService.addTicketToOrder(orderId, ticketDto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Seat is already reserved");
    }

    @Test
    void removeTicketFromOrderSuccess() {
        final Order order = sampleOrder();
        final Ticket ticket = sampleTicket();
        order.addTicket(ticket);
        final Order savedOrder = sampleOrder();
        final OrderResponseDto response = sampleOrderResponseDto();

        when(orderRepository.findById(sampleOrderId())).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(savedOrder);
        when(orderMapper.toDto(savedOrder)).thenReturn(response);

        final OrderResponseDto result = orderService.removeTicketFromOrder(sampleOrderId(), sampleTicketId());

        assertThat(result).isEqualTo(response);
        verify(cacheService).evictByPattern(OrderServiceImpl.ORDERS_BY_VENUE);
    }

    @Test
    void removeTicketFromOrderOrderNotFound() {
        final UUID orderId = sampleOrderId();
        final UUID ticketId = sampleTicketId();

        when(orderRepository.findById(sampleOrderId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.removeTicketFromOrder(orderId, ticketId))
                .isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    void removeTicketFromOrderTicketNotFound() {
        final Order order = sampleOrder();
        final UUID orderId = sampleOrderId();
        final UUID ticketId = sampleTicketId();

        when(orderRepository.findById(sampleOrderId())).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.removeTicketFromOrder(orderId, ticketId))
                .isInstanceOf(TicketNotFoundException.class);
    }

    @Test
    void softDeleteOrderByIdSuccess() {
        final Order order = sampleOrder();
        final OrderResponseDto response = sampleOrderResponseDto();

        when(orderRepository.findById(sampleOrderId())).thenReturn(Optional.of(order));
        when(orderMapper.toDto(order)).thenReturn(response);

        final OrderResponseDto result = orderService.softDeleteOrderById(sampleOrderId());

        assertThat(result).isEqualTo(response);
        assertThat(order.isDeleted()).isTrue();
        verify(cacheService).evictByPattern(OrderServiceImpl.ORDERS_BY_VENUE);
    }

    @Test
    void softDeleteOrderByIdNotFound() {
        final UUID orderId = sampleOrderId();

        when(orderRepository.findById(sampleOrderId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.softDeleteOrderById(orderId))
                .isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    void deleteOrderByIdSuccess() {
        final Order order = sampleOrder();

        when(orderRepository.findById(sampleOrderId())).thenReturn(Optional.of(order));

        orderService.deleteOrderById(sampleOrderId());

        verify(orderRepository).delete(order);
        verify(cacheService).evictByPattern(OrderServiceImpl.ORDERS_BY_VENUE);
    }

    @Test
    void deleteOrderByIdNotFound() {
        final UUID orderId = sampleOrderId();

        when(orderRepository.findById(sampleOrderId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.deleteOrderById(orderId))
                .isInstanceOf(OrderNotFoundException.class);
    }
}