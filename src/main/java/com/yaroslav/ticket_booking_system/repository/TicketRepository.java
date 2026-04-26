package com.yaroslav.ticket_booking_system.repository;

import com.yaroslav.ticket_booking_system.dto.SalesReportResponseDto;
import com.yaroslav.ticket_booking_system.model.Ticket;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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
        WHERE e.name = :name
        """, nativeQuery = true)
    Page<Ticket> findTicketsByEventName(@Param("name") String name, Pageable pageable);

    boolean existsByEventIdAndSeatId(@NotNull UUID eventId, @NotNull UUID seatId);

    boolean existsBySeatId(UUID id);

    boolean existsByEventId(UUID id);

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.order.completedAt BETWEEN :from AND :to")
    int countSoldTicketsBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT new map(t.event.name as eventName, COUNT(t) as ticketsSold, SUM(t.price) as revenue) " +
            "FROM Ticket t WHERE t.order.completedAt BETWEEN :from AND :to " +
            "GROUP BY t.event.name")
    Map<String, SalesReportResponseDto.EventSalesDto> getSalesGroupedByEvent(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );
}
