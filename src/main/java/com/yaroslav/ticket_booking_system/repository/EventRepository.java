package com.yaroslav.ticket_booking_system.repository;

import com.yaroslav.ticket_booking_system.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {
    Optional<Event> findByName(String name);

    List<Event> findByDateTimeBetween(LocalDateTime dateTimeBefore, LocalDateTime dateTimeAfter);
}
