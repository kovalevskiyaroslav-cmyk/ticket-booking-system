package com.yaroslav.ticket_booking_system.dto;

import jakarta.validation.constraints.Future;
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
public class EventUpdateDto {

    @Size(min = 1, max = 200)
    private String name;

    @Size(max = 5000)
    private String description;

    @Future
    private LocalDateTime dateTime;

    private UUID venueId;
}
