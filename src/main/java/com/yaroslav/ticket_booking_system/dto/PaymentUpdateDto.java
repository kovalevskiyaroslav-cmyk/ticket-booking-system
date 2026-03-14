package com.yaroslav.ticket_booking_system.dto;

import com.yaroslav.ticket_booking_system.model.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentUpdateDto {

    @NotNull
    private PaymentStatus status;
}
