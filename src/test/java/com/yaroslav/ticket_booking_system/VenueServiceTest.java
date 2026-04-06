package com.yaroslav.ticket_booking_system;

import com.yaroslav.ticket_booking_system.dto.VenueRequestDto;
import com.yaroslav.ticket_booking_system.dto.VenueResponseDto;
import com.yaroslav.ticket_booking_system.dto.VenueUpdateDto;
import com.yaroslav.ticket_booking_system.exception.DuplicateVenueAddressException;
import com.yaroslav.ticket_booking_system.exception.DuplicateVenueNameException;
import com.yaroslav.ticket_booking_system.exception.VenueNotFoundException;
import com.yaroslav.ticket_booking_system.mapper.VenueMapper;
import com.yaroslav.ticket_booking_system.model.Venue;
import com.yaroslav.ticket_booking_system.repository.VenueRepository;
import com.yaroslav.ticket_booking_system.service.VenueService;
import com.yaroslav.ticket_booking_system.service.impl.VenueServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
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

class VenueServiceTest {

    private VenueService venueService;
    private VenueRepository venueRepository;
    private VenueMapper venueMapper;

    @BeforeEach
    void setUp() {
        venueRepository = mock(VenueRepository.class);
        venueMapper = mock(VenueMapper.class);
        venueService = new VenueServiceImpl(venueRepository, venueMapper);
    }

    private UUID sampleVenueId() {
        return UUID.fromString("26e72fe6-7573-405f-803e-a7cb7b9387f7");
    }

    private Venue sampleVenue() {
        final Venue venue = new Venue();
        venue.setId(sampleVenueId());
        venue.setName("Grand City Concert Hall");
        venue.setAddress("125 Riverside Avenue, New York");
        return venue;
    }

    private VenueRequestDto sampleRequestDto() {
        final VenueRequestDto dto = new VenueRequestDto();
        dto.setName("Grand City Concert Hall");
        dto.setAddress("125 Riverside Avenue, New York");
        return dto;
    }

    private VenueResponseDto sampleResponseDto() {
        final VenueResponseDto dto = new VenueResponseDto();
        dto.setId(sampleVenueId());
        dto.setName("Grand City Concert Hall");
        dto.setAddress("125 Riverside Avenue, New York");
        return dto;
    }

    @Test
    void createVenueSuccess() {
        final VenueRequestDto request = sampleRequestDto();
        final Venue venue = sampleVenue();
        final VenueResponseDto response = sampleResponseDto();

        when(venueRepository.existsByName(request.getName())).thenReturn(false);
        when(venueRepository.existsByAddress(request.getAddress())).thenReturn(false);
        when(venueMapper.toEntity(request)).thenReturn(venue);
        when(venueRepository.save(venue)).thenReturn(venue);
        when(venueMapper.toDto(venue)).thenReturn(response);

        final VenueResponseDto result = venueService.createVenue(request);

        assertThat(result).isEqualTo(response);
        verify(venueRepository).save(venue);
    }

    @Test
    void createVenueDuplicateName() {
        final VenueRequestDto request = sampleRequestDto();

        when(venueRepository.existsByName(request.getName())).thenReturn(true);

        assertThatThrownBy(() -> venueService.createVenue(request))
                .isInstanceOf(DuplicateVenueNameException.class);

        verify(venueRepository, never()).save(any());
    }

    @Test
    void createVenueDuplicateAddress() {
        final VenueRequestDto request = sampleRequestDto();

        when(venueRepository.existsByName(request.getName())).thenReturn(false);
        when(venueRepository.existsByAddress(request.getAddress())).thenReturn(true);

        assertThatThrownBy(() -> venueService.createVenue(request))
                .isInstanceOf(DuplicateVenueAddressException.class);

        verify(venueRepository, never()).save(any());
    }

    @Test
    void getVenueByIdSuccess() {
        final Venue venue = sampleVenue();
        final VenueResponseDto response = sampleResponseDto();

        when(venueRepository.findById(sampleVenueId())).thenReturn(Optional.of(venue));
        when(venueMapper.toDto(venue)).thenReturn(response);

        final VenueResponseDto result = venueService.getVenueById(sampleVenueId());

        assertThat(result).isEqualTo(response);
    }

    @Test
    void getVenueByIdNotFound() {
        final UUID venueId = sampleVenueId();

        when(venueRepository.findById(sampleVenueId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> venueService.getVenueById(venueId))
                .isInstanceOf(VenueNotFoundException.class);
    }

    @Test
    void getVenueByNameSuccess() {
        final Venue venue = sampleVenue();
        final VenueResponseDto response = sampleResponseDto();

        when(venueRepository.findByName("Grand City Concert Hall")).thenReturn(Optional.of(venue));
        when(venueMapper.toDto(venue)).thenReturn(response);

        final VenueResponseDto result = venueService.getVenueByName("Grand City Concert Hall");

        assertThat(result).isEqualTo(response);
    }

    @Test
    void getVenueByNameNotFound() {
        when(venueRepository.findByName("Nonexistent Venue")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> venueService.getVenueByName("Nonexistent Venue"))
                .isInstanceOf(VenueNotFoundException.class);
    }

    @Test
    void getVenueByAddressSuccess() {
        final Venue venue = sampleVenue();
        final VenueResponseDto response = sampleResponseDto();

        when(venueRepository.findByAddress("125 Riverside Avenue, New York")).thenReturn(Optional.of(venue));
        when(venueMapper.toDto(venue)).thenReturn(response);

        final VenueResponseDto result = venueService.getVenueByAddress("125 Riverside Avenue, New York");

        assertThat(result).isEqualTo(response);
    }

    @Test
    void getVenueByAddressNotFound() {
        when(venueRepository.findByAddress("Nonexistent Address")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> venueService.getVenueByAddress("Nonexistent Address"))
                .isInstanceOf(VenueNotFoundException.class);
    }

    @Test
    void getAllVenuesSuccess() {
        final Venue venue1 = sampleVenue();
        final Venue venue2 = new Venue();
        venue2.setId(UUID.randomUUID());
        venue2.setName("Metropolitan Music Arena");
        venue2.setAddress("450 Madison Boulevard, New York");

        final List<Venue> venues = List.of(venue1, venue2);

        final VenueResponseDto response1 = sampleResponseDto();
        final VenueResponseDto response2 = new VenueResponseDto();
        response2.setId(venue2.getId());
        response2.setName("Metropolitan Music Arena");
        response2.setAddress("450 Madison Boulevard, New York");

        when(venueRepository.findAll()).thenReturn(venues);
        when(venueMapper.toDto(venue1)).thenReturn(response1);
        when(venueMapper.toDto(venue2)).thenReturn(response2);

        final List<VenueResponseDto> result = venueService.getAllVenues();

        assertThat(result).hasSize(2);
        assertThat(result.get(0)).isEqualTo(response1);
        assertThat(result.get(1)).isEqualTo(response2);
    }

    @Test
    void getAllVenuesEmpty() {
        when(venueRepository.findAll()).thenReturn(Collections.emptyList());

        final List<VenueResponseDto> result = venueService.getAllVenues();

        assertThat(result).isEmpty();
    }

    @Test
    void updateVenueByIdSuccess() {
        final VenueUpdateDto updateDto = new VenueUpdateDto();
        updateDto.setName("Updated Venue Name");
        updateDto.setAddress("Updated Address");

        final Venue venue = sampleVenue();
        final VenueResponseDto response = sampleResponseDto();
        response.setName("Updated Venue Name");
        response.setAddress("Updated Address");

        when(venueRepository.findById(sampleVenueId())).thenReturn(Optional.of(venue));
        when(venueRepository.existsByName("Updated Venue Name")).thenReturn(false);
        when(venueRepository.existsByAddress("Updated Address")).thenReturn(false);
        when(venueMapper.toDto(venue)).thenReturn(response);

        final VenueResponseDto result = venueService.updateVenueById(sampleVenueId(), updateDto);

        assertThat(result.getName()).isEqualTo("Updated Venue Name");
        assertThat(result.getAddress()).isEqualTo("Updated Address");
    }

    @Test
    void updateVenueByIdPartialUpdateName() {
        final VenueUpdateDto updateDto = new VenueUpdateDto();
        updateDto.setName("Updated Venue Name Only");

        final Venue venue = sampleVenue();
        final VenueResponseDto response = sampleResponseDto();
        response.setName("Updated Venue Name Only");

        when(venueRepository.findById(sampleVenueId())).thenReturn(Optional.of(venue));
        when(venueRepository.existsByName("Updated Venue Name Only")).thenReturn(false);
        when(venueMapper.toDto(venue)).thenReturn(response);

        final VenueResponseDto result = venueService.updateVenueById(sampleVenueId(), updateDto);

        assertThat(result.getName()).isEqualTo("Updated Venue Name Only");
        assertThat(result.getAddress()).isEqualTo("125 Riverside Avenue, New York");
    }

    @Test
    void updateVenueByIdPartialUpdateAddress() {
        final VenueUpdateDto updateDto = new VenueUpdateDto();
        updateDto.setAddress("Updated Address Only");

        final Venue venue = sampleVenue();
        final VenueResponseDto response = sampleResponseDto();
        response.setAddress("Updated Address Only");

        when(venueRepository.findById(sampleVenueId())).thenReturn(Optional.of(venue));
        when(venueRepository.existsByAddress("Updated Address Only")).thenReturn(false);
        when(venueMapper.toDto(venue)).thenReturn(response);

        final VenueResponseDto result = venueService.updateVenueById(sampleVenueId(), updateDto);

        assertThat(result.getName()).isEqualTo("Grand City Concert Hall");
        assertThat(result.getAddress()).isEqualTo("Updated Address Only");
    }

    @Test
    void updateVenueByIdNotFound() {
        final VenueUpdateDto updateDto = new VenueUpdateDto();
        updateDto.setName("Updated Name");
        final UUID venueId = sampleVenueId();

        when(venueRepository.findById(sampleVenueId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> venueService.updateVenueById(venueId, updateDto))
                .isInstanceOf(VenueNotFoundException.class);
    }

    @Test
    void updateVenueByIdDuplicateName() {
        final VenueUpdateDto updateDto = new VenueUpdateDto();
        updateDto.setName("Existing Venue Name");

        final Venue venue = sampleVenue();
        final UUID venueId = sampleVenueId();

        when(venueRepository.findById(sampleVenueId())).thenReturn(Optional.of(venue));
        when(venueRepository.existsByName("Existing Venue Name")).thenReturn(true);

        assertThatThrownBy(() -> venueService.updateVenueById(venueId, updateDto))
                .isInstanceOf(DuplicateVenueNameException.class);
    }

    @Test
    void updateVenueByIdSameNameNoDuplicateCheck() {
        final VenueUpdateDto updateDto = new VenueUpdateDto();
        updateDto.setName("Grand City Concert Hall");

        final Venue venue = sampleVenue();
        final VenueResponseDto response = sampleResponseDto();

        when(venueRepository.findById(sampleVenueId())).thenReturn(Optional.of(venue));
        when(venueMapper.toDto(venue)).thenReturn(response);

        final VenueResponseDto result = venueService.updateVenueById(sampleVenueId(), updateDto);

        assertThat(result).isEqualTo(response);
        verify(venueRepository, never()).existsByName("Grand City Concert Hall");
    }

    @Test
    void updateVenueByIdDuplicateAddress() {
        final VenueUpdateDto updateDto = new VenueUpdateDto();
        updateDto.setAddress("Existing Address");

        final Venue venue = sampleVenue();
        final UUID venueId = sampleVenueId();

        when(venueRepository.findById(sampleVenueId())).thenReturn(Optional.of(venue));
        when(venueRepository.existsByAddress("Existing Address")).thenReturn(true);

        assertThatThrownBy(() -> venueService.updateVenueById(venueId, updateDto))
                .isInstanceOf(DuplicateVenueAddressException.class);
    }

    @Test
    void updateVenueByIdSameAddressNoDuplicateCheck() {
        final VenueUpdateDto updateDto = new VenueUpdateDto();
        updateDto.setAddress("125 Riverside Avenue, New York");

        final Venue venue = sampleVenue();
        final VenueResponseDto response = sampleResponseDto();

        when(venueRepository.findById(sampleVenueId())).thenReturn(Optional.of(venue));
        when(venueRepository.existsByAddress("125 Riverside Avenue, New York")).thenReturn(true);
        when(venueMapper.toDto(venue)).thenReturn(response);

        final VenueResponseDto result = venueService.updateVenueById(sampleVenueId(), updateDto);

        assertThat(result).isEqualTo(response);
        verify(venueRepository).existsByAddress("125 Riverside Avenue, New York");
    }

    @Test
    void deleteVenueByIdSuccess() {
        final Venue venue = sampleVenue();

        when(venueRepository.findById(sampleVenueId())).thenReturn(Optional.of(venue));

        venueService.deleteVenueById(sampleVenueId());

        verify(venueRepository).delete(venue);
    }

    @Test
    void deleteVenueByIdNotFound() {
        final UUID venueId = sampleVenueId();

        when(venueRepository.findById(sampleVenueId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> venueService.deleteVenueById(venueId))
                .isInstanceOf(VenueNotFoundException.class);
    }
}