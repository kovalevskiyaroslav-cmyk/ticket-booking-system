package com.yaroslav.ticket_booking_system;

import com.yaroslav.ticket_booking_system.dto.SeatRequestDto;
import com.yaroslav.ticket_booking_system.dto.SeatResponseDto;
import com.yaroslav.ticket_booking_system.dto.SeatUpdateDto;
import com.yaroslav.ticket_booking_system.exception.DuplicateSeatException;
import com.yaroslav.ticket_booking_system.exception.SeatNotFoundException;
import com.yaroslav.ticket_booking_system.exception.VenueNotFoundException;
import com.yaroslav.ticket_booking_system.mapper.SeatMapper;
import com.yaroslav.ticket_booking_system.model.Seat;
import com.yaroslav.ticket_booking_system.model.Venue;
import com.yaroslav.ticket_booking_system.repository.SeatRepository;
import com.yaroslav.ticket_booking_system.repository.TicketRepository;
import com.yaroslav.ticket_booking_system.repository.VenueRepository;
import com.yaroslav.ticket_booking_system.service.SeatService;
import com.yaroslav.ticket_booking_system.service.impl.SeatServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SeatServiceTest {

    private SeatService seatService;
    private SeatRepository seatRepository;
    private VenueRepository venueRepository;
    private TicketRepository ticketRepository;
    private SeatMapper seatMapper;

    @BeforeEach
    void setUp() {
        seatRepository = mock(SeatRepository.class);
        venueRepository = mock(VenueRepository.class);
        ticketRepository = mock(TicketRepository.class);
        seatMapper = mock(SeatMapper.class);
        seatService = new SeatServiceImpl(seatRepository, venueRepository, ticketRepository, seatMapper);
    }

    private UUID sampleVenueId() {
        return UUID.fromString("26e72fe6-7573-405f-803e-a7cb7b9387f7");
    }

    private UUID sampleSeatId() {
        return UUID.fromString("0f96cef9-6522-42b5-ae35-7ca415fdba4c");
    }

    private Venue sampleVenue() {
        final Venue venue = new Venue();
        venue.setId(sampleVenueId());
        venue.setName("Grand City Concert Hall");
        return venue;
    }

    private Seat sampleSeat() {
        final Seat seat = new Seat();
        seat.setId(sampleSeatId());
        seat.setNumber(1);
        seat.setSection(1);
        seat.setPrice(new BigDecimal("89.99"));
        seat.setVenue(sampleVenue());
        return seat;
    }

    private SeatRequestDto sampleRequestDto() {
        final SeatRequestDto dto = new SeatRequestDto();
        dto.setNumber(1);
        dto.setSection(1);
        dto.setPrice(new BigDecimal("89.99"));
        dto.setVenueId(sampleVenueId());
        return dto;
    }

    private SeatResponseDto sampleResponseDto() {
        final SeatResponseDto dto = new SeatResponseDto();
        dto.setId(sampleSeatId());
        dto.setNumber(1);
        dto.setSection(1);
        dto.setPrice(new BigDecimal("89.99"));
        dto.setVenueId(sampleVenueId());
        return dto;
    }

    @Test
    void createSeatSuccess() {
        final SeatRequestDto request = sampleRequestDto();
        final Venue venue = sampleVenue();
        final Seat seat = sampleSeat();
        final SeatResponseDto response = sampleResponseDto();

        when(venueRepository.findById(sampleVenueId())).thenReturn(Optional.of(venue));
        when(seatRepository.existsByVenueIdAndSectionAndNumber(sampleVenueId(), 1, 1)).thenReturn(false);
        when(seatMapper.toEntity(request)).thenReturn(seat);
        when(seatMapper.toDto(seat)).thenReturn(response);

        final SeatResponseDto result = seatService.createSeat(request);

        assertThat(result).isEqualTo(response);
        verify(seatRepository).save(seat);
    }

    @Test
    void createSeatVenueNotFound() {
        final SeatRequestDto request = sampleRequestDto();

        when(venueRepository.findById(sampleVenueId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> seatService.createSeat(request))
                .isInstanceOf(VenueNotFoundException.class);
    }

    @Test
    void createSeatDuplicate() {
        final SeatRequestDto request = sampleRequestDto();
        final Venue venue = sampleVenue();

        when(venueRepository.findById(sampleVenueId())).thenReturn(Optional.of(venue));
        when(seatRepository.existsByVenueIdAndSectionAndNumber(sampleVenueId(), 1, 1)).thenReturn(true);

        assertThatThrownBy(() -> seatService.createSeat(request))
                .isInstanceOf(DuplicateSeatException.class);
    }

    @Test
    void getSeatByIdSuccess() {
        final Seat seat = sampleSeat();
        final SeatResponseDto response = sampleResponseDto();

        when(seatRepository.findById(sampleSeatId())).thenReturn(Optional.of(seat));
        when(seatMapper.toDto(seat)).thenReturn(response);

        final SeatResponseDto result = seatService.getSeatById(sampleSeatId());

        assertThat(result).isEqualTo(response);
    }

    @Test
    void getSeatByIdNotFound() {
        final UUID seatId = sampleSeatId();

        when(seatRepository.findById(sampleSeatId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> seatService.getSeatById(seatId))
                .isInstanceOf(SeatNotFoundException.class);
    }

    @Test
    void getSeatByVenueIdAndNumberSuccess() {
        final Seat seat = sampleSeat();
        final SeatResponseDto response = sampleResponseDto();

        when(seatRepository.findByVenueIdAndNumber(sampleVenueId(), 1)).thenReturn(Optional.of(seat));
        when(seatMapper.toDto(seat)).thenReturn(response);

        final SeatResponseDto result = seatService.getSeatByVenueIdAndNumber(sampleVenueId(), 1);

        assertThat(result).isEqualTo(response);
    }

    @Test
    void getSeatByVenueIdAndNumberNotFound() {
        final UUID venueId = sampleVenueId();

        when(seatRepository.findByVenueIdAndNumber(sampleVenueId(), 999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> seatService.getSeatByVenueIdAndNumber(venueId, 999))
                .isInstanceOf(SeatNotFoundException.class);
    }

    @Test
    void getSeatsByVenueIdAndSectionSuccess() {
        final Seat seat1 = sampleSeat();
        final Seat seat2 = new Seat();
        seat2.setId(UUID.randomUUID());
        seat2.setNumber(2);
        seat2.setSection(1);
        seat2.setPrice(new BigDecimal("89.99"));
        seat2.setVenue(sampleVenue());

        final List<Seat> seats = List.of(seat1, seat2);

        final SeatResponseDto response1 = sampleResponseDto();
        final SeatResponseDto response2 = new SeatResponseDto();
        response2.setId(seat2.getId());
        response2.setNumber(2);
        response2.setSection(1);
        response2.setPrice(new BigDecimal("89.99"));
        response2.setVenueId(sampleVenueId());

        when(seatRepository.findByVenueIdAndSection(sampleVenueId(), 1)).thenReturn(seats);
        when(seatMapper.toDto(seat1)).thenReturn(response1);
        when(seatMapper.toDto(seat2)).thenReturn(response2);

        final List<SeatResponseDto> result = seatService.getSeatsByVenueIdAndSection(sampleVenueId(), 1);

        assertThat(result).hasSize(2);
        assertThat(result.get(0)).isEqualTo(response1);
        assertThat(result.get(1)).isEqualTo(response2);
    }

    @Test
    void getSeatsByVenueIdAndSectionEmpty() {
        when(seatRepository.findByVenueIdAndSection(sampleVenueId(), 999)).thenReturn(Collections.emptyList());

        final List<SeatResponseDto> result = seatService.getSeatsByVenueIdAndSection(sampleVenueId(), 999);

        assertThat(result).isEmpty();
    }

    @Test
    void getSeatsByPriceBetweenSuccess() {
        final BigDecimal min = new BigDecimal("50.00");
        final BigDecimal max = new BigDecimal("100.00");

        final Seat seat = sampleSeat();
        final SeatResponseDto response = sampleResponseDto();

        when(seatRepository.findByPriceBetween(min, max)).thenReturn(List.of(seat));
        when(seatMapper.toDto(seat)).thenReturn(response);

        final List<SeatResponseDto> result = seatService.getSeatsByPriceBetween(min, max);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(response);
    }

    @Test
    void getSeatsByPriceBetweenEmpty() {
        final BigDecimal min = new BigDecimal("500.00");
        final BigDecimal max = new BigDecimal("1000.00");

        when(seatRepository.findByPriceBetween(min, max)).thenReturn(Collections.emptyList());

        final List<SeatResponseDto> result = seatService.getSeatsByPriceBetween(min, max);

        assertThat(result).isEmpty();
    }

    @Test
    void getAllSeatsSuccess() {
        final Seat seat1 = sampleSeat();
        final Seat seat2 = new Seat();
        seat2.setId(UUID.randomUUID());
        seat2.setNumber(2);
        seat2.setSection(1);
        seat2.setPrice(new BigDecimal("89.99"));

        final List<Seat> seats = List.of(seat1, seat2);

        final SeatResponseDto response1 = sampleResponseDto();
        final SeatResponseDto response2 = new SeatResponseDto();
        response2.setId(seat2.getId());
        response2.setNumber(2);
        response2.setSection(1);
        response2.setPrice(new BigDecimal("89.99"));

        when(seatRepository.findAll()).thenReturn(seats);
        when(seatMapper.toDto(seat1)).thenReturn(response1);
        when(seatMapper.toDto(seat2)).thenReturn(response2);

        final List<SeatResponseDto> result = seatService.getAllSeats();

        assertThat(result).hasSize(2);
    }

    @Test
    void getAllSeatsEmpty() {
        when(seatRepository.findAll()).thenReturn(Collections.emptyList());

        final List<SeatResponseDto> result = seatService.getAllSeats();

        assertThat(result).isEmpty();
    }

    @Test
    void updateSeatByIdSuccess() {
        final SeatUpdateDto updateDto = new SeatUpdateDto();
        updateDto.setNumber(2);
        updateDto.setSection(2);
        updateDto.setPrice(new BigDecimal("150.00"));

        final Seat seat = sampleSeat();
        final SeatResponseDto response = sampleResponseDto();
        response.setNumber(2);
        response.setSection(2);
        response.setPrice(new BigDecimal("150.00"));

        when(seatRepository.findById(sampleSeatId())).thenReturn(Optional.of(seat));
        when(seatMapper.toDto(seat)).thenReturn(response);

        final SeatResponseDto result = seatService.updateSeatById(sampleSeatId(), updateDto);

        assertThat(result.getNumber()).isEqualTo(2);
        assertThat(result.getSection()).isEqualTo(2);
        assertThat(result.getPrice()).isEqualTo(new BigDecimal("150.00"));
    }

    @Test
    void updateSeatByIdPartialUpdate() {
        final SeatUpdateDto updateDto = new SeatUpdateDto();
        updateDto.setPrice(new BigDecimal("200.00"));

        final Seat seat = sampleSeat();
        final SeatResponseDto response = sampleResponseDto();
        response.setPrice(new BigDecimal("200.00"));

        when(seatRepository.findById(sampleSeatId())).thenReturn(Optional.of(seat));
        when(seatMapper.toDto(seat)).thenReturn(response);

        final SeatResponseDto result = seatService.updateSeatById(sampleSeatId(), updateDto);

        assertThat(result.getPrice()).isEqualTo(new BigDecimal("200.00"));
        assertThat(result.getNumber()).isEqualTo(1);
        assertThat(result.getSection()).isEqualTo(1);
    }

    @Test
    void updateSeatByIdNotFound() {
        final SeatUpdateDto updateDto = new SeatUpdateDto();
        updateDto.setNumber(2);
        final UUID seatId = sampleSeatId();

        when(seatRepository.findById(sampleSeatId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> seatService.updateSeatById(seatId, updateDto))
                .isInstanceOf(SeatNotFoundException.class);
    }

    @Test
    void updateSeatByIdPriceNotUpdatedWhenNull() {
        final SeatUpdateDto updateDto = new SeatUpdateDto();
        updateDto.setNumber(5);
        updateDto.setPrice(null);

        final Seat seat = sampleSeat();
        final BigDecimal originalPrice = seat.getPrice();

        final SeatResponseDto response = sampleResponseDto();

        when(seatRepository.findById(sampleSeatId())).thenReturn(Optional.of(seat));
        when(seatMapper.toDto(seat)).thenReturn(response);

        seatService.updateSeatById(sampleSeatId(), updateDto);

        assertThat(seat.getPrice()).isEqualTo(originalPrice);
    }

    @Test
    void deleteSeatByIdSuccess() {
        final Seat seat = sampleSeat();

        when(seatRepository.findById(sampleSeatId())).thenReturn(Optional.of(seat));
        when(ticketRepository.existsBySeatId(sampleSeatId())).thenReturn(false);

        seatService.deleteSeatById(sampleSeatId());

        verify(seatRepository).delete(seat);
    }

    @Test
    void deleteSeatByIdNotFound() {
        final UUID seatId = sampleSeatId();

        when(seatRepository.findById(sampleSeatId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> seatService.deleteSeatById(seatId))
                .isInstanceOf(SeatNotFoundException.class);
    }

    @Test
    void deleteSeatByIdHasTickets() {
        final Seat seat = sampleSeat();
        final UUID seatId = sampleSeatId();

        when(seatRepository.findById(sampleSeatId())).thenReturn(Optional.of(seat));
        when(ticketRepository.existsBySeatId(sampleSeatId())).thenReturn(true);

        assertThatThrownBy(() -> seatService.deleteSeatById(seatId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot delete seat with ID: " + sampleSeatId());

        verify(seatRepository, never()).delete(any());
    }
}