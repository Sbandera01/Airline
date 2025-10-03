package com.example.airline.api.services;

import com.example.airline.api.dto.AirportDtos;
import java.util.List;

public interface AirportService {
    AirportDtos.AirportResponse create(AirportDtos.AirportCreateRequest request);
    AirportDtos.AirportResponse findById(Long id);
    AirportDtos.AirportResponse findByCode(String code);
    List<AirportDtos.AirportResponse> findAll();
    AirportDtos.AirportResponse update(Long id, AirportDtos.AirportCreateRequest request);
    void delete(Long id);
}
