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
    public SeatResponseDto getSeatBySeatNum(Integer seatNum) {

        final Seat seat = seatRepository.findBySeatNum(seatNum)
                .orElseThrow(() -> new SeatNotFoundException("Seat not found with number: " + seatNum));

        return seatMapper.toDto(seat);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeatResponseDto> getSeatsBySection(Integer section) {

        return seatRepository.findBySection(section)
                .stream()
                .map(seatMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeatResponseDto> getSeatsByPriceBetween(BigDecimal lowerPrice, BigDecimal higherPrice) {

        return seatRepository.findByPriceBetween(lowerPrice, higherPrice)
                .stream()
                .map(seatMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public SeatResponseDto updateSeatById(UUID id, SeatUpdateDto updateDto) {

        final Seat seat = seatRepository.findById(id).orElseThrow(() -> new SeatNotFoundException(id));

        if (updateDto.getSeatNum() != null) {
            seat.setSeatNum(updateDto.getSeatNum());
        } else if (updateDto.getSection() != null) {
            seat.setSection(updateDto.getSection());
        }
        else {
            seat.setPrice(updateDto.getPrice());
        }

        return seatMapper.toDto(seat);
    }

    @Override
    @Transactional
    public void deleteSeatById(UUID id) {

        final Seat seat = seatRepository.findById(id).orElseThrow(() -> new SeatNotFoundException(id));
        seatRepository.delete(seat);
    }
}
