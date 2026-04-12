package com.yaroslav.ticket_booking_system.service.impl;

import com.yaroslav.ticket_booking_system.dto.SalesReportRequestDto;
import com.yaroslav.ticket_booking_system.dto.SalesReportResponseDto;
import com.yaroslav.ticket_booking_system.repository.TicketRepository;
import com.yaroslav.ticket_booking_system.repository.PaymentRepository;
import com.yaroslav.ticket_booking_system.service.ReportGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReportGeneratorImpl implements ReportGenerator {

    private final TicketRepository ticketRepository;
    private final PaymentRepository paymentRepository;

    @Override
    @Async("taskExecutor")
    public CompletableFuture<SalesReportResponseDto> generate(String taskId, SalesReportRequestDto request) {
        try {
            log.info("Processing report task: {}", taskId);

            Thread.sleep(5000);
            final int totalTickets = ticketRepository.countSoldTicketsBetween(
                    request.getFromDate(), request.getToDate()
            );

            Thread.sleep(5000);
            final BigDecimal totalRevenue = paymentRepository.sumCompletedPaymentsBetween(
                    request.getFromDate(), request.getToDate()
            );

            Thread.sleep(5000);
            final Map<String, SalesReportResponseDto.EventSalesDto> salesByEvent =
                    ticketRepository.getSalesGroupedByEvent(request.getFromDate(), request.getToDate());

            Thread.sleep(5000);

            final SalesReportResponseDto result = SalesReportResponseDto.builder()
                    .reportId(UUID.randomUUID())
                    .generatedAt(LocalDateTime.now())
                    .totalTicketsSold(totalTickets)
                    .totalRevenue(totalRevenue)
                    .salesByEvent(salesByEvent)
                    .build();

            log.info("Report task completed: {}", taskId);
            return CompletableFuture.completedFuture(result);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Report task interrupted: {}", taskId, e);
            return CompletableFuture.failedFuture(e);

        } catch (Exception e) {
            log.error("Report task failed: {}", taskId, e);
            return CompletableFuture.failedFuture(e);
        }
    }
}