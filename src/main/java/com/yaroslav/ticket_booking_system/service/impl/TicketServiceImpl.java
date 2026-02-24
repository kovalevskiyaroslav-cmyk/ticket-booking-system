package com.yaroslav.ticket_booking_system.service.impl;

import com.yaroslav.ticket_booking_system.dto.TicketRequestDto;
import com.yaroslav.ticket_booking_system.dto.TicketResponseDto;
import com.yaroslav.ticket_booking_system.exception.EventNotFoundException;
import com.yaroslav.ticket_booking_system.exception.OrderNotFoundException;
import com.yaroslav.ticket_booking_system.exception.SeatNotFoundException;
import com.yaroslav.ticket_booking_system.exception.TicketNotFoundException;
import com.yaroslav.ticket_booking_system.mapper.TicketMapper;
import com.yaroslav.ticket_booking_system.model.Event;
import com.yaroslav.ticket_booking_system.model.Order;
import com.yaroslav.ticket_booking_system.model.Seat;
import com.yaroslav.ticket_booking_system.model.Ticket;
import com.yaroslav.ticket_booking_system.repository.EventRepository;
import com.yaroslav.ticket_booking_system.repository.OrderRepository;
import com.yaroslav.ticket_booking_system.repository.SeatRepository;
import com.yaroslav.ticket_booking_system.repository.TicketRepository;
import com.yaroslav.ticket_booking_system.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final EventRepository eventRepository;
    private final OrderRepository orderRepository;
    private final SeatRepository seatRepository;
    private final TicketMapper ticketMapper;

    @Override
    @Transactional
    public TicketResponseDto createTicket(TicketRequestDto requestDto) {

        final Event event = eventRepository.findById(requestDto.getEventId())
                .orElseThrow(() -> new EventNotFoundException(requestDto.getEventId()));

        final Order order = orderRepository.findById(requestDto.getOrderId())
                .orElseThrow(() -> new OrderNotFoundException(requestDto.getOrderId()));

        final Ticket ticket = new Ticket(order, event);

        final List<Seat> seats = seatRepository.findAllById(requestDto.getSeatIds());

        if (seats.size() != requestDto.getSeatIds().size()) {
            throw new IllegalArgumentException("Some seats not found");
        }

        for (Seat s : seats) {
            if (s.getTicket() != null) {
                throw new IllegalStateException("Seat already reserved");
            }
            ticket.addSeat(s);
        }

        return ticketMapper.toDto(ticketRepository.save(ticket));
    }

    @Override
    @Transactional(readOnly = true)
    public TicketResponseDto getTicketById(UUID id) {

        final Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException(id));

        return ticketMapper.toDto(ticket);
    }
    @Override
    @Transactional(readOnly = true)
    public List<TicketResponseDto> getTicketsByPriceBetween(
            BigDecimal lowerPrice,
            BigDecimal higherPrice) {

        return ticketRepository.findByPriceBetween(lowerPrice, higherPrice)
                .stream()
                .map(ticketMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketResponseDto> getTicketsByEventId(UUID id) {

        return ticketRepository.findAllByEventId(id)
                .stream()
                .map(ticketMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public TicketResponseDto addSeatToTicket(UUID id, UUID seatId) {

        final Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new SeatNotFoundException(id));

        final Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException(id));
        ticket.addSeat(seat);

        return ticketMapper.toDto(ticket);
    }

    @Override
    @Transactional
    public TicketResponseDto removeSeatFromTicket(UUID id, UUID seatId) {

        final Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new SeatNotFoundException(id));

        final Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException(id));
        ticket.removeSeat(seat);

        return ticketMapper.toDto(ticket);
    }

    @Override
    @Transactional
    public void deleteTicketById(UUID ticketId) {

        final Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ticketId));

        if (ticket.getSeats() != null) {
            for (Seat seat : ticket.getSeats()) {
                seat.setTicket(null);
            }
        }

        ticketRepository.delete(ticket);
    }
}
