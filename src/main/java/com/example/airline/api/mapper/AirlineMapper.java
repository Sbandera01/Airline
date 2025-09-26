package com.example.airline.api.mapper;

import com.example.airline.api.dto.AirlineDtos;
import com.example.airline.domain.entities.Airline;

public class AirlineMapper {

    public static Airline toEntity(AirlineDtos.AirlineCreateRequest dto) {
        if (dto == null) return null;
        return Airline.builder()
                .code(dto.code())
                .name(dto.name())
                .build();
    }

    public static AirlineDtos.AirlineResponse toResponse(Airline entity) {
        if (entity == null) return null;
        return new AirlineDtos.AirlineResponse(entity.getId(), entity.getCode(), entity.getName());
    }
}


