package com.example.airline.api.services;

import com.example.airline.api.dto.FlightDtos;
import com.example.airline.api.mapper.FlightMapper;
import com.example.airline.domain.entities.*;
import com.example.airline.domain.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FlightServiceImpl implements FlightService {

    private final FlightRepository flightRepository;
    private final AirlineRepository airlineRepository;
    private final AirportRepository airportRepository;
    private final TagRepository tagRepository;
    private final FlightMapper flightMapper;

    @Override
    @Transactional
    public FlightDtos.FlightResponse create(FlightDtos.FlightCreateRequest request) {
        Flight flight = flightMapper.toEntity(request);

        // Asociar aerolínea
        Airline airline = airlineRepository.findById(request.airlineId())
                .orElseThrow(() -> new IllegalArgumentException("Airline not found with id: " + request.airlineId()));
        flight.setAirline(airline);

        // Asociar aeropuerto de origen
        Airport origin = airportRepository.findById(request.originAirportId())
                .orElseThrow(() -> new IllegalArgumentException("Origin airport not found with id: " + request.originAirportId()));
        flight.setOrigin(origin);

        // Asociar aeropuerto de destino
        Airport destination = airportRepository.findById(request.destinationAirportId())
                .orElseThrow(() -> new IllegalArgumentException("Destination airport not found with id: " + request.destinationAirportId()));
        flight.setDestination(destination);

        // Asociar tags (crear si no existen)
        if (request.tags() != null && !request.tags().isEmpty()) {
            Set<Tag> tags = new HashSet<>();
            for (String tagName : request.tags()) {
                Tag tag = tagRepository.findByName(tagName)
                        .orElseGet(() -> tagRepository.save(Tag.builder().name(tagName).build()));
                tags.add(tag);
            }
            flight.setTags(tags);
        }


        return flightMapper.toResponse(flightRepository.save(flight));
    }

    @Override
    public FlightDtos.FlightResponse findById(Long id) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Flight not found with id: " + id));
        return flightMapper.toResponse(flight);
    }

    @Override
    public List<FlightDtos.FlightResponse> findAll() {
        return flightRepository.findAll().stream()
                .map(flightMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<FlightDtos.FlightResponse> findByAirlineName(String airlineName) {
        return flightRepository.findByAirline_Name(airlineName).stream()
                .map(flightMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<FlightDtos.FlightResponse> searchFlights(String origin, String destination,
                                                         OffsetDateTime from, OffsetDateTime to,
                                                         Pageable pageable) {
        return flightRepository.findByOrigin_CodeAndDestination_CodeAndDepartureTimeBetween(
                        origin, destination, from, to, pageable)
                .map(flightMapper::toResponse);
    }

    @Override
    public List<FlightDtos.FlightResponse> searchWithAssociations(String origin, String destination,
                                                                  OffsetDateTime from, OffsetDateTime to) {
        return flightRepository.searchWithAssociations(origin, destination, from, to).stream()
                .map(flightMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<FlightDtos.FlightResponse> findFlightsWithAllTags(List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return List.of();
        }
        return flightRepository.findFlightsWithAllTags(tags, tags.size()).stream()
                .map(flightMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public FlightDtos.FlightResponse update(Long id, FlightDtos.FlightCreateRequest request) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Flight not found with id: " + id));

        flight.setNumber(request.number());
        flight.setDepartureTime(request.departureTime());
        flight.setArrivalTime(request.arrivalTime());

        // Actualizar aerolínea
        Airline airline = airlineRepository.findById(request.airlineId())
                .orElseThrow(() -> new IllegalArgumentException("Airline not found with id: " + request.airlineId()));
        flight.setAirline(airline);

        // Actualizar origen
        Airport origin = airportRepository.findById(request.originAirportId())
                .orElseThrow(() -> new IllegalArgumentException("Origin airport not found with id: " + request.originAirportId()));
        flight.setOrigin(origin);

        // Actualizar destino
        Airport destination = airportRepository.findById(request.destinationAirportId())
                .orElseThrow(() -> new IllegalArgumentException("Destination airport not found with id: " + request.destinationAirportId()));
        flight.setDestination(destination);

        // Actualizar tags
        if (request.tags() != null) {
            Set<Tag> tags = new HashSet<>();
            for (String tagName : request.tags()) {
                Tag tag = tagRepository.findByName(tagName)
                        .orElseGet(() -> tagRepository.save(Tag.builder().name(tagName).build()));
                tags.add(tag);
            }
            flight.setTags(tags);
        }

        flight = flightRepository.save(flight);
        return flightMapper.toResponse(flight);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!flightRepository.existsById(id)) {
            throw new IllegalArgumentException("Flight not found with id: " + id);
        }
        flightRepository.deleteById(id);
    }
}