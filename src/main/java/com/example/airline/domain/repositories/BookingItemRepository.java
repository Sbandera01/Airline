package com.example.airline.domain.repositories;

import com.example.airline.domain.entities.BookingItem;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface BookingItemRepository extends JpaRepository<BookingItem, Long> {

    // segmentos de una reserva ordenados por segmentOrder
    List<BookingItem> findByBooking_IdOrderBySegmentOrderAsc(Long bookingId);

    // total de la reserva
    @Query("SELECT COALESCE(SUM(i.price), 0) FROM BookingItem i WHERE i.booking.id = :bookingId")
    BigDecimal calculateTotal(@Param("bookingId") Long bookingId);

    // asientos vendidos/reservados por vuelo y cabina
    @Query("SELECT COUNT(i) FROM BookingItem i WHERE i.flight.id = :flightId AND i.cabin = :cabin")
    long countReservedSeats(@Param("flightId") Long flightId, @Param("cabin") String cabin);
}

