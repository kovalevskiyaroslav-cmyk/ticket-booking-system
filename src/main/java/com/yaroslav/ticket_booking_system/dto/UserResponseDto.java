package com.yaroslav.ticket_booking_system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Response object containing user details")
public class UserResponseDto {
    private UUID id;
    private String name;
    private String email;
    private String phone;
    private List<UUID> orderIds;
    private Set<UUID> favoriteEventIds;
}