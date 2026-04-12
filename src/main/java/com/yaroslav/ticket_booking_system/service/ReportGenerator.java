package com.yaroslav.ticket_booking_system.service;

import com.yaroslav.ticket_booking_system.dto.SalesReportRequestDto;
import com.yaroslav.ticket_booking_system.dto.SalesReportResponseDto;
import java.util.concurrent.CompletableFuture;

public interface ReportGenerator {
    CompletableFuture<SalesReportResponseDto> generate(String taskId, SalesReportRequestDto request);
}