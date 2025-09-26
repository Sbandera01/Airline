package com.example.airline.api.dto;

import java.io.Serializable;

public class SeatInventoryDtos {

    public record SeatInventoryCreateRequest(Long flightId, String cabin, Integer totalSeats) implements Serializable {}

    public record SeatInventoryResponse(Long id, String cabin, Integer totalSeats, Integer availableSeats)
            implements Serializable {}
}
