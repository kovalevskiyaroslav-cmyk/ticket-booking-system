package com.yaroslav.ticket_booking_system;

import com.yaroslav.ticket_booking_system.dto.UserRequestDto;
import com.yaroslav.ticket_booking_system.dto.UserResponseDto;
import com.yaroslav.ticket_booking_system.dto.UserUpdateDto;
import com.yaroslav.ticket_booking_system.exception.DuplicateEmailException;
import com.yaroslav.ticket_booking_system.exception.DuplicatePhoneException;
import com.yaroslav.ticket_booking_system.exception.EventNotFoundException;
import com.yaroslav.ticket_booking_system.exception.FavoriteEventAlreadyExistsException;
import com.yaroslav.ticket_booking_system.exception.FavoriteEventNotFoundException;
import com.yaroslav.ticket_booking_system.exception.UserNotFoundException;
import com.yaroslav.ticket_booking_system.mapper.UserMapper;
import com.yaroslav.ticket_booking_system.model.Event;
import com.yaroslav.ticket_booking_system.model.User;
import com.yaroslav.ticket_booking_system.repository.EventRepository;
import com.yaroslav.ticket_booking_system.repository.UserRepository;
import com.yaroslav.ticket_booking_system.service.UserService;
import com.yaroslav.ticket_booking_system.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceTest {

    private UserService userService;
    private UserRepository userRepository;
    private EventRepository eventRepository;
    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        eventRepository = mock(EventRepository.class);
        userMapper = mock(UserMapper.class);
        userService = new UserServiceImpl(userRepository, eventRepository, userMapper);
    }

    private UUID sampleUserId() {
        return UUID.fromString("2bee94e4-e13c-42b6-bcd6-4c418decfc30");
    }

    private UUID sampleEventId() {
        return UUID.fromString("ae6e48c9-c229-4d28-bf3c-82b6d4310d28");
    }

    private User sampleUser() {
        User user = new User();
        user.setId(sampleUserId());
        user.setName("Alice Johnson");
        user.setEmail("alice.johnson@example.com");
        user.setPhone("+12125550123");
        user.setFavoriteEvents(new HashSet<>());
        return user;
    }

    private Event sampleEvent() {
        Event event = new Event();
        event.setId(sampleEventId());
        event.setName("Legends of Rock Live");
        return event;
    }

    private UserRequestDto sampleRequestDto() {
        UserRequestDto dto = new UserRequestDto();
        dto.setName("Alice Johnson");
        dto.setEmail("alice.johnson@example.com");
        dto.setPhone("+12125550123");
        return dto;
    }

    private UserResponseDto sampleResponseDto() {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(sampleUserId());
        dto.setName("Alice Johnson");
        dto.setEmail("alice.johnson@example.com");
        dto.setPhone("+12125550123");
        dto.setOrderIds(Collections.emptyList());
        dto.setFavoriteEventIds(Collections.emptySet());
        return dto;
    }

    @Test
    void createUserSuccess() {
        UserRequestDto request = sampleRequestDto();
        User user = sampleUser();
        UserResponseDto response = sampleResponseDto();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByPhone(request.getPhone())).thenReturn(Optional.empty());
        when(userMapper.toEntity(request)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(response);

        UserResponseDto result = userService.createUser(request);

        assertThat(result).isEqualTo(response);
        verify(userRepository).save(user);
    }

    @Test
    void createUserDuplicateEmail() {
        UserRequestDto request = sampleRequestDto();
        User existingUser = sampleUser();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(existingUser));

        assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(DuplicateEmailException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void createUserDuplicatePhone() {
        UserRequestDto request = sampleRequestDto();
        User existingUser = sampleUser();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByPhone(request.getPhone())).thenReturn(Optional.of(existingUser));

        assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(DuplicatePhoneException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void getUserByIdSuccess() {
        User user = sampleUser();
        UserResponseDto response = sampleResponseDto();

        when(userRepository.findById(sampleUserId())).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(response);

        UserResponseDto result = userService.getUserById(sampleUserId());

        assertThat(result).isEqualTo(response);
    }

    @Test
    void getUserByIdNotFound() {
        when(userRepository.findById(sampleUserId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(sampleUserId()))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void getUserByNameSuccess() {
        User user = sampleUser();
        UserResponseDto response = sampleResponseDto();

        when(userRepository.findByName("Alice Johnson")).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(response);

        UserResponseDto result = userService.getUserByName("Alice Johnson");

        assertThat(result).isEqualTo(response);
    }

    @Test
    void getUserByNameNotFound() {
        when(userRepository.findByName("Nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserByName("Nonexistent"))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void getUserByPhoneSuccess() {
        User user = sampleUser();
        UserResponseDto response = sampleResponseDto();

        when(userRepository.findByPhone("+12125550123")).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(response);

        UserResponseDto result = userService.getUserByPhone("+12125550123");

        assertThat(result).isEqualTo(response);
    }

    @Test
    void getUserByPhoneNotFound() {
        when(userRepository.findByPhone("+9999999999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserByPhone("+9999999999"))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void getUserByEmailSuccess() {
        User user = sampleUser();
        UserResponseDto response = sampleResponseDto();

        when(userRepository.findByEmail("alice.johnson@example.com")).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(response);

        UserResponseDto result = userService.getUserByEmail("alice.johnson@example.com");

        assertThat(result).isEqualTo(response);
    }

    @Test
    void getUserByEmailNotFound() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserByEmail("nonexistent@example.com"))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void getAllUsersSuccess() {
        User user1 = sampleUser();
        User user2 = new User();
        user2.setId(UUID.randomUUID());
        user2.setName("Bob Miller");
        user2.setEmail("bob@example.com");
        user2.setPhone("+12125550789");

        List<User> users = List.of(user1, user2);

        UserResponseDto response1 = sampleResponseDto();
        UserResponseDto response2 = new UserResponseDto();
        response2.setId(user2.getId());
        response2.setName("Bob Miller");
        response2.setEmail("bob@example.com");
        response2.setPhone("+12125550789");

        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toDto(user1)).thenReturn(response1);
        when(userMapper.toDto(user2)).thenReturn(response2);

        List<UserResponseDto> result = userService.getAllUsers();

        assertThat(result).hasSize(2);
    }

    @Test
    void getAllUsersEmpty() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        List<UserResponseDto> result = userService.getAllUsers();

        assertThat(result).isEmpty();
    }

    @Test
    void addFavoriteEventToUserSuccess() {
        User user = sampleUser();
        Event event = sampleEvent();
        UserResponseDto response = sampleResponseDto();

        when(eventRepository.findById(sampleEventId())).thenReturn(Optional.of(event));
        when(userRepository.findById(sampleUserId())).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(response);

        UserResponseDto result = userService.addFavoriteEventToUser(sampleUserId(), sampleEventId());

        assertThat(result).isEqualTo(response);
        assertThat(user.getFavoriteEvents()).contains(event);
    }

    @Test
    void addFavoriteEventToUserEventNotFound() {
        when(eventRepository.findById(sampleEventId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.addFavoriteEventToUser(sampleUserId(), sampleEventId()))
                .isInstanceOf(EventNotFoundException.class);
    }

    @Test
    void addFavoriteEventToUserNotFound() {
        Event event = sampleEvent();

        when(eventRepository.findById(sampleEventId())).thenReturn(Optional.of(event));
        when(userRepository.findById(sampleUserId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.addFavoriteEventToUser(sampleUserId(), sampleEventId()))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void addFavoriteEventToUserAlreadyExists() {
        User user = sampleUser();
        Event event = sampleEvent();
        user.getFavoriteEvents().add(event);

        when(eventRepository.findById(sampleEventId())).thenReturn(Optional.of(event));
        when(userRepository.findById(sampleUserId())).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.addFavoriteEventToUser(sampleUserId(), sampleEventId()))
                .isInstanceOf(FavoriteEventAlreadyExistsException.class);
    }

    @Test
    void removeFavoriteEventFromUserSuccess() {
        User user = sampleUser();
        Event event = sampleEvent();
        user.getFavoriteEvents().add(event);
        UserResponseDto response = sampleResponseDto();

        when(eventRepository.findById(sampleEventId())).thenReturn(Optional.of(event));
        when(userRepository.findById(sampleUserId())).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(response);

        UserResponseDto result = userService.removeFavoriteEventFromUser(sampleUserId(), sampleEventId());

        assertThat(result).isEqualTo(response);
        assertThat(user.getFavoriteEvents()).doesNotContain(event);
    }

    @Test
    void removeFavoriteEventFromUserEventNotFound() {
        when(eventRepository.findById(sampleEventId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.removeFavoriteEventFromUser(sampleUserId(), sampleEventId()))
                .isInstanceOf(EventNotFoundException.class);
    }

    @Test
    void removeFavoriteEventFromUserNotFound() {
        Event event = sampleEvent();

        when(eventRepository.findById(sampleEventId())).thenReturn(Optional.of(event));
        when(userRepository.findById(sampleUserId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.removeFavoriteEventFromUser(sampleUserId(), sampleEventId()))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void removeFavoriteEventFromUserNotExists() {
        User user = sampleUser();
        Event event = sampleEvent();

        when(eventRepository.findById(sampleEventId())).thenReturn(Optional.of(event));
        when(userRepository.findById(sampleUserId())).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.removeFavoriteEventFromUser(sampleUserId(), sampleEventId()))
                .isInstanceOf(FavoriteEventNotFoundException.class);
    }

    @Test
    void updateUserByIdSuccess() {
        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setName("Alice Updated");
        updateDto.setEmail("alice.updated@example.com");
        updateDto.setPhone("+12125550999");

        User user = sampleUser();
        UserResponseDto response = sampleResponseDto();
        response.setName("Alice Updated");
        response.setEmail("alice.updated@example.com");
        response.setPhone("+12125550999");

        when(userRepository.findById(sampleUserId())).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("alice.updated@example.com")).thenReturn(Optional.empty());
        when(userRepository.findByPhone("+12125550999")).thenReturn(Optional.empty());
        when(userMapper.toDto(user)).thenReturn(response);

        UserResponseDto result = userService.updateUserById(sampleUserId(), updateDto);

        assertThat(result.getName()).isEqualTo("Alice Updated");
        assertThat(result.getEmail()).isEqualTo("alice.updated@example.com");
        assertThat(result.getPhone()).isEqualTo("+12125550999");
    }

    @Test
    void updateUserByIdPartialUpdate() {
        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setName("Alice Updated Only");

        User user = sampleUser();
        UserResponseDto response = sampleResponseDto();
        response.setName("Alice Updated Only");

        when(userRepository.findById(sampleUserId())).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(response);

        UserResponseDto result = userService.updateUserById(sampleUserId(), updateDto);

        assertThat(result.getName()).isEqualTo("Alice Updated Only");
        assertThat(result.getEmail()).isEqualTo("alice.johnson@example.com");
        assertThat(result.getPhone()).isEqualTo("+12125550123");
    }

    @Test
    void updateUserByIdNotFound() {
        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setName("Updated Name");

        when(userRepository.findById(sampleUserId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUserById(sampleUserId(), updateDto))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void updateUserByIdDuplicateEmail() {
        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setEmail("existing@example.com");

        User user = sampleUser();
        User existingUser = new User();
        existingUser.setId(UUID.randomUUID());
        existingUser.setEmail("existing@example.com");

        when(userRepository.findById(sampleUserId())).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(existingUser));

        assertThatThrownBy(() -> userService.updateUserById(sampleUserId(), updateDto))
                .isInstanceOf(DuplicateEmailException.class);
    }

    @Test
    void updateUserByIdDuplicatePhone() {
        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setPhone("+12125550999");

        User user = sampleUser();
        User existingUser = new User();
        existingUser.setId(UUID.randomUUID());
        existingUser.setPhone("+12125550999");

        when(userRepository.findById(sampleUserId())).thenReturn(Optional.of(user));
        when(userRepository.findByPhone("+12125550999")).thenReturn(Optional.of(existingUser));

        assertThatThrownBy(() -> userService.updateUserById(sampleUserId(), updateDto))
                .isInstanceOf(DuplicatePhoneException.class);
    }

    @Test
    void updateUserByIdSameEmailDifferentUser() {
        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setEmail("alice.johnson@example.com");

        User user = sampleUser();
        User existingUser = new User();
        existingUser.setId(sampleUserId());
        existingUser.setEmail("alice.johnson@example.com");

        when(userRepository.findById(sampleUserId())).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("alice.johnson@example.com")).thenReturn(Optional.of(existingUser));
        when(userMapper.toDto(user)).thenReturn(sampleResponseDto());

        UserResponseDto result = userService.updateUserById(sampleUserId(), updateDto);

        assertThat(result).isNotNull();
    }

    @Test
    void deleteUserByIdSuccess() {
        User user = sampleUser();

        when(userRepository.findById(sampleUserId())).thenReturn(Optional.of(user));

        userService.deleteUserById(sampleUserId());

        verify(userRepository).delete(user);
    }

    @Test
    void deleteUserByIdNotFound() {
        when(userRepository.findById(sampleUserId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUserById(sampleUserId()))
                .isInstanceOf(UserNotFoundException.class);
    }
}