package com.example.airline.domain.repositories;

import com.example.airline.BaseIntegrationTest;
import com.example.airline.domain.entities.Airport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class AirportRepositoryTest extends BaseIntegrationTest {

    @Autowired
    AirportRepository airportRepository;

    @Test
    void shouldFindAirportByCode() {
        airportRepository.save(Airport.builder().code("BOG").name("El Dorado").city("Bogotá").build());

        Optional<Airport> found = airportRepository.findByCode("BOG");

        assertThat(found).isPresent();
        assertThat(found.get().getCity()).isEqualTo("Bogotá");
    }
}

