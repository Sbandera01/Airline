package com.example.airline.api.mapper;

import com.example.airline.api.dto.SeatInventoryDtos.*;
import com.example.airline.domain.entities.SeatInventory;
import com.example.airline.domain.entities.Flight;
import com.example.airline.domain.repositories.FlightRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(config = MapStructConfig.class)
public abstract class SeatInventoryMapper {

    @Autowired
    protected FlightRepository flightRepository;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "availableSeats", source = "totalSeats")
    @Mapping(target = "flight", source = "flightId", qualifiedByName = "flightIdToEntity")
    public abstract SeatInventory toEntity(SeatInventoryCreateRequest dto);

    public abstract SeatInventoryResponse toResponse(SeatInventory entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "flight", ignore = true)
    @Mapping(target = "availableSeats", ignore = true)
    public abstract void updateEntity(@MappingTarget SeatInventory entity, SeatInventoryCreateRequest dto);

    @Named("flightIdToEntity")
    protected Flight mapFlightId(Long flightId) {
        return flightId == null ? null :
                flightRepository.findById(flightId)
                        .orElseThrow(() -> new IllegalArgumentException("Flight not found with id: " + flightId));
    }
}