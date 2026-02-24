package com.yaroslav.ticket_booking_system.service.impl;

import com.yaroslav.ticket_booking_system.dto.EventRequestDto;
import com.yaroslav.ticket_booking_system.dto.EventResponseDto;
import com.yaroslav.ticket_booking_system.exception.EventNotFoundException;
import com.yaroslav.ticket_booking_system.exception.VenueNotFoundException;
import com.yaroslav.ticket_booking_system.mapper.EventMapper;
import com.yaroslav.ticket_booking_system.model.Event;
import com.yaroslav.ticket_booking_system.model.Venue;
import com.yaroslav.ticket_booking_system.repository.EventRepository;
import com.yaroslav.ticket_booking_system.repository.VenueRepository;
import com.yaroslav.ticket_booking_system.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final VenueRepository venueRepository;
    private final EventMapper eventMapper;

    @Override
    @Transactional
    public EventResponseDto createEvent(EventRequestDto requestDto) {

        final Event event = eventMapper.toEntity(requestDto);

        final Venue venue = venueRepository.findById(requestDto.getVenueId())
                .orElseThrow(() -> new VenueNotFoundException(requestDto.getVenueId()));
        event.setVenue(venue);

        return eventMapper.toDto(eventRepository.save(event));
    }

    @Override
    @Transactional(readOnly = true)
    public EventResponseDto getEventById(UUID id) {

        final Event event = eventRepository.findById(id).orElseThrow(() -> new EventNotFoundException(id));

        return eventMapper.toDto(event);
    }

    @Override
    @Transactional(readOnly = true)
    public EventResponseDto getEventByName(String name) {

        final Event event = eventRepository.findByName(name)
                .orElseThrow(() -> new EventNotFoundException("Event not found with name: " + name));

        return eventMapper.toDto(event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponseDto> getEventsByDateTimeBetween(
            LocalDateTime dateTimeBefore,
            LocalDateTime dateTimeAfter) {

        return eventRepository.findByDateTimeBetween(dateTimeBefore, dateTimeAfter)
                .stream()
                .map(eventMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public EventResponseDto updateById(UUID id, EventRequestDto requestDto) {

        final Event event = eventRepository.findById(id).orElseThrow(() -> new EventNotFoundException(id));
        event.setName(requestDto.getName());
        event.setDescription(requestDto.getDescription());
        event.setDateTime(requestDto.getDateTime());
        event.setVenue(venueRepository.findById(requestDto.getVenueId())
                .orElseThrow(() -> new VenueNotFoundException(requestDto.getVenueId())));

        return eventMapper.toDto(event);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {

        final Event event = eventRepository.findById(id).orElseThrow(() -> new EventNotFoundException(id));
        eventRepository.delete(event);
    }
}
