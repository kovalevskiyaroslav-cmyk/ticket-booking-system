package com.yaroslav.ticket_booking_system.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TicketRequestDto {

    @NotNull
    private List<UUID> seatIds;

    @NotNull
    private UUID eventId;

    @NotNull
    private UUID orderId;
}
