package com.yaroslav.ticket_booking_system.controller;

import com.yaroslav.ticket_booking_system.dto.PaymentResponseDto;
import com.yaroslav.ticket_booking_system.dto.PaymentUpdateDto;
import com.yaroslav.ticket_booking_system.model.PaymentStatus;
import com.yaroslav.ticket_booking_system.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponseDto> getPaymentById(@PathVariable UUID id) {
        final PaymentResponseDto payment = paymentService.getPaymentById(id);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/by-amount-range")
    public ResponseEntity<List<PaymentResponseDto>> getPaymentsByAmountBetween(
            @RequestParam("min") BigDecimal min,
            @RequestParam("max") BigDecimal max) {
        final List<PaymentResponseDto> payments = paymentService.getPaymentsByAmountBetween(min, max);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<PaymentResponseDto>> getPaymentsByStatus(@PathVariable PaymentStatus status) {
        final List<PaymentResponseDto> payments = paymentService.getPaymentsByStatus(status);
        return ResponseEntity.ok(payments);
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponseDto>> getAllPayments() {

        final List<PaymentResponseDto> payments = paymentService.getAllPayments();

        return ResponseEntity.ok(payments);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PaymentResponseDto> updatePaymentById(
            @PathVariable UUID id,
            @RequestBody PaymentUpdateDto updateDto) {

        final PaymentResponseDto payment = paymentService.updatePaymentById(id, updateDto);

        return ResponseEntity.ok(payment);
    }
}
