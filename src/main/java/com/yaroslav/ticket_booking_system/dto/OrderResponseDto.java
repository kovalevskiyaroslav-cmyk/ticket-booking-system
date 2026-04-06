package com.yaroslav.ticket_booking_system.dto;

import com.yaroslav.ticket_booking_system.model.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Response object containing order details")
public class OrderResponseDto {
    private UUID id;
    private LocalDateTime completedAt;
    private OrderStatus status;
    private BigDecimal totalPrice;
    private UUID userId;
    private PaymentResponseDto paymentDto;
    private List<UUID> ticketIds;
}