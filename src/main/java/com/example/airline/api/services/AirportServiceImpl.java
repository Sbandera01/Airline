package com.example.airline.api.services;

import com.example.airline.api.dto.AirportDtos;
import com.example.airline.api.mapper.AirportMapper;
import com.example.airline.domain.entities.Airport;
import com.example.airline.domain.repositories.AirportRepository;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AirportServiceImpl implements AirportService {

    private final AirportRepository airportRepository;
    private final AirportMapper airportMapper;

    @Override
    @Transactional
    public AirportDtos.AirportResponse create(AirportDtos.AirportCreateRequest request) {
        airportRepository.findByCode(request.code()).ifPresent(a -> {
            throw new IllegalArgumentException("Airport with code " + request.code() + " already exists");
        });

        Airport airport = airportMapper.toEntity(request);
        airport = airportRepository.save(airport);
        return airportMapper.toResponse(airport);
    }

    @Override
    public AirportDtos.AirportResponse findById(Long id) {
        Airport airport = airportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Airport not found with id: " + id));
        return airportMapper.toResponse(airport);
    }

    @Override
    public AirportDtos.AirportResponse findByCode(String code) {
        Airport airport = airportRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Airport not found with code: " + code));
        return airportMapper.toResponse(airport);
    }

    @Override
    public List<AirportDtos.AirportResponse> findAll() {
        return airportRepository.findAll().stream()
                .map(airportMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AirportDtos.AirportResponse update(Long id, AirportDtos.AirportCreateRequest request) {
        Airport airport = airportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Airport not found with id: " + id));

        airport.setCode(request.code());
        airport.setName(request.name());
        airport.setCity(request.city());
        airport = airportRepository.save(airport);
        return airportMapper.toResponse(airport);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!airportRepository.existsById(id)) {
            throw new IllegalArgumentException("Airport not found with id: " + id);
        }
        airportRepository.deleteById(id);
    }
}
