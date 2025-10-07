package com.example.airline.api.services;

import com.example.airline.api.dto.BookingDtos;
import com.example.airline.api.mapper.BookingMapper;
import com.example.airline.domain.entities.Booking;
import com.example.airline.domain.entities.BookingItem;
import com.example.airline.domain.entities.Flight;
import com.example.airline.domain.entities.Passenger;
import com.example.airline.domain.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingItemRepository bookingItemRepository;
    private final PassengerRepository passengerRepository;
    private final FlightRepository flightRepository;
    private final SeatInventoryService seatInventoryService;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public BookingDtos.BookingResponse create(BookingDtos.BookingCreateRequest request) {
        // Buscar pasajero (debe existir previamente)
        Passenger passenger = passengerRepository.findByEmailIgnoreCase(request.passengerEmail())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Passenger not found with email: " + request.passengerEmail() +
                                ". Please create the passenger first."));

        // Validar que hay items en la reserva
        if (request.items() == null || request.items().isEmpty()) {
            throw new IllegalArgumentException("Booking must have at least one item");
        }

        // Verificar disponibilidad de TODOS los vuelos antes de crear la reserva
        for (BookingDtos.BookingItemRequest itemRequest : request.items()) {
            if (!flightRepository.existsById(itemRequest.flightId())) {
                throw new IllegalArgumentException("Flight not found with id: " + itemRequest.flightId());
            }

            if (!seatInventoryService.hasAvailableSeats(itemRequest.flightId(), itemRequest.cabin(), 1)) {
                throw new IllegalStateException(
                        "No seats available for flight " + itemRequest.flightId() +
                                " in cabin " + itemRequest.cabin());
            }
        }

        // Crear la reserva
        Booking booking = new Booking();
        booking.setPassenger(passenger);
        booking.setCreatedAt(OffsetDateTime.now());
        booking = bookingRepository.save(booking);

        // Crear los items de la reserva y decrementar inventario
        List<BookingItem> items = new ArrayList<>();
        for (BookingDtos.BookingItemRequest itemRequest : request.items()) {
            Flight flight = flightRepository.findById(itemRequest.flightId()).get();

            // Crear el item
            BookingItem item = bookingMapper.toEntity(itemRequest);
            item.setBooking(booking);
            item.setFlight(flight);
            item = bookingItemRepository.save(item);
            items.add(item);

            // Decrementar disponibilidad de asientos
            seatInventoryService.decreaseAvailability(
                    itemRequest.flightId(),
                    itemRequest.cabin(),
                    1
            );
        }

        booking.setItems(items);
        return bookingMapper.toResponse(booking);
    }

    @Override
    public BookingDtos.BookingResponse findById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with id: " + id));
        return bookingMapper.toResponse(booking);
    }

    @Override
    public BookingDtos.BookingResponse findByIdWithDetails(Long id) {
        Booking booking = bookingRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with id: " + id));
        return bookingMapper.toResponse(booking);
    }

    @Override
    public List<BookingDtos.BookingResponse> findAll() {
        return bookingRepository.findAll().stream()
                .map(bookingMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<BookingDtos.BookingResponse> findByPassengerEmail(String email, Pageable pageable) {
        return bookingRepository.findByPassenger_EmailIgnoreCaseOrderByCreatedAtDesc(email, pageable)
                .map(bookingMapper::toResponse);
    }

    @Override
    public List<BookingDtos.BookingItemResponse> findBookingItems(Long bookingId) {
        // Verificar que la reserva existe
        if (!bookingRepository.existsById(bookingId)) {
            throw new IllegalArgumentException("Booking not found with id: " + bookingId);
        }

        return bookingItemRepository.findByBooking_IdOrderBySegmentOrderAsc(bookingId).stream()
                .map(bookingMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BigDecimal calculateBookingTotal(Long bookingId) {
        // Verificar que la reserva existe
        if (!bookingRepository.existsById(bookingId)) {
            throw new IllegalArgumentException("Booking not found with id: " + bookingId);
        }

        return bookingItemRepository.calculateTotal(bookingId);
    }

    @Override
    @Transactional
    public void cancelBooking(Long id) {
        Booking booking = bookingRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with id: " + id));

        // Liberar asientos de todos los segmentos
        for (BookingItem item : booking.getItems()) {
            try {
                seatInventoryService.increaseAvailability(
                        item.getFlight().getId(),
                        item.getCabin(),
                        1
                );
            } catch (Exception e) {
                // Log del error pero continuar liberando otros asientos
                System.err.println("Error releasing seat for flight " +
                        item.getFlight().getId() + ": " + e.getMessage());
            }
        }

        // Eliminar la reserva (esto eliminará en cascada los items si está configurado)
        bookingRepository.deleteById(id);
    }
}
