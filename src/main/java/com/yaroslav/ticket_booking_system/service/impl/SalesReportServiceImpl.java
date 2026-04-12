package com.yaroslav.ticket_booking_system.service.impl;

import com.yaroslav.ticket_booking_system.dto.SalesReportRequestDto;
import com.yaroslav.ticket_booking_system.dto.SalesReportResponseDto;
import com.yaroslav.ticket_booking_system.dto.ReportTaskDto;
import com.yaroslav.ticket_booking_system.service.ReportGenerator;
import com.yaroslav.ticket_booking_system.service.SalesReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SalesReportServiceImpl implements SalesReportService {

    private final ReportGenerator reportGenerator;

    private final Map<String, TaskContext> tasks = new ConcurrentHashMap<>();

    private record TaskContext(String status, String message, SalesReportResponseDto result, LocalDateTime createdAt,
                               CompletableFuture<SalesReportResponseDto> future) {
    }

    @Override
    public String generateReportAsync(SalesReportRequestDto request) {
        final String taskId = UUID.randomUUID().toString();

        final CompletableFuture<SalesReportResponseDto> future = reportGenerator.generate(taskId, request);

        tasks.put(taskId, new TaskContext("PENDING", "Task created", null, LocalDateTime.now(), future));

        future.whenComplete((result, error) -> {
            if (error != null) {
                tasks.put(taskId, new TaskContext("FAILED", "Error: " + error.getMessage(), null,
                        tasks.get(taskId).createdAt, future));
            } else {
                tasks.put(taskId, new TaskContext("COMPLETED", "Report generated successfully", result,
                        tasks.get(taskId).createdAt, future));
            }
        });

        log.info("Report generation task started: {}", taskId);
        return taskId;
    }

    @Override
    public ReportTaskDto getTaskStatus(String taskId) {
        final TaskContext context = tasks.get(taskId);

        if (context == null) {
            return ReportTaskDto.builder()
                    .taskId(taskId)
                    .status("NOT_FOUND")
                    .message("Task not found")
                    .createdAt(LocalDateTime.now())
                    .build();
        }

        return ReportTaskDto.builder()
                .taskId(taskId)
                .status(context.status)
                .message(context.message)
                .result(context.result)
                .createdAt(context.createdAt)
                .build();
    }
}