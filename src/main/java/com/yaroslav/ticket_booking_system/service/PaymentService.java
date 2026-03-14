package com.yaroslav.ticket_booking_system.service;

import com.yaroslav.ticket_booking_system.dto.PaymentResponseDto;
import com.yaroslav.ticket_booking_system.model.PaymentStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface PaymentService {
    PaymentResponseDto getPaymentById(UUID id);

    List<PaymentResponseDto> getPaymentsByAmountBetween(BigDecimal min, BigDecimal max);

    List<PaymentResponseDto> getPaymentsByStatus(PaymentStatus status);

    List<PaymentResponseDto> getAllPayments();
}
