package com.yaroslav.ticket_booking_system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request object for creating a ticket")
public class TicketRequestDto {

    @NotNull
    private UUID seatId;

    @NotNull
    private UUID eventId;
}