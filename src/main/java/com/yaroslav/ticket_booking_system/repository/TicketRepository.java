package com.yaroslav.ticket_booking_system.repository;

import com.yaroslav.ticket_booking_system.model.Ticket;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {

    @EntityGraph(attributePaths = {"event", "order", "seat"})
    List<Ticket> findByPriceBetween(BigDecimal min, BigDecimal max);

    @EntityGraph(attributePaths = {"event", "order", "seat"})
    List<Ticket> findAllByEventId(UUID id);

    @Query(value = """
        SELECT t.*
        FROM tickets t
        JOIN events e ON t.event_id = e.id
        WHERE e.venue_id = :venueId
        """,
            countQuery = """
        SELECT COUNT(*)
        FROM tickets t
        JOIN events e ON t.event_id = e.id
        WHERE e.venue_id = :venueId
        """,
            nativeQuery = true)
    Page<Ticket> findTicketsByVenueId(@Param("venueId") UUID venueId, Pageable pageable);

    boolean existsByEventIdAndSeatId(@NotNull UUID eventId, @NotNull UUID seatId);

    boolean existsBySeatId(UUID id);

    boolean existsByEventId(UUID id);
}
