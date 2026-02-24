package com.yaroslav.ticket_booking_system.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventRequestDto {

    @NotBlank
    @Size(min = 1, max = 100)
    private String name;

    private String description;

    @NotNull
    @Future
    private LocalDateTime dateTime;

    @NotNull
    private UUID venueId;
}
