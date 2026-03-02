package com.yaroslav.ticket_booking_system.repository;

import com.yaroslav.ticket_booking_system.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
}
