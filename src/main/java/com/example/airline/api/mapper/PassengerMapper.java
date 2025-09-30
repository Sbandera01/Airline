package com.example.airline.api.mapper;

import com.example.airline.api.dto.PassengerDtos.*;
import com.example.airline.domain.entities.Passenger;
import com.example.airline.domain.entities.PassengerProfile;
import org.mapstruct.*;

@Mapper(config = MapStructConfig.class, componentModel = "spring")
public interface PassengerMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "profile", source = "profile")
    Passenger toEntity(PassengerCreateRequest dto);

    @Mapping(target = "id", ignore = true)
    PassengerProfile toProfileEntity(PassengerProfileRequest dto);

    PassengerResponse toResponse(Passenger entity);

    PassengerProfileResponse toProfileResponse(PassengerProfile entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "profile", ignore = true)
    void updateEntity(@MappingTarget Passenger entity, PassengerCreateRequest dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProfileEntity(@MappingTarget PassengerProfile entity, PassengerProfileRequest dto);
}

