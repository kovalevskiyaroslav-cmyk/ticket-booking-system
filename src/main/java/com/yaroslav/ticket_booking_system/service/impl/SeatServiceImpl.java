package com.yaroslav.ticket_booking_system.service.impl;

import com.yaroslav.ticket_booking_system.dto.SeatRequestDto;
import com.yaroslav.ticket_booking_system.dto.SeatResponseDto;
import com.yaroslav.ticket_booking_system.dto.SeatUpdateDto;
import com.yaroslav.ticket_booking_system.exception.SeatNotFoundException;
import com.yaroslav.ticket_booking_system.exception.VenueNotFoundException;
import com.yaroslav.ticket_booking_system.mapper.SeatMapper;
import com.yaroslav.ticket_booking_system.model.Seat;
import com.yaroslav.ticket_booking_system.model.Venue;
import com.yaroslav.ticket_booking_system.repository.SeatRepository;
import com.yaroslav.ticket_booking_system.repository.TicketRepository;
import com.yaroslav.ticket_booking_system.repository.VenueRepository;
import com.yaroslav.ticket_booking_system.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SeatServiceImpl implements SeatService {

    private final SeatRepository seatRepository;
    private final VenueRepository venueRepository;
    private final TicketRepository ticketRepository;
    private final SeatMapper seatMapper;

    @Override
    @Transactional
    public SeatResponseDto createSeat(SeatRequestDto requestDto) {

        final Venue venue = venueRepository.findById(requestDto.getVenueId())
                .orElseThrow(() -> new VenueNotFoundException(requestDto.getVenueId()));

        final Seat seat = seatMapper.toEntity(requestDto);
        seat.setVenue(venue);
        seatRepository.save(seat);

        return seatMapper.toDto(seat);
    }

    @Override
    @Transactional(readOnly = true)
    public SeatResponseDto getSeatById(UUID id) {

        final Seat seat = seatRepository.findById(id).orElseThrow(() -> new SeatNotFoundException(id));

        return seatMapper.toDto(seat);
    }

    @Override
    @Transactional(readOnly = true)
    public SeatResponseDto getSeatByVenueIdAndNumber(UUID venueId, Integer number) {

        final Seat seat = seatRepository.findByVenueIdAndNumber(venueId, number)
                .orElseThrow(() -> new SeatNotFoundException("Seat not found with number: " + number));

        return seatMapper.toDto(seat);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeatResponseDto> getSeatsByVenueIdAndSection(UUID venueId, Integer section) {

        return seatRepository.findByVenueIdAndSection(venueId, section)
                .stream()
                .map(seatMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeatResponseDto> getSeatsByPriceBetween(BigDecimal min, BigDecimal max) {

        return seatRepository.findByPriceBetween(min, max)
                .stream()
                .map(seatMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeatResponseDto> getAllSeats() {

        return seatRepository.findAll()
                .stream()
                .map(seatMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public SeatResponseDto updateSeatById(UUID id, SeatUpdateDto updateDto) {

        final Seat seat = seatRepository.findById(id).orElseThrow(() -> new SeatNotFoundException(id));

        if (updateDto.getNumber() != null) {
            seat.setNumber(updateDto.getNumber());
        }
        if (updateDto.getSection() != null) {
            seat.setSection(updateDto.getSection());
        }
        if (updateDto.getPrice() != null) {
            seat.setPrice(updateDto.getPrice());
        }

        return seatMapper.toDto(seat);
    }

    @Override
    @Transactional
    public void deleteSeatById(UUID id) {

        final Seat seat = seatRepository.findById(id).orElseThrow(() -> new SeatNotFoundException(id));

        final boolean hasTickets = ticketRepository.existsBySeatId(id);
        if (hasTickets) {
            throw new IllegalStateException(
                    "Cannot delete seat with ID: " + id + " because it has associated tickets. " +
                            "This would break order history and payment records."
            );
        }

        seatRepository.delete(seat);
    }
}
