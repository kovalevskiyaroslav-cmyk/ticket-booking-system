package com.yaroslav.ticket_booking_system.service;

import com.yaroslav.ticket_booking_system.dto.EventRequestDto;
import com.yaroslav.ticket_booking_system.dto.EventResponseDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface EventService {
    EventResponseDto createEvent(EventRequestDto requestDto);

    EventResponseDto getEventByName(String name);

    EventResponseDto getEventById(UUID id);

    List<EventResponseDto> getEventsByDateTimeBetween(LocalDateTime dateTimeBefore, LocalDateTime dateTimeAfter);

    EventResponseDto updateById(UUID id, EventRequestDto requestDto);

    void deleteById(UUID id);
}
