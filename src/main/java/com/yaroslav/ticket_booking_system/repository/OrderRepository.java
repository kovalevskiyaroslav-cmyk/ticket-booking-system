package com.yaroslav.ticket_booking_system.repository;

import com.yaroslav.ticket_booking_system.model.Order;
import com.yaroslav.ticket_booking_system.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByStatus(OrderStatus status);

    List<Order> findByDeleted(Boolean deleted);

    List<Order> findByDateTimeBetween(LocalDateTime dateTimeBefore, LocalDateTime dateTimeAfter);
}
