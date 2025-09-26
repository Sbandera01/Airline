package com.example.airline.api.mapper;

import com.example.airline.api.dto.FlightDtos;
import com.example.airline.api.dto.AirlineDtos;
import com.example.airline.api.dto.AirportDtos;
import com.example.airline.api.dto.TagDtos;
import com.example.airline.domain.entities.Flight;

import java.util.Set;
import java.util.stream.Collectors;

public class FlightMapper {

    public static Flight toEntity(FlightDtos.FlightCreateRequest dto) {
        if (dto == null) return null;
        Flight flight = new Flight();
        flight.setNumber(dto.number());
        flight.setDepartureTime(dto.departureTime());
        flight.setArrivalTime(dto.arrivalTime());
        // airline, origin, destination y tags deben setearse en el service
        return flight;
    }

    public static FlightDtos.FlightResponse toResponse(Flight entity) {
        if (entity == null) return null;
        Set<TagDtos.TagResponse> tags = entity.getTags() == null ? Set.of() :
                entity.getTags().stream().map(TagMapper::toResponse).collect(Collectors.toSet());
        return new FlightDtos.FlightResponse(
                entity.getId(),
                entity.getNumber(),
                entity.getDepartureTime(),
                entity.getArrivalTime(),
                AirlineMapper.toResponse(entity.getAirline()),
                AirportMapper.toResponse(entity.getOrigin()),
                AirportMapper.toResponse(entity.getDestination()),
                tags
        );
    }
}

