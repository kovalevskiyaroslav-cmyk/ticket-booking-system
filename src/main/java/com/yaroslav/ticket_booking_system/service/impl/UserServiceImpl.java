package com.yaroslav.ticket_booking_system.service.impl;

import com.yaroslav.ticket_booking_system.dto.UserRequestDto;
import com.yaroslav.ticket_booking_system.dto.UserResponseDto;
import com.yaroslav.ticket_booking_system.dto.UserUpdateDto;
import com.yaroslav.ticket_booking_system.exception.DuplicateEmailException;
import com.yaroslav.ticket_booking_system.exception.DuplicatePhoneException;
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

import java.util.List;
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

        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new DuplicateEmailException(requestDto.getEmail());
        }
        if (userRepository.findByPhone(requestDto.getPhone()).isPresent()) {
            throw new DuplicatePhoneException(requestDto.getPhone());
        }

        final User user = userMapper.toEntity(requestDto);

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
    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public UserResponseDto addFavoriteEventToUser(UUID id, UUID eventId) {

        final Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException(eventId));

        final User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        user.addFavoriteEvent(event);

        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public UserResponseDto removeFavoriteEventFromUser(UUID id, UUID eventId) {

        final Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException(eventId));

        final User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        user.removeFavoriteEvent(event);

        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public UserResponseDto updateUserById(UUID id, UserUpdateDto updateDto) {

        final User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));

        if (updateDto.getName() != null) {
            user.setName(updateDto.getName());
        }
        if (updateDto.getEmail() != null) {
            userRepository.findByEmail(updateDto.getEmail())
                    .ifPresent(existingUser -> {
                        if (!existingUser.getId().equals(id)) {
                            throw new DuplicateEmailException(updateDto.getEmail());
                        }
                    });
            user.setEmail(updateDto.getEmail());
        }
        if (updateDto.getPhone() != null) {
            userRepository.findByPhone(updateDto.getPhone())
                    .ifPresent(existingUser -> {
                        if (!existingUser.getId().equals(id)) {
                            throw new DuplicatePhoneException(updateDto.getPhone());
                        }
                    });
            user.setPhone(updateDto.getPhone());
        }

        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public void deleteUserById(UUID id) {

        final User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        userRepository.delete(user);
    }
}
