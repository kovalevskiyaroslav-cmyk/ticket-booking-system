package com.yaroslav.ticket_booking_system.dto;

import com.yaroslav.ticket_booking_system.model.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderUpdateDto {

    @NotNull
    private OrderStatus status;
}
