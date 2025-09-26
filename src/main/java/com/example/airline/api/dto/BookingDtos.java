package com.example.airline.api.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public class BookingDtos {

    public record BookingCreateRequest(String passengerEmail, List<BookingItemRequest> items)
            implements Serializable {}

    public record BookingItemRequest(Long flightId, String cabin, BigDecimal price, Integer segmentOrder)
            implements Serializable {}

    public record BookingResponse(Long id, OffsetDateTime createdAt, PassengerDtos.PassengerResponse passenger,
                                  List<BookingItemResponse> items)
            implements Serializable {}

    public record BookingItemResponse(Long id, String cabin, BigDecimal price, Integer segmentOrder,
                                      FlightDtos.FlightResponse flight)
            implements Serializable {}
}
