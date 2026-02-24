package com.yaroslav.ticket_booking_system.service.impl;

import com.yaroslav.ticket_booking_system.dto.UserRequestDto;
import com.yaroslav.ticket_booking_system.dto.UserResponseDto;
import com.yaroslav.ticket_booking_system.dto.UserUpdateDto;
import com.yaroslav.ticket_booking_system.exception.EventNotFoundException;
import com.yaroslav.ticket_booking_system.exception.UserNotFoundException;
import com.yaroslav.ticket_booking_system.mapper.UserMapper;
import com.yaroslav.ticket_booking_system.model.Event;
import com.yaroslav.ticket_booking_system.model.User;
import com.yaroslav.ticket_booking_system.repository.EventRepository;
import com.yaroslav.ticket_booking_system.repository.UserRepository;
import com.yaroslav.ticket_booking_system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponseDto createUser(UserRequestDto requestDto) {

        final User user = userMapper.toEntity(requestDto);
        user.setActive(true);

        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserById(UUID id) {

        final User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));

        return userMapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserByName(String name) {

        final User user = userRepository.findByName(name)
                .orElseThrow(() -> new UserNotFoundException("User not found with name: " + name));

        return userMapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserByPhone(String phone) {

        final User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new UserNotFoundException("User not found with phone: " + phone));

        return userMapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserByEmail(String email) {

        final User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public UserResponseDto addFavoriteEventToUser(UUID id, UUID eventId) {

        final Event event = eventRepository.findById(id).orElseThrow(() -> new EventNotFoundException(id));

        final User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        user.getFavoriteEvents().add(event);

        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public UserResponseDto removeFavoriteEventFromUser(UUID id, UUID eventId) {

        final Event event = eventRepository.findById(id).orElseThrow(() -> new EventNotFoundException(id));

        final User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        user.getFavoriteEvents().remove(event);

        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public UserResponseDto updateUserById(UUID id, UserUpdateDto updateDto) {

        final User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));

        if (updateDto.getName() != null) {
            user.setName(updateDto.getName());
        } else if (updateDto.getPhone() != null) {
            user.setPhone(updateDto.getPhone());
        } else if (updateDto.getEmail() != null) {
            user.setEmail(updateDto.getEmail());
        }

        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public UserResponseDto activateUserById(UUID id) {

        final User user = userRepository.activateUser(id).orElseThrow(() -> new UserNotFoundException(id));

        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public UserResponseDto deactivateUserById(UUID id) {

        final User user = userRepository.deactivateUser(id).orElseThrow(() -> new UserNotFoundException(id));

        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public void deleteUserById(UUID id) {

        final User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        userRepository.delete(user);
    }
}
