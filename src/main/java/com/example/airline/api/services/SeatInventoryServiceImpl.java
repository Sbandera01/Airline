package com.example.airline.api.services;

import com.example.airline.api.dto.SeatInventoryDtos;
import com.example.airline.api.mapper.SeatInventoryMapper;
import com.example.airline.domain.entities.Flight;
import com.example.airline.domain.entities.SeatInventory;
import com.example.airline.domain.repositories.FlightRepository;
import com.example.airline.domain.repositories.SeatInventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeatInventoryServiceImpl implements SeatInventoryService {

    private final SeatInventoryRepository seatInventoryRepository;
    private final FlightRepository flightRepository;

    @Override
    @Transactional
    public SeatInventoryDtos.SeatInventoryResponse create(SeatInventoryDtos.SeatInventoryCreateRequest request) {
        // Verificar que el vuelo existe
        Flight flight = flightRepository.findById(request.flightId())
                .orElseThrow(() -> new IllegalArgumentException("Flight not found with id: " + request.flightId()));

        // Verificar que no existe ya un inventario para ese vuelo y cabina
        seatInventoryRepository.findByFlight_IdAndCabin(request.flightId(), request.cabin())
                .ifPresent(s -> {
                    throw new IllegalArgumentException("Seat inventory already exists for flight " +
                            request.flightId() + " and cabin " + request.cabin());
                });

        SeatInventory seatInventory = SeatInventoryMapper.toEntity(request);
        seatInventory.setFlight(flight);
        seatInventory = seatInventoryRepository.save(seatInventory);
        return SeatInventoryMapper.toResponse(seatInventory);
    }

    @Override
    public SeatInventoryDtos.SeatInventoryResponse findById(Long id) {
        SeatInventory seatInventory = seatInventoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Seat inventory not found with id: " + id));
        return SeatInventoryMapper.toResponse(seatInventory);
    }

    @Override
    public SeatInventoryDtos.SeatInventoryResponse findByFlightAndCabin(Long flightId, String cabin) {
        SeatInventory seatInventory = seatInventoryRepository.findByFlight_IdAndCabin(flightId, cabin)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Seat inventory not found for flight " + flightId + " and cabin " + cabin));
        return SeatInventoryMapper.toResponse(seatInventory);
    }

    @Override
    public List<SeatInventoryDtos.SeatInventoryResponse> findAll() {
        return seatInventoryRepository.findAll().stream()
                .map(SeatInventoryMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasAvailableSeats(Long flightId, String cabin, int minimumSeats) {
        return seatInventoryRepository.hasAvailableSeats(flightId, cabin, minimumSeats);
    }

    @Override
    @Transactional
    public SeatInventoryDtos.SeatInventoryResponse decreaseAvailability(Long flightId, String cabin, int quantity) {
        SeatInventory seatInventory = seatInventoryRepository.findByFlight_IdAndCabin(flightId, cabin)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Seat inventory not found for flight " + flightId + " and cabin " + cabin));

        if (seatInventory.getAvailableSeats() < quantity) {
            throw new IllegalStateException("Not enough available seats. Available: " +
                    seatInventory.getAvailableSeats() + ", Requested: " + quantity);
        }

        seatInventory.setAvailableSeats(seatInventory.getAvailableSeats() - quantity);
        seatInventory = seatInventoryRepository.save(seatInventory);
        return SeatInventoryMapper.toResponse(seatInventory);
    }

    @Override
    @Transactional
    public SeatInventoryDtos.SeatInventoryResponse increaseAvailability(Long flightId, String cabin, int quantity) {
        SeatInventory seatInventory = seatInventoryRepository.findByFlight_IdAndCabin(flightId, cabin)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Seat inventory not found for flight " + flightId + " and cabin " + cabin));

        int newAvailable = seatInventory.getAvailableSeats() + quantity;
        if (newAvailable > seatInventory.getTotalSeats()) {
            throw new IllegalStateException("Cannot increase availability beyond total seats. Total: " +
                    seatInventory.getTotalSeats() + ", Would be: " + newAvailable);
        }

        seatInventory.setAvailableSeats(newAvailable);
        seatInventory = seatInventoryRepository.save(seatInventory);
        return SeatInventoryMapper.toResponse(seatInventory);
    }

    @Override
    @Transactional
    public SeatInventoryDtos.SeatInventoryResponse update(Long id, SeatInventoryDtos.SeatInventoryCreateRequest request) {
        SeatInventory seatInventory = seatInventoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Seat inventory not found with id: " + id));

        Flight flight = flightRepository.findById(request.flightId())
                .orElseThrow(() -> new IllegalArgumentException("Flight not found with id: " + request.flightId()));

        seatInventory.setFlight(flight);
        seatInventory.setCabin(request.cabin());
        seatInventory.setTotalSeats(request.totalSeats());
        seatInventory.setAvailableSeats(request.totalSeats());

        seatInventory = seatInventoryRepository.save(seatInventory);
        return SeatInventoryMapper.toResponse(seatInventory);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!seatInventoryRepository.existsById(id)) {
            throw new IllegalArgumentException("Seat inventory not found with id: " + id);
        }
        seatInventoryRepository.deleteById(id);
    }
}
