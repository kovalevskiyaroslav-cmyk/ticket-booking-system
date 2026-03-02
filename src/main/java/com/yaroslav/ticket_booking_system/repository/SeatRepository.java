package com.yaroslav.ticket_booking_system.repository;

import com.yaroslav.ticket_booking_system.model.Seat;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SeatRepository extends JpaRepository<Seat, UUID> {

    @EntityGraph(attributePaths = {"venue"})
    Optional<Seat> findBySeatNum(Integer seatNum);

    @EntityGraph(attributePaths = {"venue"})
    List<Seat> findBySection(Integer section);

    @EntityGraph(attributePaths = {"venue"})
    List<Seat> findByPriceBetween(BigDecimal lowerPrice, BigDecimal higherPrice);
}
