package com.yaroslav.ticket_booking_system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Response object containing event details")
public class EventResponseDto {
    private UUID id;
    private String name;
    private String description;
    private LocalDateTime dateTime;
    private UUID venueId;
}