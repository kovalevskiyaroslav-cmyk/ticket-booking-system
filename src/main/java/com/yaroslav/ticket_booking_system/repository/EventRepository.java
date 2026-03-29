package com.yaroslav.ticket_booking_system.repository;

import com.yaroslav.ticket_booking_system.model.Event;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {

    @EntityGraph(attributePaths = {"venue"})
    Optional<Event> findByName(String name);

    @EntityGraph(attributePaths = {"venue"})
    List<Event> findByDateTimeBetween(LocalDateTime start, LocalDateTime end);

    boolean existsByName(@NotBlank @Size(min = 1, max = 200) String name);
}
