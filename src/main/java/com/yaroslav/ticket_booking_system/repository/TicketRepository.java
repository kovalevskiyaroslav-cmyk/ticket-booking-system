package com.yaroslav.ticket_booking_system.repository;

import com.yaroslav.ticket_booking_system.model.Ticket;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {

    @EntityGraph(attributePaths = {"event", "order", "seats"})
    List<Ticket> findByPriceBetween(BigDecimal lowerPrice, BigDecimal higherPrice);

    @EntityGraph(attributePaths = {"event", "order", "seats"})
    List<Ticket> findAllByEventId(UUID id);
}
