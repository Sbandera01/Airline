package com.example.airline.api.dto;

import java.io.Serializable;

public class PassengerDtos {

    public record PassengerCreateRequest(String fullName, String email, PassengerProfileRequest profile)
            implements Serializable {}

    public record PassengerProfileRequest(String phone, String countryCode)
            implements Serializable {}

    public record PassengerResponse(Long id, String fullName, String email, PassengerProfileResponse profile)
            implements Serializable {}

    public record PassengerProfileResponse(Long id, String phone, String countryCode)
            implements Serializable {}
}
