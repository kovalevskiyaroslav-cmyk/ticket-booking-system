package com.yaroslav.ticket_booking_system.service;

import com.yaroslav.ticket_booking_system.dto.UserRequestDto;
import com.yaroslav.ticket_booking_system.dto.UserResponseDto;
import com.yaroslav.ticket_booking_system.dto.UserUpdateDto;

import java.util.UUID;

public interface UserService {
    UserResponseDto createUser(UserRequestDto requestDto);

    UserResponseDto getUserById(UUID id);

    UserResponseDto getUserByName(String name);

    UserResponseDto getUserByPhone(String phone);

    UserResponseDto getUserByEmail(String email);

    UserResponseDto addFavoriteEventToUser(UUID id, UUID eventId);

    UserResponseDto removeFavoriteEventFromUser(UUID id, UUID eventId);

    UserResponseDto updateUserById(UUID id, UserUpdateDto updateDto);

    UserResponseDto activateUserById(UUID id);

    UserResponseDto deactivateUserById(UUID id);

    void deleteUserById(UUID id);
}
