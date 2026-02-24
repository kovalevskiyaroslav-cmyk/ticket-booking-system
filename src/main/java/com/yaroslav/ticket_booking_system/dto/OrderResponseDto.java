package com.yaroslav.ticket_booking_system.dto;

import com.yaroslav.ticket_booking_system.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseDto {
    private UUID id;
    private LocalDateTime dateTime;
    private OrderStatus status;
    private BigDecimal totalPrice;
    private UUID userId;
    private List<UUID> ticketIds;
}
