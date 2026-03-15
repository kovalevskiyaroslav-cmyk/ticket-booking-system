package com.yaroslav.ticket_booking_system.dto;

import com.yaroslav.ticket_booking_system.model.OrderStatus;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderUpdateDto {

    private OrderStatus status;

    @Past
    private LocalDateTime completedAt;
}
