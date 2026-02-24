package com.yaroslav.ticket_booking_system.dto;

import com.yaroslav.ticket_booking_system.model.PaymentStatus;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentUpdateDto {

    @Positive
    private BigDecimal paymentAmount;

    private PaymentStatus status;
}
