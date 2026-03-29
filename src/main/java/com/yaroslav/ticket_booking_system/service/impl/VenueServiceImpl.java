package com.yaroslav.ticket_booking_system.service.impl;

import com.yaroslav.ticket_booking_system.dto.VenueRequestDto;
import com.yaroslav.ticket_booking_system.dto.VenueResponseDto;
import com.yaroslav.ticket_booking_system.dto.VenueUpdateDto;
import com.yaroslav.ticket_booking_system.exception.DuplicateVenueAddressException;
import com.yaroslav.ticket_booking_system.exception.DuplicateVenueNameException;
import com.yaroslav.ticket_booking_system.exception.VenueNotFoundException;
import com.yaroslav.ticket_booking_system.mapper.VenueMapper;
import com.yaroslav.ticket_booking_system.model.Venue;
import com.yaroslav.ticket_booking_system.repository.VenueRepository;
import com.yaroslav.ticket_booking_system.service.VenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VenueServiceImpl implements VenueService {

    private final VenueRepository venueRepository;
    private final VenueMapper venueMapper;

    @Override
    @Transactional
    public VenueResponseDto createVenue(VenueRequestDto requestDto) {

        if (venueRepository.existsByName(requestDto.getName())) {
            throw new DuplicateVenueNameException(requestDto.getName());
        }

        if (venueRepository.existsByAddress(requestDto.getAddress())) {
            throw new DuplicateVenueAddressException(requestDto.getAddress());
        }

        final Venue venue = venueRepository.save(venueMapper.toEntity(requestDto));

        return venueMapper.toDto(venue);
    }

    @Override
    @Transactional(readOnly = true)
    public VenueResponseDto getVenueById(UUID id) {

        final Venue venue = venueRepository.findById(id).orElseThrow(() -> new VenueNotFoundException(id));

        return venueMapper.toDto(venue);
    }

    @Override
    @Transactional(readOnly = true)
    public VenueResponseDto getVenueByName(String name) {

        final Venue venue = venueRepository.findByName(name)
                .orElseThrow(() -> new VenueNotFoundException("Venue not found with name: " + name));

        return venueMapper.toDto(venue);
    }

    @Override
    @Transactional(readOnly = true)
    public VenueResponseDto getVenueByAddress(String address) {

        final Venue venue = venueRepository.findByAddress(address)
                .orElseThrow(() -> new VenueNotFoundException("Venue not found with address: " + address));

        return venueMapper.toDto(venue);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VenueResponseDto> getAllVenues() {

        return venueRepository.findAll()
                .stream()
                .map(venueMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public VenueResponseDto updateVenueById(UUID id, VenueUpdateDto updateDto) {

        final Venue venue = venueRepository.findById(id).orElseThrow(() -> new VenueNotFoundException(id));

        if (updateDto.getName() != null && !updateDto.getName().equals(venue.getName())) {
            if (venueRepository.existsByName(updateDto.getName())) {
                throw new DuplicateVenueNameException(updateDto.getName());
            }

            venue.setName(updateDto.getName());
        }
        if (updateDto.getAddress() != null) {
            if (venueRepository.existsByAddress(updateDto.getAddress()) && !updateDto.getAddress().equals(venue.getAddress())) {
                throw new DuplicateVenueAddressException(updateDto.getAddress());
            }

            venue.setAddress(updateDto.getAddress());
        }

        return venueMapper.toDto(venue);
    }

    @Override
    @Transactional
    public void deleteVenueById(UUID id) {

        final Venue venue = venueRepository.findById(id).orElseThrow(() -> new VenueNotFoundException(id));
        venueRepository.delete(venue);
    }
}
