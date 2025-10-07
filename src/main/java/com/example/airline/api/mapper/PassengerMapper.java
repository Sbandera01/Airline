package com.example.airline.api.mapper;

import com.example.airline.api.dto.PassengerDtos;
import com.example.airline.domain.entities.Passenger;
import com.example.airline.domain.entities.PassengerProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PassengerMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "profile", source = "profile")
    Passenger toEntity(PassengerDtos.PassengerCreateRequest dto);

    @Mapping(target = "id",ignore = true)
    PassengerProfile toProfileEntity(PassengerDtos.PassengerProfileRequest dto);

    PassengerDtos.PassengerResponse toResponse(Passenger entity);

    PassengerDtos.PassengerProfileResponse toProfileResponse(PassengerProfile entity);
}
