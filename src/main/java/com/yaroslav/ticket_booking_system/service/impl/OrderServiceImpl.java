package com.yaroslav.ticket_booking_system.service.impl;

import com.yaroslav.ticket_booking_system.dto.OrderRequestDto;
import com.yaroslav.ticket_booking_system.dto.OrderResponseDto;
import com.yaroslav.ticket_booking_system.dto.OrderUpdateDto;
import com.yaroslav.ticket_booking_system.dto.TicketRequestDto;
import com.yaroslav.ticket_booking_system.exception.EventNotFoundException;
import com.yaroslav.ticket_booking_system.exception.OrderNotFoundException;
import com.yaroslav.ticket_booking_system.exception.OrderTransactionException;
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
import com.yaroslav.ticket_booking_system.repository.PaymentRepository;
import com.yaroslav.ticket_booking_system.repository.SeatRepository;
import com.yaroslav.ticket_booking_system.repository.TicketRepository;
import com.yaroslav.ticket_booking_system.repository.UserRepository;
import com.yaroslav.ticket_booking_system.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
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
    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto requestDto) {

        final User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new UserNotFoundException(requestDto.getUserId()));

        final Order order = new Order(user);

        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    //@Transactional
    public OrderResponseDto demonstratePartialSave(OrderRequestDto requestDto) {

        final User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new UserNotFoundException(requestDto.getUserId()));

        final Order order = new Order(user);

        final Order savedOrder = orderRepository.save(order);
        log.info("Payment saved: {}", savedOrder.getId());

        final Payment payment = new Payment();
        payment.setStatus(PaymentStatus.PENDING);
        payment.setPaymentAmount(BigDecimal.ZERO);
        payment.setTimestamp(Instant.now());

        if (Instant.now().getEpochSecond() > 0) {
            throw new OrderTransactionException("Error - partial save");
        }

        payment.setOrder(savedOrder);
        final Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment saved: {}", savedPayment.getId());

        savedOrder.setPayment(savedPayment);
        orderRepository.save(savedOrder);

        return orderMapper.toDto(savedOrder);
    }

    @Override
    @Transactional
    public OrderResponseDto demonstrateTransaction(OrderRequestDto requestDto) {

        final User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new UserNotFoundException(requestDto.getUserId()));

        final Order order = new Order(user);
        final Order savedOrder = orderRepository.save(order);

        final Payment payment = new Payment();
        payment.setStatus(PaymentStatus.PENDING);
        payment.setPaymentAmount(BigDecimal.ZERO);
        payment.setTimestamp(Instant.now());

        if (Instant.now().getEpochSecond() > 0) {
            throw new OrderTransactionException("Error - no save");
        }

        payment.setOrder(savedOrder);
        final Payment savedPayment = paymentRepository.save(payment);

        savedOrder.setPayment(savedPayment);

        return orderMapper.toDto(savedOrder);
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

        final Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        final Event event = eventRepository.findById(requestDto.getEventId())
                .orElseThrow(() -> new EventNotFoundException(requestDto.getEventId()));

        final Ticket ticket = new Ticket(order, event);

        for (UUID seatId : requestDto.getSeatIds()) {
            final Seat seat = seatRepository.findById(seatId)
                    .orElseThrow(() -> new SeatNotFoundException(seatId));
            ticket.addSeat(seat);
        }

        order.addTicket(ticket);

        ticketRepository.save(ticket);

        return orderMapper.toDto(order);
    }

    @Override
    @Transactional
    public OrderResponseDto removeTicketFromOrder(UUID id, UUID ticketId) {

        final Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        final Ticket ticket = order.getTickets().stream()
                .filter(t -> t.getId().equals(ticketId))
                .findFirst()
                .orElseThrow(() -> new TicketNotFoundException(ticketId));

        final List<Seat> seats = ticket.getSeats();
        for (Seat seat : seats) {
            seat.setTicket(null);
        }
        ticket.getSeats().clear();

        order.removeTicket(ticket);

        ticketRepository.delete(ticket);

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
