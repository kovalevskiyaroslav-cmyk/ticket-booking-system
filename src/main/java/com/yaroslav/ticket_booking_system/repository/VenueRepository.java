package com.yaroslav.ticket_booking_system.repository;

import com.yaroslav.ticket_booking_system.model.Venue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface VenueRepository extends JpaRepository<Venue, UUID> {
    Optional<Venue> findByName(String name);

    Optional<Venue> findByAddress(String address);

    boolean existsByAddress(@Size(min = 1, max = 300) String address);

    boolean existsByName(@NotBlank @Size(min = 1, max = 200) String name);
}
