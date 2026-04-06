package com.yaroslav.ticket_booking_system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Response object containing seat details")
public class SeatResponseDto {
    private UUID id;
    private Integer number;
    private Integer section;
    private BigDecimal price;
    private UUID venueId;
}