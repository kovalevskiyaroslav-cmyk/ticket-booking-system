package com.yaroslav.ticket_booking_system.service;

import com.yaroslav.ticket_booking_system.dto.EventRequestDto;
import com.yaroslav.ticket_booking_system.dto.EventResponseDto;
import com.yaroslav.ticket_booking_system.dto.EventUpdateDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface EventService {
    EventResponseDto createEvent(EventRequestDto requestDto);

    EventResponseDto getEventByName(String name);

    EventResponseDto getEventById(UUID id);

    List<EventResponseDto> getEventsByDateTimeBetween(LocalDateTime start, LocalDateTime end);

    List<EventResponseDto> getAllEvents();

    EventResponseDto updateById(UUID id, EventUpdateDto updateDto);

    void deleteById(UUID id);
}
