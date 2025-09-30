package com.example.airline.api.serviceImpl;

import com.example.airline.api.dto.AirportDtos;
import com.example.airline.api.mapper.AirportMapper;
import com.example.airline.api.service.AirportService;
import com.example.airline.domain.entities.Airport;
import com.example.airline.domain.repositories.AirportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AirportServiceImpl implements AirportService {

    private final AirportRepository airportRepository;

    @Override
    @Transactional
    public AirportDtos.AirportResponse create(AirportDtos.AirportCreateRequest request) {
        airportRepository.findByCode(request.code()).ifPresent(a -> {
            throw new IllegalArgumentException("Airport with code " + request.code() + " already exists");
        });

        Airport airport = AirportMapper.toEntity(request);
        airport = airportRepository.save(airport);
        return AirportMapper.toResponse(airport);
    }

    @Override
    public AirportDtos.AirportResponse findById(Long id) {
        Airport airport = airportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Airport not found with id: " + id));
        return AirportMapper.toResponse(airport);
    }

    @Override
    public AirportDtos.AirportResponse findByCode(String code) {
        Airport airport = airportRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Airport not found with code: " + code));
        return AirportMapper.toResponse(airport);
    }

    @Override
    public List<AirportDtos.AirportResponse> findAll() {
        return airportRepository.findAll().stream()
                .map(AirportMapper::toResponse)
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
        return AirportMapper.toResponse(airport);
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
