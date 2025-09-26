package com.example.airline.domain.repositories;

import com.example.airline.domain.entities.SeatInventory;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SeatInventoryRepository extends JpaRepository<SeatInventory, Long> {

    // inventario de asientos de un vuelo + cabina
    Optional<SeatInventory> findByFlight_IdAndCabin(Long flightId, String cabin);

    // verificar cupos disponibles
    @Query("SELECT CASE WHEN si.availableSeats >= :min THEN true ELSE false END " +
            "FROM SeatInventory si " +
            "WHERE si.flight.id = :flightId AND si.cabin = :cabin")
    boolean hasAvailableSeats(@Param("flightId") Long flightId,
                              @Param("cabin") String cabin,
                              @Param("min") int min);
}

