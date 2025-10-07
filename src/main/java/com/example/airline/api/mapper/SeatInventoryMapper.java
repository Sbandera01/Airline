package com.example.airline.api.mapper;

import com.example.airline.api.dto.SeatInventoryDtos;
import com.example.airline.domain.entities.SeatInventory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SeatInventoryMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "flight", ignore = true)
    @Mapping(target = "availableSeats", source = "totalSeats")
    SeatInventory toEntity(SeatInventoryDtos.SeatInventoryCreateRequest dto);

    SeatInventoryDtos.SeatInventoryResponse toResponse(SeatInventory entity);
}
