package com.yaroslav.ticket_booking_system.service;

import com.yaroslav.ticket_booking_system.dto.ReportTaskDto;
import com.yaroslav.ticket_booking_system.dto.SalesReportRequestDto;

public interface SalesReportService {
    String generateReportAsync(SalesReportRequestDto request);

    ReportTaskDto getTaskStatus(String taskId);
}