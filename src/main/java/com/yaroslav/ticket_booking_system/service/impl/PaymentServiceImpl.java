package com.yaroslav.ticket_booking_system.service.impl;


import com.yaroslav.ticket_booking_system.dto.PaymentResponseDto;
import com.yaroslav.ticket_booking_system.dto.PaymentUpdateDto;
import com.yaroslav.ticket_booking_system.exception.PaymentNotFoundException;
import com.yaroslav.ticket_booking_system.mapper.PaymentMapper;
import com.yaroslav.ticket_booking_system.model.Payment;
import com.yaroslav.ticket_booking_system.model.PaymentStatus;
import com.yaroslav.ticket_booking_system.repository.PaymentRepository;
import com.yaroslav.ticket_booking_system.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDto getPaymentById(UUID id) {

        final Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));

        return paymentMapper.toDto(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getPaymentsByAmountBetween(BigDecimal min, BigDecimal max) {

        return paymentRepository.findByAmountBetween(min, max)
                .stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getPaymentsByStatus(PaymentStatus status) {

        return paymentRepository.findByStatus(status)
                .stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getAllPayments() {

        return paymentRepository.findAll()
                .stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public PaymentResponseDto updatePaymentById(UUID id, PaymentUpdateDto updateDto) {

        final Payment payment = paymentRepository.findById(id).orElseThrow(() -> new PaymentNotFoundException(id));

        payment.setStatus(updateDto.getStatus());

        return paymentMapper.toDto(payment);
    }
}
