package com.yaroslav.ticket_booking_system.controller;

import com.yaroslav.ticket_booking_system.dto.UserRequestDto;
import com.yaroslav.ticket_booking_system.dto.UserResponseDto;
import com.yaroslav.ticket_booking_system.dto.UserUpdateDto;
import com.yaroslav.ticket_booking_system.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs for managing users in the ticket booking system")
public class UserController {

    private final UserService userService;

    @PostMapping
    @Operation(summary = "Create a new user", description = "Registers a new user with name, email, and phone number")
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserRequestDto requestDto) {

        final UserResponseDto created = userService.createUser(requestDto);

        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieves detailed information about a specific user")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable UUID id) {

        final UserResponseDto user = userService.getUserById(id);

        return ResponseEntity.ok(user);
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "Get user by name", description = "Retrieves user information by their full name")
    public ResponseEntity<UserResponseDto> getUserByName(@PathVariable String name) {

        final UserResponseDto user = userService.getUserByName(name);

        return ResponseEntity.ok(user);
    }

    @GetMapping("/phone/{phone}")
    @Operation(summary = "Get user by phone", description = "Retrieves user information by phone number")
    public ResponseEntity<UserResponseDto> getUserByPhone(@PathVariable String phone) {

        final UserResponseDto user = userService.getUserByPhone(phone);

        return ResponseEntity.ok(user);
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Get user by email", description = "Retrieves user information by email address")
    public ResponseEntity<UserResponseDto> getUserByEmail(@PathVariable String email) {

        final UserResponseDto user = userService.getUserByEmail(email);

        return ResponseEntity.ok(user);
    }

    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieves a list of all users in the system")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {

        final List<UserResponseDto> users = userService.getAllUsers();

        return ResponseEntity.ok(users);
    }

    @PatchMapping("/{id}/events/{eventId}")
    @Operation(summary = "Add favorite event to user", description = "Adds an event to user's favorites list")
    public ResponseEntity<UserResponseDto> addFavoriteEventToUser(@PathVariable UUID id, @PathVariable UUID eventId) {

        final UserResponseDto user = userService.addFavoriteEventToUser(id, eventId);

        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}/events/{eventId}")
    @Operation(summary = "Remove favorite event from user", description = "Removes an event from user's favorites list")
    public ResponseEntity<UserResponseDto> removeFavoriteEventFromUser(
            @PathVariable UUID id,
            @PathVariable UUID eventId) {

        final UserResponseDto user = userService.removeFavoriteEventFromUser(id, eventId);

        return ResponseEntity.ok(user);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update user by ID", description = "Updates user information (name, email, phone)")
    public ResponseEntity<UserResponseDto> updateUserById(
            @PathVariable UUID id,
            @Valid @RequestBody UserUpdateDto updateDto) {

        final UserResponseDto user = userService.updateUserById(id, updateDto);

        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user by ID", description = "Permanently deletes a user (only if no active orders)")
    public ResponseEntity<Void> deleteUserById(@PathVariable UUID id) {

        userService.deleteUserById(id);

        return ResponseEntity.noContent().build();
    }
}