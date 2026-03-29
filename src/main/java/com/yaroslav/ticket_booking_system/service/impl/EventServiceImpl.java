package com.yaroslav.ticket_booking_system.service.impl;

import com.yaroslav.ticket_booking_system.dto.EventRequestDto;
import com.yaroslav.ticket_booking_system.dto.EventResponseDto;
import com.yaroslav.ticket_booking_system.dto.EventUpdateDto;
import com.yaroslav.ticket_booking_system.exception.DuplicateEventNameException;
import com.yaroslav.ticket_booking_system.exception.EventNotFoundException;
import com.yaroslav.ticket_booking_system.exception.VenueNotFoundException;
import com.yaroslav.ticket_booking_system.mapper.EventMapper;
import com.yaroslav.ticket_booking_system.model.Event;
import com.yaroslav.ticket_booking_system.model.Venue;
import com.yaroslav.ticket_booking_system.repository.EventRepository;
import com.yaroslav.ticket_booking_system.repository.TicketRepository;
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
    private final TicketRepository ticketRepository;
    private final EventMapper eventMapper;

    @Override
    @Transactional
    public EventResponseDto createEvent(EventRequestDto requestDto) {
        if (eventRepository.existsByName(requestDto.getName())) {
            throw new DuplicateEventNameException(requestDto.getName());
        }

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
            LocalDateTime start,
            LocalDateTime end) {

        return eventRepository.findByDateTimeBetween(start, end)
                .stream()
                .map(eventMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponseDto> getAllEvents() {

        return eventRepository.findAll()
                .stream()
                .map(eventMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public EventResponseDto updateById(UUID id, EventUpdateDto updateDto) {

        final Event event = eventRepository.findById(id).orElseThrow(() -> new EventNotFoundException(id));

        if (updateDto.getName() != null && !updateDto.getName().equals(event.getName())) {
            if (eventRepository.existsByName(updateDto.getName())) {
                throw new DuplicateEventNameException(updateDto.getName());
            }

            event.setName(updateDto.getName());
        }
        if (updateDto.getDescription() != null) {
            event.setDescription(updateDto.getDescription());
        }
        if (updateDto.getDateTime() != null) {
            event.setDateTime(updateDto.getDateTime());
        }
        if (updateDto.getVenueId() != null) {
            event.setVenue(venueRepository.findById(updateDto.getVenueId())
                    .orElseThrow(() -> new VenueNotFoundException(updateDto.getVenueId())));
        }

        return eventMapper.toDto(event);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        final Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));

        final boolean hasTickets = ticketRepository.existsByEventId(id);
        if (hasTickets) {
            throw new IllegalStateException(
                    "Cannot delete event with ID: " + id + " because tickets have been sold."
            );
        }

        eventRepository.delete(event);
    }
}
