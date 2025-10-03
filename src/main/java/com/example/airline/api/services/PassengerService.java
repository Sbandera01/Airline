package com.example.airline.api.services;

import com.example.airline.api.dto.PassengerDtos;
import java.util.List;

public interface PassengerService {
    PassengerDtos.PassengerResponse create(PassengerDtos.PassengerCreateRequest request);
    PassengerDtos.PassengerResponse findById(Long id);
    PassengerDtos.PassengerResponse findByEmail(String email);
    List<PassengerDtos.PassengerResponse> findAll();
    PassengerDtos.PassengerResponse update(Long id, PassengerDtos.PassengerCreateRequest request);
    void delete(Long id);
}