package com.example.airline.api.mapper;

import com.example.airline.api.dto.AirportDtos.*;
import com.example.airline.domain.entities.Airport;
import org.mapstruct.*;

@Mapper(config = MapStructConfig.class, componentModel = "spring")
public interface AirportMapper {

    @Mapping(target = "id", ignore = true)
    Airport toEntity(AirportCreateRequest dto);

    AirportResponse toResponse(Airport entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget Airport entity, AirportCreateRequest dto);
}

