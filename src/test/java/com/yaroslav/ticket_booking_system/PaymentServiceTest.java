package com.yaroslav.ticket_booking_system;

import com.yaroslav.ticket_booking_system.dto.PaymentResponseDto;
import com.yaroslav.ticket_booking_system.dto.PaymentUpdateDto;
import com.yaroslav.ticket_booking_system.exception.InvalidPaymentStatusTransitionException;
import com.yaroslav.ticket_booking_system.exception.PaymentNotFoundException;
import com.yaroslav.ticket_booking_system.mapper.PaymentMapper;
import com.yaroslav.ticket_booking_system.model.Payment;
import com.yaroslav.ticket_booking_system.model.PaymentStatus;
import com.yaroslav.ticket_booking_system.repository.PaymentRepository;
import com.yaroslav.ticket_booking_system.service.PaymentService;
import com.yaroslav.ticket_booking_system.service.impl.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PaymentServiceTest {

    private PaymentService paymentService;
    private PaymentRepository paymentRepository;
    private PaymentMapper paymentMapper;

    @BeforeEach
    void setUp() {
        paymentRepository = mock(PaymentRepository.class);
        paymentMapper = mock(PaymentMapper.class);
        paymentService = new PaymentServiceImpl(paymentRepository, paymentMapper);
    }

    private UUID samplePaymentId() {
        return UUID.fromString("2843b0dd-b624-4b58-bde3-7cfe924c9ace");
    }

    private Payment samplePayment() {
        Payment payment = new Payment();
        payment.setId(samplePaymentId());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setAmount(new BigDecimal("89.99"));
        return payment;
    }

    private PaymentResponseDto samplePaymentResponseDto() {
        PaymentResponseDto dto = new PaymentResponseDto();
        dto.setId(samplePaymentId());
        dto.setStatus(PaymentStatus.PENDING);
        dto.setAmount(new BigDecimal("89.99"));
        return dto;
    }

    @Test
    void getPaymentByIdSuccess() {
        Payment payment = samplePayment();
        PaymentResponseDto response = samplePaymentResponseDto();

        when(paymentRepository.findById(samplePaymentId())).thenReturn(Optional.of(payment));
        when(paymentMapper.toDto(payment)).thenReturn(response);

        PaymentResponseDto result = paymentService.getPaymentById(samplePaymentId());

        assertThat(result).isEqualTo(response);
        assertThat(result.getId()).isEqualTo(samplePaymentId());
        assertThat(result.getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(result.getAmount()).isEqualTo(new BigDecimal("89.99"));
    }

    @Test
    void getPaymentByIdNotFound() {
        when(paymentRepository.findById(samplePaymentId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.getPaymentById(samplePaymentId()))
                .isInstanceOf(PaymentNotFoundException.class);
    }

    @Test
    void getPaymentsByAmountBetweenSuccess() {
        BigDecimal min = new BigDecimal("50.00");
        BigDecimal max = new BigDecimal("150.00");

        Payment payment1 = samplePayment();
        Payment payment2 = new Payment();
        payment2.setId(UUID.randomUUID());
        payment2.setStatus(PaymentStatus.COMPLETED);
        payment2.setAmount(new BigDecimal("120.00"));

        List<Payment> payments = List.of(payment1, payment2);

        PaymentResponseDto response1 = samplePaymentResponseDto();
        PaymentResponseDto response2 = new PaymentResponseDto();
        response2.setId(payment2.getId());
        response2.setStatus(PaymentStatus.COMPLETED);
        response2.setAmount(new BigDecimal("120.00"));

        List<PaymentResponseDto> expectedResponses = List.of(response1, response2);

        when(paymentRepository.findByAmountBetween(min, max)).thenReturn(payments);
        when(paymentMapper.toDto(payment1)).thenReturn(response1);
        when(paymentMapper.toDto(payment2)).thenReturn(response2);

        List<PaymentResponseDto> result = paymentService.getPaymentsByAmountBetween(min, max);

        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(expectedResponses);
    }

    @Test
    void getPaymentsByAmountBetweenEmpty() {
        BigDecimal min = new BigDecimal("500.00");
        BigDecimal max = new BigDecimal("1000.00");

        when(paymentRepository.findByAmountBetween(min, max)).thenReturn(Collections.emptyList());

        List<PaymentResponseDto> result = paymentService.getPaymentsByAmountBetween(min, max);

        assertThat(result).isEmpty();
    }

    @Test
    void getPaymentsByAmountBetweenWithNullValues() {

        when(paymentRepository.findByAmountBetween(null, null)).thenReturn(Collections.emptyList());

        List<PaymentResponseDto> result = paymentService.getPaymentsByAmountBetween(null, null);

        assertThat(result).isEmpty();
    }

    @Test
    void getPaymentsByStatusSuccess() {
        Payment payment1 = samplePayment();
        Payment payment2 = new Payment();
        payment2.setId(UUID.randomUUID());
        payment2.setStatus(PaymentStatus.PENDING);
        payment2.setAmount(new BigDecimal("255.00"));

        List<Payment> payments = List.of(payment1, payment2);

        PaymentResponseDto response1 = samplePaymentResponseDto();
        PaymentResponseDto response2 = new PaymentResponseDto();
        response2.setId(payment2.getId());
        response2.setStatus(PaymentStatus.PENDING);
        response2.setAmount(new BigDecimal("255.00"));

        List<PaymentResponseDto> expectedResponses = List.of(response1, response2);

        when(paymentRepository.findByStatus(PaymentStatus.PENDING)).thenReturn(payments);
        when(paymentMapper.toDto(payment1)).thenReturn(response1);
        when(paymentMapper.toDto(payment2)).thenReturn(response2);

        List<PaymentResponseDto> result = paymentService.getPaymentsByStatus(PaymentStatus.PENDING);

        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(expectedResponses);
        assertThat(result.get(0).getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(result.get(1).getStatus()).isEqualTo(PaymentStatus.PENDING);
    }

    @Test
    void getPaymentsByStatusEmpty() {
        when(paymentRepository.findByStatus(PaymentStatus.FAILED)).thenReturn(Collections.emptyList());

        List<PaymentResponseDto> result = paymentService.getPaymentsByStatus(PaymentStatus.FAILED);

        assertThat(result).isEmpty();
    }

    @Test
    void getAllPaymentsSuccess() {
        Payment payment1 = samplePayment();
        Payment payment2 = new Payment();
        payment2.setId(UUID.randomUUID());
        payment2.setStatus(PaymentStatus.COMPLETED);
        payment2.setAmount(new BigDecimal("120.00"));

        List<Payment> payments = List.of(payment1, payment2);

        PaymentResponseDto response1 = samplePaymentResponseDto();
        PaymentResponseDto response2 = new PaymentResponseDto();
        response2.setId(payment2.getId());
        response2.setStatus(PaymentStatus.COMPLETED);
        response2.setAmount(new BigDecimal("120.00"));

        List<PaymentResponseDto> expectedResponses = List.of(response1, response2);

        when(paymentRepository.findAll()).thenReturn(payments);
        when(paymentMapper.toDto(payment1)).thenReturn(response1);
        when(paymentMapper.toDto(payment2)).thenReturn(response2);

        List<PaymentResponseDto> result = paymentService.getAllPayments();

        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(expectedResponses);
    }

    @Test
    void getAllPaymentsEmpty() {
        when(paymentRepository.findAll()).thenReturn(Collections.emptyList());

        List<PaymentResponseDto> result = paymentService.getAllPayments();

        assertThat(result).isEmpty();
    }

    @Test
    void updatePaymentByIdSuccess() {
        PaymentUpdateDto updateDto = new PaymentUpdateDto();
        updateDto.setStatus(PaymentStatus.COMPLETED);

        Payment payment = samplePayment();
        PaymentResponseDto response = samplePaymentResponseDto();
        response.setStatus(PaymentStatus.COMPLETED);

        when(paymentRepository.findById(samplePaymentId())).thenReturn(Optional.of(payment));
        when(paymentMapper.toDto(payment)).thenReturn(response);

        PaymentResponseDto result = paymentService.updatePaymentById(samplePaymentId(), updateDto);

        assertThat(result.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
    }

    @Test
    void updatePaymentByIdFromPendingToCompleted() {
        PaymentUpdateDto updateDto = new PaymentUpdateDto();
        updateDto.setStatus(PaymentStatus.COMPLETED);

        Payment payment = samplePayment();
        payment.setStatus(PaymentStatus.PENDING);

        PaymentResponseDto response = samplePaymentResponseDto();
        response.setStatus(PaymentStatus.COMPLETED);

        when(paymentRepository.findById(samplePaymentId())).thenReturn(Optional.of(payment));
        when(paymentMapper.toDto(payment)).thenReturn(response);

        PaymentResponseDto result = paymentService.updatePaymentById(samplePaymentId(), updateDto);

        assertThat(result.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
    }

    @Test
    void updatePaymentByIdFromPendingToFailed() {
        PaymentUpdateDto updateDto = new PaymentUpdateDto();
        updateDto.setStatus(PaymentStatus.FAILED);

        Payment payment = samplePayment();
        payment.setStatus(PaymentStatus.PENDING);

        PaymentResponseDto response = samplePaymentResponseDto();
        response.setStatus(PaymentStatus.FAILED);

        when(paymentRepository.findById(samplePaymentId())).thenReturn(Optional.of(payment));
        when(paymentMapper.toDto(payment)).thenReturn(response);

        PaymentResponseDto result = paymentService.updatePaymentById(samplePaymentId(), updateDto);

        assertThat(result.getStatus()).isEqualTo(PaymentStatus.FAILED);
    }

    @Test
    void updatePaymentByIdInvalidTransitionFromCompletedToPending() {
        PaymentUpdateDto updateDto = new PaymentUpdateDto();
        updateDto.setStatus(PaymentStatus.PENDING);

        Payment payment = samplePayment();
        payment.setStatus(PaymentStatus.COMPLETED);

        when(paymentRepository.findById(samplePaymentId())).thenReturn(Optional.of(payment));

        assertThatThrownBy(() -> paymentService.updatePaymentById(samplePaymentId(), updateDto))
                .isInstanceOf(InvalidPaymentStatusTransitionException.class);
    }

    @Test
    void updatePaymentByIdInvalidTransitionFromCompletedToFailed() {
        PaymentUpdateDto updateDto = new PaymentUpdateDto();
        updateDto.setStatus(PaymentStatus.FAILED);

        Payment payment = samplePayment();
        payment.setStatus(PaymentStatus.COMPLETED);

        when(paymentRepository.findById(samplePaymentId())).thenReturn(Optional.of(payment));

        assertThatThrownBy(() -> paymentService.updatePaymentById(samplePaymentId(), updateDto))
                .isInstanceOf(InvalidPaymentStatusTransitionException.class);
    }

    @Test
    void updatePaymentByIdInvalidTransitionFromFailedToCompleted() {
        PaymentUpdateDto updateDto = new PaymentUpdateDto();
        updateDto.setStatus(PaymentStatus.COMPLETED);

        Payment payment = samplePayment();
        payment.setStatus(PaymentStatus.FAILED);

        when(paymentRepository.findById(samplePaymentId())).thenReturn(Optional.of(payment));

        assertThatThrownBy(() -> paymentService.updatePaymentById(samplePaymentId(), updateDto))
                .isInstanceOf(InvalidPaymentStatusTransitionException.class);
    }

    @Test
    void updatePaymentByIdInvalidTransitionFromFailedToPending() {
        PaymentUpdateDto updateDto = new PaymentUpdateDto();
        updateDto.setStatus(PaymentStatus.PENDING);

        Payment payment = samplePayment();
        payment.setStatus(PaymentStatus.FAILED);

        when(paymentRepository.findById(samplePaymentId())).thenReturn(Optional.of(payment));

        assertThatThrownBy(() -> paymentService.updatePaymentById(samplePaymentId(), updateDto))
                .isInstanceOf(InvalidPaymentStatusTransitionException.class);
    }

    @Test
    void updatePaymentByIdSameStatus() {
        PaymentUpdateDto updateDto = new PaymentUpdateDto();
        updateDto.setStatus(PaymentStatus.PENDING);

        Payment payment = samplePayment();
        payment.setStatus(PaymentStatus.PENDING);

        when(paymentRepository.findById(samplePaymentId())).thenReturn(Optional.of(payment));

        assertThatThrownBy(() -> paymentService.updatePaymentById(samplePaymentId(), updateDto))
                .isInstanceOf(InvalidPaymentStatusTransitionException.class);
    }

    @Test
    void updatePaymentByIdNotFound() {
        PaymentUpdateDto updateDto = new PaymentUpdateDto();
        updateDto.setStatus(PaymentStatus.COMPLETED);

        when(paymentRepository.findById(samplePaymentId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.updatePaymentById(samplePaymentId(), updateDto))
                .isInstanceOf(PaymentNotFoundException.class);
    }

    @Test
    void updatePaymentByIdWithNullUpdateDto() {
        when(paymentRepository.findById(samplePaymentId())).thenReturn(Optional.of(samplePayment()));

        assertThatThrownBy(() -> paymentService.updatePaymentById(samplePaymentId(), null))
                .isInstanceOf(NullPointerException.class);
    }
}