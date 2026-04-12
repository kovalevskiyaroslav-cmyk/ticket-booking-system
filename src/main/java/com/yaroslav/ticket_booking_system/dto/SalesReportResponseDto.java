package com.yaroslav.ticket_booking_system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SalesReportResponseDto {
    private UUID reportId;
    private LocalDateTime generatedAt;
    private int totalTicketsSold;
    private BigDecimal totalRevenue;
    private Map<String, EventSalesDto> salesByEvent;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class EventSalesDto {
        private String eventName;
        private int ticketsSold;
        private BigDecimal revenue;
    }
}