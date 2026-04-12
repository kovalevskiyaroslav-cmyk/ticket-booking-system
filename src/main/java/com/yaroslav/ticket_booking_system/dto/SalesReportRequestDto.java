package com.yaroslav.ticket_booking_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SalesReportRequestDto {
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private String groupBy;
}