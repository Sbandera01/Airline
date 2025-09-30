package com.example.airline.api.service;

import com.example.airline.api.dto.SeatInventoryDtos;
import java.util.List;

public interface SeatInventoryService {
    SeatInventoryDtos.SeatInventoryResponse create(SeatInventoryDtos.SeatInventoryCreateRequest request);
    SeatInventoryDtos.SeatInventoryResponse findById(Long id);
    SeatInventoryDtos.SeatInventoryResponse findByFlightAndCabin(Long flightId, String cabin);
    List<SeatInventoryDtos.SeatInventoryResponse> findAll();
    boolean hasAvailableSeats(Long flightId, String cabin, int minimumSeats);
    SeatInventoryDtos.SeatInventoryResponse decreaseAvailability(Long flightId, String cabin, int quantity);
    SeatInventoryDtos.SeatInventoryResponse increaseAvailability(Long flightId, String cabin, int quantity);
    SeatInventoryDtos.SeatInventoryResponse update(Long id, SeatInventoryDtos.SeatInventoryCreateRequest request);
    void delete(Long id);
}
