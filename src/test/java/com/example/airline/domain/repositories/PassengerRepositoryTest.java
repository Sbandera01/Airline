package com.example.airline.domain.repositories;

import com.example.airline.BaseIntegrationTest;
import com.example.airline.domain.entities.Passenger;
import com.example.airline.domain.entities.PassengerProfile;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class PassengerRepositoryTest extends BaseIntegrationTest {

    @Autowired
    PassengerRepository passengerRepository;

    @Test
    void shouldFindPassengerByEmailIgnoreCase() {
        PassengerProfile profile = PassengerProfile.builder()
                .phone("12345")
                .countryCode("CO")
                .build();

        Passenger saved = passengerRepository.save(
                Passenger.builder()
                        .fullName("Jane Doe")
                        .email("jane@example.com")
                        .profile(profile)
                        .build()
        );

        Optional<Passenger> found = passengerRepository.findByEmailIgnoreCase("JANE@EXAMPLE.COM");

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(saved.getId());
    }

    @Test
    void shouldFetchPassengerWithProfile() {
        PassengerProfile profile = PassengerProfile.builder()
                .phone("98765")
                .countryCode("US")
                .build();

        passengerRepository.save(
                Passenger.builder()
                        .fullName("Mark Smith")
                        .email("mark@example.com")
                        .profile(profile)
                        .build()
        );

        Optional<Passenger> found = passengerRepository.findByEmailIgnoreCaseWithProfile("mark@example.com");

        assertThat(found).isPresent();
        assertThat(found.get().getProfile()).isNotNull();
        assertThat(found.get().getProfile().getPhone()).isEqualTo("98765");
    }
}

