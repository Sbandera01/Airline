package com.example.airline.api.mapper;

import com.example.airline.api.dto.PassengerDtos;
import com.example.airline.domain.entities.Passenger;
import com.example.airline.domain.entities.PassengerProfile;

public class PassengerMapper {

    public static Passenger toEntity(PassengerDtos.PassengerCreateRequest dto) {
        if (dto == null) return null;
        PassengerProfile profile = null;
        if (dto.profile() != null) {
            profile = new PassengerProfile();
            profile.setPhone(dto.profile().phone());
            profile.setCountryCode(dto.profile().countryCode());
        }
        return Passenger.builder()
                .fullName(dto.fullName())
                .email(dto.email())
                .profile(profile)
                .build();
    }

    public static PassengerDtos.PassengerResponse toResponse(Passenger entity) {
        if (entity == null) return null;
        PassengerDtos.PassengerProfileResponse profileDto = null;
        if (entity.getProfile() != null) {
            profileDto = new PassengerDtos.PassengerProfileResponse(
                    entity.getProfile().getId(),
                    entity.getProfile().getPhone(),
                    entity.getProfile().getCountryCode()
            );
        }
        return new PassengerDtos.PassengerResponse(
                entity.getId(),
                entity.getFullName(),
                entity.getEmail(),
                profileDto
        );
    }
}


