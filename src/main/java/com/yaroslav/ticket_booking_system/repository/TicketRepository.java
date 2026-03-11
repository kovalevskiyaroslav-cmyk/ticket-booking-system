package com.yaroslav.ticket_booking_system.repository;

import com.yaroslav.ticket_booking_system.model.Ticket;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {

    @EntityGraph(attributePaths = {"event", "order", "seat"})
    List<Ticket> findByPriceBetween(BigDecimal min, BigDecimal max);

    @EntityGraph(attributePaths = {"event", "order", "seat"})
    List<Ticket> findAllByEventId(UUID id);

    boolean existsByEventIdAndSeatId(@NotNull UUID eventId, @NotNull UUID seatId);

    boolean existsBySeatId(UUID id);

    boolean existsByEventId(UUID id);
}
