package com.yaroslav.ticket_booking_system.service;


import com.yaroslav.ticket_booking_system.dto.PaymentRequestDto;
import com.yaroslav.ticket_booking_system.dto.PaymentResponseDto;
import com.yaroslav.ticket_booking_system.dto.PaymentUpdateDto;
import com.yaroslav.ticket_booking_system.model.PaymentStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface PaymentService {
    PaymentResponseDto createPayment(PaymentRequestDto requestDto);

    PaymentResponseDto getPaymentById(UUID id);

    List<PaymentResponseDto> getPaymentsByTimestampBetween(
            Instant timestampBefore,
            Instant timestampAfter);

    List<PaymentResponseDto> getPaymentsByStatus(PaymentStatus status);

    PaymentResponseDto updatePaymentById(UUID id, PaymentUpdateDto updateDto);

    void deletePaymentById(UUID id);
}
