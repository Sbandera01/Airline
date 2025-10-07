package com.example.airline.api.mapper;

import com.example.airline.api.dto.FlightDtos;
import com.example.airline.domain.entities.Flight;
import org.mapstruct.*;


@Mapper(componentModel = "spring", uses = {AirlineMapper.class, AirportMapper.class, TagMapper.class})
public interface FlightMapper {

    @Mapping(target = "id",ignore = true)
    @Mapping(target = "airline", ignore = true)
    @Mapping(target = "origin", ignore = true)
    @Mapping(target = "destination", ignore = true)
    @Mapping(target = "tags", ignore = true)
    Flight toEntity(FlightDtos.FlightCreateRequest dto);

    FlightDtos.FlightResponse toResponse(Flight entity);
}
