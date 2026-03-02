package com.yaroslav.ticket_booking_system.repository;

import com.yaroslav.ticket_booking_system.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    @EntityGraph(attributePaths = {"orders", "favoriteEvents"})
    Optional<User> findByName(String name);

    @EntityGraph(attributePaths = {"orders", "favoriteEvents"})
    Optional<User> findByPhone(String phone);

    @EntityGraph(attributePaths = {"orders", "favoriteEvents"})
    Optional<User> findByEmail(String email);
}
