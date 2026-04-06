package com.yaroslav.ticket_booking_system;

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
import com.yaroslav.ticket_booking_system.service.impl.EventServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
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

class EventServiceTest {

    private EventService eventService;
    private EventRepository eventRepository;
    private VenueRepository venueRepository;
    private TicketRepository ticketRepository;
    private EventMapper eventMapper;

    @BeforeEach
    void setUp() {
        eventRepository = mock(EventRepository.class);
        venueRepository = mock(VenueRepository.class);
        ticketRepository = mock(TicketRepository.class);
        eventMapper = mock(EventMapper.class);
        eventService = new EventServiceImpl(
                eventRepository,
                venueRepository,
                ticketRepository,
                eventMapper
        );
    }

    private UUID sampleVenueId() {
        return UUID.fromString("26e72fe6-7573-405f-803e-a7cb7b9387f7");
    }

    private UUID sampleEventId() {
        return UUID.fromString("ae6e48c9-c229-4d28-bf3c-82b6d4310d28");
    }

    private EventRequestDto sampleRequestDto() {
        EventRequestDto dto = new EventRequestDto();
        dto.setName("Legends of Rock Live");
        dto.setDescription("A high-energy rock concert");
        dto.setDateTime(LocalDateTime.of(2026, 3, 10, 20, 0));
        dto.setVenueId(sampleVenueId());
        return dto;
    }

    private EventResponseDto sampleResponseDto() {
        EventResponseDto dto = new EventResponseDto();
        dto.setId(sampleEventId());
        dto.setName("Legends of Rock Live");
        dto.setDescription("A high-energy rock concert");
        dto.setDateTime(LocalDateTime.of(2026, 3, 10, 20, 0));
        dto.setVenueId(sampleVenueId());
        return dto;
    }

    private Venue sampleVenue() {
        Venue venue = new Venue();
        venue.setId(sampleVenueId());
        venue.setName("Grand City Concert Hall");
        venue.setAddress("125 Riverside Avenue, New York");
        return venue;
    }

    private Event sampleEvent() {
        Event event = new Event();
        event.setId(sampleEventId());
        event.setName("Legends of Rock Live");
        event.setDescription("A high-energy rock concert");
        event.setDateTime(LocalDateTime.of(2026, 3, 10, 20, 0));
        event.setVenue(sampleVenue());
        return event;
    }

    @Test
    void createEventSuccess() {
        EventRequestDto request = sampleRequestDto();
        Event eventEntity = sampleEvent();
        EventResponseDto response = sampleResponseDto();
        Venue venue = sampleVenue();

        when(eventRepository.existsByName(request.getName())).thenReturn(false);
        when(eventMapper.toEntity(request)).thenReturn(eventEntity);
        when(venueRepository.findById(request.getVenueId())).thenReturn(Optional.of(venue));
        when(eventRepository.save(eventEntity)).thenReturn(eventEntity);
        when(eventMapper.toDto(eventEntity)).thenReturn(response);

        EventResponseDto result = eventService.createEvent(request);

        assertThat(result).isEqualTo(response);
        verify(eventRepository).save(eventEntity);
    }

    @Test
    void createEventDuplicateName() {
        EventRequestDto request = sampleRequestDto();
        when(eventRepository.existsByName(request.getName())).thenReturn(true);

        assertThatThrownBy(() -> eventService.createEvent(request))
                .isInstanceOf(DuplicateEventNameException.class);

        verify(eventRepository, never()).save(any());
    }

    @Test
    void createEventVenueNotFound() {
        EventRequestDto request = sampleRequestDto();
        when(eventRepository.existsByName(request.getName())).thenReturn(false);
        when(venueRepository.findById(request.getVenueId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.createEvent(request))
                .isInstanceOf(VenueNotFoundException.class);

        verify(eventRepository, never()).save(any());
    }

    @Test
    void getEventByIdSuccess() {
        Event event = sampleEvent();
        EventResponseDto response = sampleResponseDto();

        when(eventRepository.findById(sampleEventId())).thenReturn(Optional.of(event));
        when(eventMapper.toDto(event)).thenReturn(response);

        EventResponseDto result = eventService.getEventById(sampleEventId());

        assertThat(result).isEqualTo(response);
    }

    @Test
    void getEventByIdNotFound() {
        when(eventRepository.findById(sampleEventId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.getEventById(sampleEventId()))
                .isInstanceOf(EventNotFoundException.class);
    }

    @Test
    void getEventByNameSuccess() {
        Event event = sampleEvent();
        EventResponseDto response = sampleResponseDto();

        when(eventRepository.findByName("Legends of Rock Live")).thenReturn(Optional.of(event));
        when(eventMapper.toDto(event)).thenReturn(response);

        EventResponseDto result = eventService.getEventByName("Legends of Rock Live");

        assertThat(result).isEqualTo(response);
    }

    @Test
    void getEventByNameNotFound() {
        when(eventRepository.findByName("Non Existent Event")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.getEventByName("Non Existent Event"))
                .isInstanceOf(EventNotFoundException.class);
    }

    @Test
    void getEventsByDateTimeBetweenSuccess() {
        LocalDateTime start = LocalDateTime.of(2026, 3, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, 3, 31, 23, 59);
        Event event = sampleEvent();
        EventResponseDto response = sampleResponseDto();
        List<Event> events = List.of(event);
        List<EventResponseDto> expectedResponses = List.of(response);

        when(eventRepository.findByDateTimeBetween(start, end)).thenReturn(events);
        when(eventMapper.toDto(event)).thenReturn(response);

        List<EventResponseDto> result = eventService.getEventsByDateTimeBetween(start, end);

        assertThat(result).hasSize(1);
        assertThat(result).isEqualTo(expectedResponses);
    }

    @Test
    void getEventsByDateTimeBetweenEmpty() {
        LocalDateTime start = LocalDateTime.of(2026, 3, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, 3, 31, 23, 59);

        when(eventRepository.findByDateTimeBetween(start, end)).thenReturn(List.of());

        List<EventResponseDto> result = eventService.getEventsByDateTimeBetween(start, end);

        assertThat(result).isEmpty();
    }

    @Test
    void getAllEventsSuccess() {
        Event event = sampleEvent();
        EventResponseDto response = sampleResponseDto();
        List<Event> events = List.of(event);
        List<EventResponseDto> expectedResponses = List.of(response);

        when(eventRepository.findAll()).thenReturn(events);
        when(eventMapper.toDto(event)).thenReturn(response);

        List<EventResponseDto> result = eventService.getAllEvents();

        assertThat(result).isEqualTo(expectedResponses);
    }

    @Test
    void getAllEventsEmpty() {
        when(eventRepository.findAll()).thenReturn(List.of());

        List<EventResponseDto> result = eventService.getAllEvents();

        assertThat(result).isEmpty();
    }

    @Test
    void updateByIdSuccess() {
        UUID eventId = sampleEventId();
        EventUpdateDto updateDto = new EventUpdateDto();
        updateDto.setName("Updated Event Name");
        updateDto.setDescription("Updated description");
        updateDto.setDateTime(LocalDateTime.of(2026, 4, 15, 19, 0));

        Event existingEvent = sampleEvent();
        Event updatedEvent = sampleEvent();
        updatedEvent.setName("Updated Event Name");
        updatedEvent.setDescription("Updated description");
        updatedEvent.setDateTime(LocalDateTime.of(2026, 4, 15, 19, 0));

        EventResponseDto response = sampleResponseDto();
        response.setName("Updated Event Name");
        response.setDescription("Updated description");
        response.setDateTime(LocalDateTime.of(2026, 4, 15, 19, 0));

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(existingEvent));
        when(eventRepository.existsByName("Updated Event Name")).thenReturn(false);
        when(eventMapper.toDto(existingEvent)).thenReturn(response);

        EventResponseDto result = eventService.updateById(eventId, updateDto);

        assertThat(result.getName()).isEqualTo("Updated Event Name");
        assertThat(result.getDescription()).isEqualTo("Updated description");
        verify(eventRepository).findById(eventId);
    }

    @Test
    void updateByIdNameDuplicate() {
        UUID eventId = sampleEventId();
        EventUpdateDto updateDto = new EventUpdateDto();
        updateDto.setName("Existing Event Name");

        Event existingEvent = sampleEvent();

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(existingEvent));
        when(eventRepository.existsByName("Existing Event Name")).thenReturn(true);

        assertThatThrownBy(() -> eventService.updateById(eventId, updateDto))
                .isInstanceOf(DuplicateEventNameException.class);
    }

    @Test
    void updateByIdVenueNotFound() {
        UUID eventId = sampleEventId();
        UUID newVenueId = UUID.randomUUID();
        EventUpdateDto updateDto = new EventUpdateDto();
        updateDto.setVenueId(newVenueId);

        Event existingEvent = sampleEvent();

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(existingEvent));
        when(venueRepository.findById(newVenueId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.updateById(eventId, updateDto))
                .isInstanceOf(VenueNotFoundException.class);
    }

    @Test
    void updateByIdEventNotFound() {
        UUID eventId = sampleEventId();
        EventUpdateDto updateDto = new EventUpdateDto();
        updateDto.setName("Updated Name");

        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.updateById(eventId, updateDto))
                .isInstanceOf(EventNotFoundException.class);
    }

    @Test
    void deleteByIdSuccess() {
        UUID eventId = sampleEventId();
        Event event = sampleEvent();

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(ticketRepository.existsByEventId(eventId)).thenReturn(false);

        eventService.deleteById(eventId);

        verify(eventRepository).delete(event);
    }

    @Test
    void deleteByIdEventNotFound() {
        UUID eventId = sampleEventId();

        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.deleteById(eventId))
                .isInstanceOf(EventNotFoundException.class);

        verify(eventRepository, never()).delete(any());
    }

    @Test
    void deleteByIdEventHasTickets() {
        UUID eventId = sampleEventId();
        Event event = sampleEvent();

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(ticketRepository.existsByEventId(eventId)).thenReturn(true);

        assertThatThrownBy(() -> eventService.deleteById(eventId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot delete event with ID: " + eventId);

        verify(eventRepository, never()).delete(any());
    }
}