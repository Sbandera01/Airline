package com.example.airline.api.services;

import com.example.airline.api.dto.PassengerDtos;
import com.example.airline.api.mapper.PassengerMapper;
import com.example.airline.domain.entities.Passenger;
import com.example.airline.domain.repositories.PassengerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PassengerServiceImpl implements PassengerService {

    private final PassengerRepository passengerRepository;

    @Override
    @Transactional
    public PassengerDtos.PassengerResponse create(PassengerDtos.PassengerCreateRequest request) {
        passengerRepository.findByEmailIgnoreCase(request.email()).ifPresent(p -> {
            throw new IllegalArgumentException("Passenger with email " + request.email() + " already exists");
        });

        Passenger passenger = PassengerMapper.toEntity(request);
        passenger = passengerRepository.save(passenger);
        return PassengerMapper.toResponse(passenger);
    }

    @Override
    public PassengerDtos.PassengerResponse findById(Long id) {
        Passenger passenger = passengerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Passenger not found with id: " + id));
        return PassengerMapper.toResponse(passenger);
    }

    @Override
    public PassengerDtos.PassengerResponse findByEmail(String email) {
        Passenger passenger = passengerRepository.findByEmailIgnoreCaseWithProfile(email)
                .orElseThrow(() -> new IllegalArgumentException("Passenger not found with email: " + email));
        return PassengerMapper.toResponse(passenger);
    }

    @Override
    public List<PassengerDtos.PassengerResponse> findAll() {
        return passengerRepository.findAll().stream()
                .map(PassengerMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PassengerDtos.PassengerResponse update(Long id, PassengerDtos.PassengerCreateRequest request) {
        Passenger passenger = passengerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Passenger not found with id: " + id));

        // Verificar si el email ya existe en otro pasajero
        passengerRepository.findByEmailIgnoreCase(request.email()).ifPresent(p -> {
            if (!p.getId().equals(id)) {
                throw new IllegalArgumentException("Email " + request.email() + " is already in use");
            }
        });

        passenger.setFullName(request.fullName());
        passenger.setEmail(request.email());

        if (request.profile() != null) {
            if (passenger.getProfile() != null) {
                passenger.getProfile().setPhone(request.profile().phone());
                passenger.getProfile().setCountryCode(request.profile().countryCode());
            } else {
                passenger.setProfile(PassengerMapper.toEntity(request).getProfile());
            }
        }

        passenger = passengerRepository.save(passenger);
        return PassengerMapper.toResponse(passenger);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!passengerRepository.existsById(id)) {
            throw new IllegalArgumentException("Passenger not found with id: " + id);
        }
        passengerRepository.deleteById(id);
    }
}