package com.yaroslav.ticket_booking_system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportTaskDto {
    private String taskId;
    private String status;
    private String message;
    private SalesReportResponseDto result;
    private LocalDateTime createdAt;
}