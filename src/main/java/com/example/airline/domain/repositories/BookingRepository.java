package com.example.airline.domain.repositories;

import com.example.airline.domain.entities.Booking;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // historial de reservas por pasajero (email)
    Page<Booking> findByPassenger_EmailIgnoreCaseOrderByCreatedAtDesc(String email, Pageable pageable);

    // reserva por id con items, flights y passenger
    @Query("""
           SELECT b FROM Booking b
           LEFT JOIN FETCH b.items i
           LEFT JOIN FETCH i.flight
           LEFT JOIN FETCH b.passenger
           WHERE b.id = :id
           """)
    Optional<Booking> findByIdWithDetails(@Param("id") Long id);
}

