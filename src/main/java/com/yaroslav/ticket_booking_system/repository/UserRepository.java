package com.yaroslav.ticket_booking_system.repository;

import com.yaroslav.ticket_booking_system.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByName(String name);

    Optional<User> findByPhone(String phone);

    Optional<User> findByEmail(String email);

    @Modifying
    @Query(value = "UPDATE users SET active = true WHERE id = :id", nativeQuery = true)
    Optional<User> activateUser(@Param("id") UUID id);

    @Modifying
    @Query(value = "UPDATE users SET active = false WHERE id = :id", nativeQuery = true)
    Optional<User> deactivateUser(@Param("id") UUID id);
}
