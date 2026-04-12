package com.yaroslav.ticket_booking_system.repository;

import com.yaroslav.ticket_booking_system.model.Payment;
import com.yaroslav.ticket_booking_system.model.PaymentStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    @EntityGraph(attributePaths = {"order"})
    List<Payment> findByAmountBetween(BigDecimal min, BigDecimal max);

    @EntityGraph(attributePaths = {"order"})
    List<Payment> findByStatus(PaymentStatus status);

    @EntityGraph(attributePaths = {"order"})
    @Query("SELECT SUM(p.amount) FROM Payment p " +
            "WHERE p.status = 'COMPLETED' AND p.order.completedAt BETWEEN :from AND :to")
    BigDecimal sumCompletedPaymentsBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}
