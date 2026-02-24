package com.yaroslav.ticket_booking_system.mapper;

import com.yaroslav.ticket_booking_system.dto.PaymentRequestDto;
import com.yaroslav.ticket_booking_system.dto.PaymentResponseDto;
import com.yaroslav.ticket_booking_system.model.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "order", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "timestamp", ignore = true)
    Payment toEntity(PaymentRequestDto requestDto);

    @Mapping(source = "order.id", target = "orderId")
    PaymentResponseDto toDto(Payment payment);
}
