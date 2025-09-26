package com.example.airline.api.mapper;

import com.example.airline.api.dto.SeatInventoryDtos;
import com.example.airline.domain.entities.SeatInventory;

public class SeatInventoryMapper {

    public static SeatInventory toEntity(SeatInventoryDtos.SeatInventoryCreateRequest dto) {
        if (dto == null) return null;
        return SeatInventory.builder()
                .cabin(dto.cabin())
                .totalSeats(dto.totalSeats())
                .availableSeats(dto.totalSeats()) // inicialmente disponibles = total
                .build();
    }

    public static SeatInventoryDtos.SeatInventoryResponse toResponse(SeatInventory entity) {
        if (entity == null) return null;
        return new SeatInventoryDtos.SeatInventoryResponse(
                entity.getId(),
                entity.getCabin(),
                entity.getTotalSeats(),
                entity.getAvailableSeats()
        );
    }
}

