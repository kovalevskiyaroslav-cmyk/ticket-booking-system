package com.yaroslav.ticket_booking_system.repository;

import com.yaroslav.ticket_booking_system.model.Payment;
import com.yaroslav.ticket_booking_system.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    List<Payment> findByTimestampBetween(Instant paymentTimestampAfter, Instant paymentTimestampBefore);

    List<Payment> findByStatus(PaymentStatus status);
}
