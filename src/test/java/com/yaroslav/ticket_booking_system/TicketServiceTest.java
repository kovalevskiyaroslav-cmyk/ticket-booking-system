package com.yaroslav.ticket_booking_system;

import com.yaroslav.ticket_booking_system.cache.QueryCacheService;
import com.yaroslav.ticket_booking_system.cache.QueryKey;
import com.yaroslav.ticket_booking_system.dto.TicketResponseDto;
import com.yaroslav.ticket_booking_system.exception.TicketNotFoundException;
import com.yaroslav.ticket_booking_system.mapper.TicketMapper;
import com.yaroslav.ticket_booking_system.model.Ticket;
import com.yaroslav.ticket_booking_system.repository.TicketRepository;
import com.yaroslav.ticket_booking_system.service.TicketService;
import com.yaroslav.ticket_booking_system.service.impl.TicketServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TicketServiceTest {

    private TicketService ticketService;
    private TicketRepository ticketRepository;
    private TicketMapper ticketMapper;
    private QueryCacheService cacheService;

    @BeforeEach
    void setUp() {
        ticketRepository = mock(TicketRepository.class);
        ticketMapper = mock(TicketMapper.class);
        cacheService = mock(QueryCacheService.class);
        ticketService = new TicketServiceImpl(ticketRepository, ticketMapper, cacheService);
    }

    private UUID sampleTicketId() {
        return UUID.fromString("a6aae4f0-da7d-453f-97c0-d7cfbad11f19");
    }

    private UUID sampleEventId() {
        return UUID.fromString("ae6e48c9-c229-4d28-bf3c-82b6d4310d28");
    }

    private UUID sampleSeatId() {
        return UUID.fromString("0f96cef9-6522-42b5-ae35-7ca415fdba4c");
    }

    private UUID sampleOrderId() {
        return UUID.fromString("83ba4bbd-9588-497b-a04a-1036edc3e0a2");
    }

    private Ticket sampleTicket() {
        Ticket ticket = new Ticket();
        ticket.setId(sampleTicketId());
        ticket.setPrice(new BigDecimal("89.99"));
        return ticket;
    }

    private TicketResponseDto sampleResponseDto() {
        TicketResponseDto dto = new TicketResponseDto();
        dto.setId(sampleTicketId());
        dto.setPrice(new BigDecimal("89.99"));
        dto.setSeatId(sampleSeatId());
        dto.setEventId(sampleEventId());
        dto.setOrderId(sampleOrderId());
        return dto;
    }

    @Test
    void getTicketByIdSuccess() {
        Ticket ticket = sampleTicket();
        TicketResponseDto response = sampleResponseDto();

        when(ticketRepository.findById(sampleTicketId())).thenReturn(Optional.of(ticket));
        when(ticketMapper.toDto(ticket)).thenReturn(response);

        TicketResponseDto result = ticketService.getTicketById(sampleTicketId());

        assertThat(result).isEqualTo(response);
    }

    @Test
    void getTicketByIdNotFound() {
        when(ticketRepository.findById(sampleTicketId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ticketService.getTicketById(sampleTicketId()))
                .isInstanceOf(TicketNotFoundException.class);
    }

    @Test
    void getTicketsByPriceBetweenSuccess() {
        BigDecimal min = new BigDecimal("50.00");
        BigDecimal max = new BigDecimal("100.00");

        Ticket ticket1 = sampleTicket();
        Ticket ticket2 = new Ticket();
        ticket2.setId(UUID.randomUUID());
        ticket2.setPrice(new BigDecimal("95.50"));

        List<Ticket> tickets = List.of(ticket1, ticket2);

        TicketResponseDto response1 = sampleResponseDto();
        TicketResponseDto response2 = new TicketResponseDto();
        response2.setId(ticket2.getId());
        response2.setPrice(new BigDecimal("95.50"));

        when(ticketRepository.findByPriceBetween(min, max)).thenReturn(tickets);
        when(ticketMapper.toDto(ticket1)).thenReturn(response1);
        when(ticketMapper.toDto(ticket2)).thenReturn(response2);

        List<TicketResponseDto> result = ticketService.getTicketsByPriceBetween(min, max);

        assertThat(result).hasSize(2);
        assertThat(result.get(0)).isEqualTo(response1);
        assertThat(result.get(1)).isEqualTo(response2);
    }

    @Test
    void getTicketsByPriceBetweenEmpty() {
        BigDecimal min = new BigDecimal("500.00");
        BigDecimal max = new BigDecimal("1000.00");

        when(ticketRepository.findByPriceBetween(min, max)).thenReturn(Collections.emptyList());

        List<TicketResponseDto> result = ticketService.getTicketsByPriceBetween(min, max);

        assertThat(result).isEmpty();
    }

    @Test
    void getTicketsByEventIdSuccess() {
        Ticket ticket1 = sampleTicket();
        Ticket ticket2 = new Ticket();
        ticket2.setId(UUID.randomUUID());
        ticket2.setPrice(new BigDecimal("120.00"));

        List<Ticket> tickets = List.of(ticket1, ticket2);

        TicketResponseDto response1 = sampleResponseDto();
        TicketResponseDto response2 = new TicketResponseDto();
        response2.setId(ticket2.getId());
        response2.setPrice(new BigDecimal("120.00"));

        when(ticketRepository.findAllByEventId(sampleEventId())).thenReturn(tickets);
        when(ticketMapper.toDto(ticket1)).thenReturn(response1);
        when(ticketMapper.toDto(ticket2)).thenReturn(response2);

        List<TicketResponseDto> result = ticketService.getTicketsByEventId(sampleEventId());

        assertThat(result).hasSize(2);
    }

    @Test
    void getTicketsByEventIdEmpty() {
        when(ticketRepository.findAllByEventId(sampleEventId())).thenReturn(Collections.emptyList());

        List<TicketResponseDto> result = ticketService.getTicketsByEventId(sampleEventId());

        assertThat(result).isEmpty();
    }

    @Test
    void getAllTicketsSuccess() {
        Ticket ticket1 = sampleTicket();
        Ticket ticket2 = new Ticket();
        ticket2.setId(UUID.randomUUID());
        ticket2.setPrice(new BigDecimal("120.00"));

        List<Ticket> tickets = List.of(ticket1, ticket2);

        TicketResponseDto response1 = sampleResponseDto();
        TicketResponseDto response2 = new TicketResponseDto();
        response2.setId(ticket2.getId());
        response2.setPrice(new BigDecimal("120.00"));

        when(ticketRepository.findAll()).thenReturn(tickets);
        when(ticketMapper.toDto(ticket1)).thenReturn(response1);
        when(ticketMapper.toDto(ticket2)).thenReturn(response2);

        List<TicketResponseDto> result = ticketService.getAllTickets();

        assertThat(result).hasSize(2);
    }

    @Test
    void getAllTicketsEmpty() {
        when(ticketRepository.findAll()).thenReturn(Collections.emptyList());

        List<TicketResponseDto> result = ticketService.getAllTickets();

        assertThat(result).isEmpty();
    }

    @Test
    void getTicketsByEventNameCacheHit() {
        String eventName = "Legends of Rock Live";
        Pageable pageable = PageRequest.of(0, 10);
        QueryKey key = new QueryKey(
                "getTicketsByVenue",
                eventName,
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort()
        );
        Page<TicketResponseDto> cachedPage = new PageImpl<>(List.of(sampleResponseDto()));

        when(cacheService.containsKey(key)).thenReturn(true);
        when(cacheService.getPage(key, TicketResponseDto.class)).thenReturn(cachedPage);

        Page<TicketResponseDto> result = ticketService.getTicketsByEventName(eventName, pageable);

        assertThat(result).isEqualTo(cachedPage);
        verify(ticketRepository, never()).findTicketsByEventName(anyString(), any());
    }

    @Test
    void getTicketsByEventNameCacheMiss() {
        String eventName = "Legends of Rock Live";
        Pageable pageable = PageRequest.of(0, 10);
        Ticket ticket = sampleTicket();
        TicketResponseDto response = sampleResponseDto();
        Page<Ticket> ticketPage = new PageImpl<>(List.of(ticket));
        new PageImpl<>(List.of(response));

        when(cacheService.containsKey(any(QueryKey.class))).thenReturn(false);
        when(ticketRepository.findTicketsByEventName(eq(eventName), eq(pageable))).thenReturn(ticketPage);
        when(ticketMapper.toDto(ticket)).thenReturn(response);

        Page<TicketResponseDto> result = ticketService.getTicketsByEventName(eventName, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst()).isEqualTo(response);
        verify(cacheService).put(any(QueryKey.class), any(Page.class));
    }
}