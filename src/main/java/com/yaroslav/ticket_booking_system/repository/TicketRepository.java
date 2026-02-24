package com.yaroslav.ticket_booking_system.repository;

import com.yaroslav.ticket_booking_system.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {
    List<Ticket> findByPriceBetween(BigDecimal lowerPrice, BigDecimal higherPrice);

    List<Ticket> findAllByEventId(UUID id);
}
