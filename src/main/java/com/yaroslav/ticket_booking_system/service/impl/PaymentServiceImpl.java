package com.yaroslav.ticket_booking_system.service.impl;

import com.yaroslav.ticket_booking_system.dto.PaymentRequestDto;
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

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    @Override
    @Transactional
    public PaymentResponseDto createPayment(PaymentRequestDto requestDto) {

        final Payment payment = paymentMapper.toEntity(requestDto);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setTimestamp(Instant.now());

        return paymentMapper.toDto(paymentRepository.save(payment));
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDto getPaymentById(UUID id) {

        final Payment payment = paymentRepository.findById(id).orElseThrow(() -> new PaymentNotFoundException(id));

        return paymentMapper.toDto(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getPaymentsByTimestampBetween(
            Instant timestampBefore,
            Instant timestampAfter) {

        return paymentRepository.findByTimestampBetween(timestampBefore, timestampAfter)
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
    @Transactional
    public PaymentResponseDto updatePaymentById(UUID id, PaymentUpdateDto updateDto) {

        final Payment payment = paymentRepository.findById(id).orElseThrow(() -> new PaymentNotFoundException(id));

        if (updateDto.getPaymentAmount() != null) {
            payment.setPaymentAmount(updateDto.getPaymentAmount());
        }
        else {
            payment.setStatus(updateDto.getStatus());
        }

        return paymentMapper.toDto(payment);
    }

    @Override
    @Transactional
    public void deletePaymentById(UUID id) {

        final Payment payment = paymentRepository.findById(id).orElseThrow(() -> new PaymentNotFoundException(id));
        paymentRepository.delete(payment);
    }
}
