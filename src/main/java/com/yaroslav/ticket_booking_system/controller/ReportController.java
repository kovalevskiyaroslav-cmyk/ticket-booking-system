package com.yaroslav.ticket_booking_system.controller;

import com.yaroslav.ticket_booking_system.dto.ReportTaskDto;
import com.yaroslav.ticket_booking_system.dto.SalesReportRequestDto;
import com.yaroslav.ticket_booking_system.service.SalesReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final SalesReportService reportService;

    @PostMapping("/sales")
    public ResponseEntity<Map<String, String>> generateSalesReport(@RequestBody SalesReportRequestDto request) {
        final String taskId = reportService.generateReportAsync(request);

        return ResponseEntity.accepted().body(Map.of(
                "taskId", taskId,
                "status", "PENDING",
                "message", "Report generation started"
        ));
    }

    @GetMapping("/{taskId}/status")
    public ResponseEntity<ReportTaskDto> getReportStatus(@PathVariable String taskId) {
        final ReportTaskDto status = reportService.getTaskStatus(taskId);
        return ResponseEntity.ok(status);
    }
}