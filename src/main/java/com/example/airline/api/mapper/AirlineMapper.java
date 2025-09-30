package com.example.airline.api.mapper;

import com.example.airline.api.dto.AirlineDtos.*;
import com.example.airline.domain.entities.Airline;
import org.mapstruct.*;

@Mapper(config = MapStructConfig.class, componentModel = "spring")
public interface AirlineMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "flights", ignore = true)
    Airline toEntity(AirlineCreateRequest dto);

    AirlineResponse toResponse(Airline entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget Airline entity, AirlineCreateRequest dto);
}



