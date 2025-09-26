package com.example.airline.domain.repositories;

import com.example.airline.BaseIntegrationTest;
import com.example.airline.domain.entities.Airline;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class AirlineRepositoryTest extends BaseIntegrationTest {

    @Autowired
    AirlineRepository airlineRepository;

    @Test
    void shouldFindAirlineByCode() {
        airlineRepository.save(Airline.builder().code("AV").name("Avianca").build());

        Optional<Airline> found = airlineRepository.findByCode("AV");

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Avianca");
    }
}

