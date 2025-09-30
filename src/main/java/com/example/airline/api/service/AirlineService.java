package com.example.airline.api.service;

import com.example.airline.api.dto.AirlineDtos;
import java.util.List;

public interface AirlineService {
    AirlineDtos.AirlineResponse create(AirlineDtos.AirlineCreateRequest request);
    AirlineDtos.AirlineResponse findById(Long id);
    AirlineDtos.AirlineResponse findByCode(String code);
    List<AirlineDtos.AirlineResponse> findAll();
    AirlineDtos.AirlineResponse update(Long id, AirlineDtos.AirlineCreateRequest request);
    void delete(Long id);
}
