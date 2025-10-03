package com.example.airline.api.services;

import com.example.airline.api.dto.AirlineDtos;
import com.example.airline.api.mapper.AirlineMapper;
import com.example.airline.domain.entities.Airline;
import com.example.airline.domain.repositories.AirlineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AirlineServiceImpl implements AirlineService {

    private final AirlineRepository airlineRepository;

    @Override
    @Transactional
    public AirlineDtos.AirlineResponse create(AirlineDtos.AirlineCreateRequest request) {
        airlineRepository.findByCode(request.code()).ifPresent(a -> {
            throw new IllegalArgumentException("Airline with code " + request.code() + " already exists");
        });

        Airline airline = AirlineMapper.toEntity(request);
        airline = airlineRepository.save(airline);
        return AirlineMapper.toResponse(airline);
    }

    @Override
    public AirlineDtos.AirlineResponse findById(Long id) {
        Airline airline = airlineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Airline not found with id: " + id));
        return AirlineMapper.toResponse(airline);
    }

    @Override
    public AirlineDtos.AirlineResponse findByCode(String code) {
        Airline airline = airlineRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Airline not found with code: " + code));
        return AirlineMapper.toResponse(airline);
    }

    @Override
    public List<AirlineDtos.AirlineResponse> findAll() {
        return airlineRepository.findAll().stream()
                .map(AirlineMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public AirlineDtos.AirlineResponse update(Long id, AirlineDtos.AirlineCreateRequest request) {
        Airline airline = airlineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Airline not found with id: " + id));

        airline.setCode(request.code());
        airline.setName(request.name());
        airline = airlineRepository.save(airline);
        return AirlineMapper.toResponse(airline);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!airlineRepository.existsById(id)) {
            throw new IllegalArgumentException("Airline not found with id: " + id);
        }
        airlineRepository.deleteById(id);
    }
}
