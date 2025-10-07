package com.example.airline.api.mapper;

import com.example.airline.api.dto.AirlineDtos;
import com.example.airline.domain.entities.Airline;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AirlineMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "flights",ignore = true)
    Airline toEntity(AirlineDtos.AirlineCreateRequest dto);

    AirlineDtos.AirlineResponse toResponse(Airline entity);
}


