package com.example.airline.api.mapper;

import com.example.airline.api.dto.AirportDtos;
import com.example.airline.domain.entities.Airport;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AirportMapper {

    @Mapping(target = "id", ignore = true)
    Airport toEntity(AirportDtos.AirportCreateRequest dto);

    AirportDtos.AirportResponse toResponse(Airport entity);
}

