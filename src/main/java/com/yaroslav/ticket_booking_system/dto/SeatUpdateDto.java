package com.yaroslav.ticket_booking_system.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SeatUpdateDto {

    @Positive
    @Min(1)
    private Integer number;

    @Positive
    @Min(1)
    private Integer section;

    @Positive
    @DecimalMin(value = "0.01")
    private BigDecimal price;
}
