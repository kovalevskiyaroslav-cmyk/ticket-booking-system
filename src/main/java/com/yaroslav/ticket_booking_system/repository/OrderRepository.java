package com.yaroslav.ticket_booking_system.repository;

import com.yaroslav.ticket_booking_system.model.Order;
import com.yaroslav.ticket_booking_system.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    @EntityGraph(attributePaths = {"user", "payment", "tickets"})
    List<Order> findByStatus(@Param("status") OrderStatus status);

    @EntityGraph(attributePaths = {"user", "payment", "tickets"})
    List<Order> findByDeleted(Boolean deleted);

    @EntityGraph(attributePaths = {"user", "payment", "tickets"})
    List<Order> findByCompletedAtBetween(LocalDateTime start, LocalDateTime end);

    @Query("""
        SELECT DISTINCT o
        FROM Order o
        JOIN o.tickets t
        JOIN t.event e
        WHERE e.venue.id = :venueId
        """)
    Page<Order> findOrdersByVenueId(@Param("venueId") UUID venueId, Pageable pageable);
}
