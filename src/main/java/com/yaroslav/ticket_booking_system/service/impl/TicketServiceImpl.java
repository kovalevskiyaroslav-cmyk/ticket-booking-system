package com.yaroslav.ticket_booking_system.service.impl;

import com.yaroslav.ticket_booking_system.cache.QueryCacheService;
import com.yaroslav.ticket_booking_system.cache.QueryKey;
import com.yaroslav.ticket_booking_system.dto.TicketResponseDto;
import com.yaroslav.ticket_booking_system.exception.TicketNotFoundException;
import com.yaroslav.ticket_booking_system.mapper.TicketMapper;
import com.yaroslav.ticket_booking_system.model.Ticket;
import com.yaroslav.ticket_booking_system.repository.TicketRepository;
import com.yaroslav.ticket_booking_system.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;
    private final QueryCacheService cacheService;

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
            BigDecimal min,
            BigDecimal max) {

        return ticketRepository.findByPriceBetween(min, max)
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
    @Transactional(readOnly = true)
    public List<TicketResponseDto> getAllTickets() {

        return ticketRepository.findAll()
                .stream()
                .map(ticketMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TicketResponseDto> getTicketsByVenue(UUID venueId, Pageable pageable) {

        QueryKey key = new QueryKey("getTicketsByVenue", venueId,
                pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());

        if (cacheService.containsKey(key)) {
            System.out.println("[CACHE HIT] getTicketsByVenue: " + venueId + " page " + pageable.getPageNumber());
            return cacheService.getPage(key, TicketResponseDto.class);
        }

        Page<Ticket> tickets = ticketRepository.findTicketsByVenueId(venueId, pageable);
        Page<TicketResponseDto> ticketPage = tickets.map(ticketMapper::toDto);

        cacheService.put(key, ticketPage);

        return ticketPage;
    }
}
