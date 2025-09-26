package com.example.airline.api.mapper;

import com.example.airline.api.dto.AirportDtos;
import com.example.airline.domain.entities.Airport;

public class AirportMapper {

    public static Airport toEntity(AirportDtos.AirportCreateRequest dto) {
        if (dto == null) return null;
        return Airport.builder()
                .code(dto.code())
                .name(dto.name())
                .city(dto.city())
                .build();
    }

    public static AirportDtos.AirportResponse toResponse(Airport entity) {
        if (entity == null) return null;
        return new AirportDtos.AirportResponse(
                entity.getId(),
                entity.getCode(),
                entity.getName(),
                entity.getCity()
        );
    }
}

