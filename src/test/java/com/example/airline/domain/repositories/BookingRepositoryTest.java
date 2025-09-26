package com.example.airline.domain.repositories;

import com.example.airline.BaseIntegrationTest;
import com.example.airline.domain.entities.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class BookingRepositoryTest extends BaseIntegrationTest {

    @Autowired BookingRepository bookingRepository;
    @Autowired PassengerRepository passengerRepository;

    @Test
    void shouldPageBookingsByPassengerEmail() {
        Passenger passenger = passengerRepository.save(
                Passenger.builder().fullName("Laura").email("laura@example.com").build()
        );

        bookingRepository.save(Booking.builder().passenger(passenger).createdAt(OffsetDateTime.now()).build());

        var result = bookingRepository.findByPassenger_EmailIgnoreCaseOrderByCreatedAtDesc(
                "LAURA@example.com", PageRequest.of(0, 5)
        );

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void shouldFetchBookingWithDetails() {
        Passenger passenger = passengerRepository.save(
                Passenger.builder().fullName("Tom").email("tom@example.com").build()
        );

        Booking booking = bookingRepository.save(
                Booking.builder().passenger(passenger).createdAt(OffsetDateTime.now()).build()
        );

        Optional<Booking> found = bookingRepository.findByIdWithDetails(booking.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getPassenger().getEmail()).isEqualTo("tom@example.com");
    }
}

