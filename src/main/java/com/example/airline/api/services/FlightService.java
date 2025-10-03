package com.example.airline.api.services;

import com.example.airline.api.dto.FlightDtos;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.util.List;

public interface FlightService {
    FlightDtos.FlightResponse create(FlightDtos.FlightCreateRequest request);
    FlightDtos.FlightResponse findById(Long id);
    List<FlightDtos.FlightResponse> findAll();
    List<FlightDtos.FlightResponse> findByAirlineName(String airlineName);
    Page<FlightDtos.FlightResponse> searchFlights(String origin, String destination,
                                                  OffsetDateTime from, OffsetDateTime to,
                                                  Pageable pageable);
    List<FlightDtos.FlightResponse> searchWithAssociations(String origin, String destination,
                                                           OffsetDateTime from, OffsetDateTime to);
    List<FlightDtos.FlightResponse> findFlightsWithAllTags(List<String> tags);
    FlightDtos.FlightResponse update(Long id, FlightDtos.FlightCreateRequest request);
    void delete(Long id);
}