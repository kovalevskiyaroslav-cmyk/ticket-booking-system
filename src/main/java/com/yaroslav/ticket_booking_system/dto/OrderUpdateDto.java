package com.yaroslav.ticket_booking_system.dto;

import com.yaroslav.ticket_booking_system.model.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Request object for updating an existing order")
public class OrderUpdateDto {

    private OrderStatus status;

    @Past
    private LocalDateTime completedAt;
}