package com.yaroslav.ticket_booking_system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Request object for creating or updating an event")
public class EventRequestDto {

    @NotBlank
    @Size(min = 1, max = 200)
    private String name;

    @Size(max = 5000)
    private String description;

    @NotNull
    @Future
    private LocalDateTime dateTime;

    @NotNull
    private UUID venueId;
}