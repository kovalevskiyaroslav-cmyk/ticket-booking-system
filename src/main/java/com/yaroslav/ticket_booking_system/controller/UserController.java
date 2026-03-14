package com.yaroslav.ticket_booking_system.controller;

import com.yaroslav.ticket_booking_system.dto.UserRequestDto;
import com.yaroslav.ticket_booking_system.dto.UserResponseDto;
import com.yaroslav.ticket_booking_system.dto.UserUpdateDto;
import com.yaroslav.ticket_booking_system.service.UserService;
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
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@RequestBody UserRequestDto requestDto) {

        final UserResponseDto created = userService.createUser(requestDto);

        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable UUID id) {

        final UserResponseDto user = userService.getUserById(id);

        return ResponseEntity.ok(user);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<UserResponseDto> getUserByName(@PathVariable String name) {

        final UserResponseDto user = userService.getUserByName(name);

        return ResponseEntity.ok(user);
    }

    @GetMapping("/phone/{phone}")
    public ResponseEntity<UserResponseDto> getUserByPhone(@PathVariable String phone) {

        final UserResponseDto user = userService.getUserByPhone(phone);

        return ResponseEntity.ok(user);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponseDto> getUserByEmail(@PathVariable String email) {

        final UserResponseDto user = userService.getUserByEmail(email);

        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {

        final List<UserResponseDto> users = userService.getAllUsers();

        return ResponseEntity.ok(users);
    }

    @PatchMapping("/{id}/events/{eventId}")
    public ResponseEntity<UserResponseDto> addFavoriteEventToUser(@PathVariable UUID id, @PathVariable UUID eventId) {

        final UserResponseDto user = userService.addFavoriteEventToUser(id, eventId);

        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}/events/{eventId}")
    public ResponseEntity<UserResponseDto> removeFavoriteEventFromUser(
            @PathVariable UUID id,
            @PathVariable UUID eventId) {

        final UserResponseDto user = userService.removeFavoriteEventFromUser(id, eventId);

        return ResponseEntity.ok(user);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUserById(@PathVariable UUID id, @RequestBody UserUpdateDto updateDto) {

        final UserResponseDto user = userService.updateUserById(id, updateDto);

        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable UUID id) {

        userService.deleteUserById(id);

        return ResponseEntity.noContent().build();
    }
}
