package com.yaroslav.ticket_booking_system;

import com.yaroslav.ticket_booking_system.dto.ReportTaskDto;
import com.yaroslav.ticket_booking_system.dto.SalesReportRequestDto;
import com.yaroslav.ticket_booking_system.dto.SalesReportResponseDto;
import com.yaroslav.ticket_booking_system.service.ReportGenerator;
import com.yaroslav.ticket_booking_system.service.SalesReportService;
import com.yaroslav.ticket_booking_system.service.impl.SalesReportServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SalesReportServiceTest {

    private ReportGenerator reportGenerator;
    private SalesReportService salesReportService;

    @BeforeEach
    void setUp() {
        reportGenerator = Mockito.mock(ReportGenerator.class);
        salesReportService = new SalesReportServiceImpl(reportGenerator);
    }

    @Test
    void generateReportAsyncSuccessFlow() {
        final SalesReportRequestDto request = new SalesReportRequestDto();
        request.setFromDate(LocalDateTime.now().minusDays(1));
        request.setToDate(LocalDateTime.now());

        final SalesReportResponseDto response = SalesReportResponseDto.builder().build();

        final CompletableFuture<SalesReportResponseDto> future =
                CompletableFuture.completedFuture(response);

        Mockito.when(reportGenerator.generate(Mockito.anyString(), Mockito.eq(request)))
                .thenReturn(future);

        final String taskId = salesReportService.generateReportAsync(request);

        final ReportTaskDto task = salesReportService.getTaskStatus(taskId);

        assertEquals("COMPLETED", task.getStatus());
        assertEquals("Report generated successfully", task.getMessage());
    }

    @Test
    void generateReportAsyncFailureFlow() {
        final SalesReportRequestDto request = new SalesReportRequestDto();

        final CompletableFuture<SalesReportResponseDto> future =
                CompletableFuture.failedFuture(new RuntimeException("fail"));

        Mockito.when(reportGenerator.generate(Mockito.anyString(), Mockito.eq(request)))
                .thenReturn(future);

        final String taskId = salesReportService.generateReportAsync(request);

        final ReportTaskDto task = salesReportService.getTaskStatus(taskId);

        assertEquals("FAILED", task.getStatus());
        assertTrue(task.getMessage().contains("fail"));
    }

    @Test
    void getTaskStatusNotFound() {
        final ReportTaskDto task = salesReportService.getTaskStatus("unknown");

        assertEquals("NOT_FOUND", task.getStatus());
        assertEquals("Task not found", task.getMessage());
    }
}