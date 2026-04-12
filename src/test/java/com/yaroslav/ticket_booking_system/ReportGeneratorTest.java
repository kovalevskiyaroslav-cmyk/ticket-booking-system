package com.yaroslav.ticket_booking_system;

import com.yaroslav.ticket_booking_system.dto.SalesReportRequestDto;
import com.yaroslav.ticket_booking_system.dto.SalesReportResponseDto;
import com.yaroslav.ticket_booking_system.repository.PaymentRepository;
import com.yaroslav.ticket_booking_system.repository.TicketRepository;
import com.yaroslav.ticket_booking_system.service.ReportGenerator;
import com.yaroslav.ticket_booking_system.service.impl.ReportGeneratorImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReportGeneratorTest {

    private TicketRepository ticketRepository;
    private PaymentRepository paymentRepository;
    private ReportGenerator reportGenerator;

    @BeforeEach
    void setUp() {
        ticketRepository = Mockito.mock(TicketRepository.class);
        paymentRepository = Mockito.mock(PaymentRepository.class);
        reportGenerator = new ReportGeneratorImpl(ticketRepository, paymentRepository);
    }

    @Test
    void generateSuccess() throws Exception {
        final SalesReportRequestDto request = new SalesReportRequestDto();
        request.setFromDate(LocalDateTime.now().minusDays(1));
        request.setToDate(LocalDateTime.now());

        Mockito.when(ticketRepository.countSoldTicketsBetween(Mockito.any(), Mockito.any()))
                .thenReturn(10);
        Mockito.when(paymentRepository.sumCompletedPaymentsBetween(Mockito.any(), Mockito.any()))
                .thenReturn(BigDecimal.TEN);
        Mockito.when(ticketRepository.getSalesGroupedByEvent(Mockito.any(), Mockito.any()))
                .thenReturn(Map.of());

        final CompletableFuture<SalesReportResponseDto> future =
                reportGenerator.generate("task1", request);

        final SalesReportResponseDto result = future.get();

        assertNotNull(result);
        assertEquals(10, result.getTotalTicketsSold());
        assertEquals(BigDecimal.TEN, result.getTotalRevenue());
    }

    @Test
    void generateExceptionBranch() {
        final SalesReportRequestDto request = new SalesReportRequestDto();

        Mockito.when(ticketRepository.countSoldTicketsBetween(Mockito.any(), Mockito.any()))
                .thenThrow(new RuntimeException("db error"));

        final CompletableFuture<SalesReportResponseDto> future =
                reportGenerator.generate("task2", request);

        assertTrue(future.isCompletedExceptionally());
    }

    @Test
    @SuppressWarnings("ResultOfMethodCallIgnored")
    void generateShouldHandleInterruptedException() {
        Thread.currentThread().interrupt();

        final CompletableFuture<SalesReportResponseDto> future =
                reportGenerator.generate("taskId", new SalesReportRequestDto());

        assertTrue(future.isCompletedExceptionally());

        Thread.interrupted();
    }
}