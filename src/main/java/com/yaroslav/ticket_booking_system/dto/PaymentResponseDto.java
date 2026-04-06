package com.yaroslav.ticket_booking_system.dto;

import com.yaroslav.ticket_booking_system.model.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Response object containing payment details")
public class PaymentResponseDto {
    private UUID id;
    private PaymentStatus status;
    private BigDecimal amount;
}