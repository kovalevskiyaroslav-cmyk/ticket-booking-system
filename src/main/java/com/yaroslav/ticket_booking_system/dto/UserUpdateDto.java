package com.yaroslav.ticket_booking_system.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDto {

    @Size(min = 1, max = 100)
    private String name;

    @Email
    @Size(min = 1, max = 255)
    private String email;

    @Pattern(regexp = "^[+]?[0-9\\s-()]{8,20}$",
            message = "Invalid phone number format")
    private String phone;
}
