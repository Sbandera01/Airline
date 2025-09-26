package com.example.airline.domain.repositories;

import com.example.airline.BaseIntegrationTest;
import com.example.airline.domain.entities.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class SeatInventoryRepositoryTest extends BaseIntegrationTest {

    @Autowired SeatInventoryRepository seatInventoryRepository;
    @Autowired FlightRepository flightRepository;
    @Autowired AirlineRepository airlineRepository;
    @Autowired AirportRepository airportRepository;

    @Test
    void shouldFindInventoryByFlightAndCabin() {
        Flight flight = setupFlight();
        seatInventoryRepository.save(
                SeatInventory.builder().flight(flight).cabin("ECONOMY").totalSeats(100).availableSeats(80).build()
        );

        Optional<SeatInventory> found = seatInventoryRepository.findByFlight_IdAndCabin(flight.getId(), "ECONOMY");

        assertThat(found).isPresent();
        assertThat(found.get().getAvailableSeats()).isEqualTo(80);
    }

    @Test
    void shouldVerifyAvailableSeats() {
        Flight flight = setupFlight();
        seatInventoryRepository.save(
                SeatInventory.builder().flight(flight).cabin("BUSINESS").totalSeats(20).availableSeats(5).build()
        );

        boolean hasSeats = seatInventoryRepository.hasAvailableSeats(flight.getId(), "BUSINESS", 3);

        assertThat(hasSeats).isTrue();
    }

    private Flight setupFlight() {
        Airline airline = airlineRepository.save(Airline.builder().code("AV").name("Avianca").build());
        Airport origin = airportRepository.save(Airport.builder().code("BOG").name("El Dorado").city("Bogotá").build());
        Airport destination = airportRepository.save(Airport.builder().code("MIA").name("Miami").city("Miami").build());

        return flightRepository.save(
                Flight.builder()
                        .number("AV300")
                        .airline(airline)
                        .origin(origin)
                        .destination(destination)
                        .departureTime(OffsetDateTime.now().plusDays(1))
                        .arrivalTime(OffsetDateTime.now().plusDays(1).plusHours(3))
                        .build()
        );
    }
}

