package com.yaroslav.ticket_booking_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TicketResponseDto {
    private UUID id;
    private BigDecimal price;
    private LocalDateTime purchaseDateTime;
    private List<UUID> seatIds;
    private UUID eventId;
    private UUID orderId;
}
