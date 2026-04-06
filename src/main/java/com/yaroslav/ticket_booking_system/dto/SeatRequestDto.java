package com.yaroslav.ticket_booking_system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
@Schema(description = "Request object for creating a new seat")
public class SeatRequestDto {

    @NotNull
    @Positive
    @Min(1)
    private Integer number;

    @NotNull
    @Positive
    @Min(1)
    private Integer section;

    @NotNull
    @Positive
    @DecimalMin(value = "0.01")
    private BigDecimal price;

    @NotNull
    private UUID venueId;
}