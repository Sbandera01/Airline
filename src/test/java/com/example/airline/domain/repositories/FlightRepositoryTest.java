package com.example.airline.domain.repositories;

import com.example.airline.BaseIntegrationTest;
import com.example.airline.domain.entities.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FlightRepositoryTest extends BaseIntegrationTest {

    @Autowired FlightRepository flightRepository;
    @Autowired AirlineRepository airlineRepository;
    @Autowired AirportRepository airportRepository;

    @Test
    void shouldFindFlightsByAirlineName() {
        Airline airline = airlineRepository.save(Airline.builder().code("AV").name("Avianca").build());
        Airport origin = airportRepository.save(Airport.builder().code("BOG").name("El Dorado").city("Bogotá").build());
        Airport destination = airportRepository.save(Airport.builder().code("MIA").name("Miami Intl").city("Miami").build());

        flightRepository.save(
                Flight.builder()
                        .number("AV120")
                        .airline(airline)
                        .origin(origin)
                        .destination(destination)
                        .departureTime(OffsetDateTime.now().plusDays(1))
                        .arrivalTime(OffsetDateTime.now().plusDays(1).plusHours(3))
                        .build()
        );

        List<Flight> flights = flightRepository.findByAirline_Name("Avianca");

        assertThat(flights).hasSize(1);
        assertThat(flights.get(0).getNumber()).isEqualTo("AV120");
    }

    @Test
    void shouldFindFlightsByOriginDestinationAndWindow() {
        Airline airline = airlineRepository.save(Airline.builder().code("AV").name("Avianca").build());
        Airport origin = airportRepository.save(Airport.builder().code("BOG").name("El Dorado").city("Bogotá").build());
        Airport destination = airportRepository.save(Airport.builder().code("MAD").name("Barajas").city("Madrid").build());

        flightRepository.save(
                Flight.builder()
                        .number("AV10")
                        .airline(airline)
                        .origin(origin)
                        .destination(destination)
                        .departureTime(OffsetDateTime.now().plusDays(2))
                        .arrivalTime(OffsetDateTime.now().plusDays(2).plusHours(10))
                        .build()
        );

        Page<Flight> result = flightRepository.findByOrigin_CodeAndDestination_CodeAndDepartureTimeBetween(
                "BOG", "MAD",
                OffsetDateTime.now().plusDays(1),
                OffsetDateTime.now().plusDays(3),
                PageRequest.of(0, 5)
        );

        assertThat(result.getContent()).hasSize(1);
    }
}

