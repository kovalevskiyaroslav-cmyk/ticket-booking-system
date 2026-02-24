package com.yaroslav.ticket_booking_system.dto;

import com.yaroslav.ticket_booking_system.model.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponseDto {
    private UUID id;
    private PaymentStatus status;
    private BigDecimal paymentAmount;
    private Instant timestamp;
    private UUID orderId;
}
