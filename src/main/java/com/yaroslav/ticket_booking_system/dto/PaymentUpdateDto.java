package com.yaroslav.ticket_booking_system.dto;

import com.yaroslav.ticket_booking_system.model.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request object for updating payment status")
public class PaymentUpdateDto {

    @NotNull
    private PaymentStatus status;
}