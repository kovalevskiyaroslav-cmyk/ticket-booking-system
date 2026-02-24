package com.yaroslav.ticket_booking_system.mapper;

import com.yaroslav.ticket_booking_system.dto.SeatRequestDto;
import com.yaroslav.ticket_booking_system.dto.SeatResponseDto;
import com.yaroslav.ticket_booking_system.model.Seat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SeatMapper {

    @Mapping(target = "venue", ignore = true)
    @Mapping(target = "ticket", ignore = true)
    Seat toEntity(SeatRequestDto requestDto);

    @Mapping(source = "venue.id", target = "venueId")
    SeatResponseDto toDto(Seat seat);
}
