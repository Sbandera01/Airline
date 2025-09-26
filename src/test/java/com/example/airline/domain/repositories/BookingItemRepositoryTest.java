package com.example.airline.domain.repositories;

import com.example.airline.BaseIntegrationTest;
import com.example.airline.domain.entities.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BookingItemRepositoryTest extends BaseIntegrationTest {

    @Autowired BookingItemRepository bookingItemRepository;
    @Autowired BookingRepository bookingRepository;
    @Autowired PassengerRepository passengerRepository;
    @Autowired FlightRepository flightRepository;
    @Autowired AirlineRepository airlineRepository;
    @Autowired AirportRepository airportRepository;

    @Test
    void shouldListBookingItemsBySegmentOrder() {
        Booking booking = setupBooking();

        Flight flight = setupFlight();
        bookingItemRepository.saveAll(List.of(
                BookingItem.builder().booking(booking).flight(flight).cabin("ECONOMY").price(BigDecimal.valueOf(200)).segmentOrder(2).build(),
                BookingItem.builder().booking(booking).flight(flight).cabin("BUSINESS").price(BigDecimal.valueOf(500)).segmentOrder(1).build()
        ));

        List<BookingItem> items = bookingItemRepository.findByBooking_IdOrderBySegmentOrderAsc(booking.getId());

        assertThat(items).hasSize(2);
        assertThat(items.get(0).getSegmentOrder()).isEqualTo(1);
    }

    @Test
    void shouldCalculateTotalBookingPrice() {
        Booking booking = setupBooking();

        Flight flight = setupFlight();
        bookingItemRepository.saveAll(List.of(
                BookingItem.builder().booking(booking).flight(flight).cabin("ECONOMY").price(BigDecimal.valueOf(200)).segmentOrder(1).build(),
                BookingItem.builder().booking(booking).flight(flight).cabin("BUSINESS").price(BigDecimal.valueOf(500)).segmentOrder(2).build()
        ));

        BigDecimal total = bookingItemRepository.calculateTotal(booking.getId());

        assertThat(total).isEqualByComparingTo("700");
    }

    @Test
    void shouldCountReservedSeats() {
        Booking booking = setupBooking();
        Flight flight = setupFlight();

        bookingItemRepository.save(
                BookingItem.builder().booking(booking).flight(flight).cabin("ECONOMY").price(BigDecimal.valueOf(150)).segmentOrder(1).build()
        );

        long reserved = bookingItemRepository.countReservedSeats(flight.getId(), "ECONOMY");

        assertThat(reserved).isEqualTo(1);
    }

    private Booking setupBooking() {
        Passenger passenger = passengerRepository.save(Passenger.builder().fullName("Anna").email("anna@example.com").build());
        return bookingRepository.save(Booking.builder().passenger(passenger).createdAt(OffsetDateTime.now()).build());
    }

    private Flight setupFlight() {
        Airline airline = airlineRepository.save(Airline.builder().code("AV").name("Avianca").build());
        Airport origin = airportRepository.save(Airport.builder().code("BOG").name("El Dorado").city("Bogotá").build());
        Airport destination = airportRepository.save(Airport.builder().code("MIA").name("Miami Intl").city("Miami").build());

        return flightRepository.save(
                Flight.builder()
                        .number("AV500")
                        .airline(airline)
                        .origin(origin)
                        .destination(destination)
                        .departureTime(OffsetDateTime.now().plusDays(1))
                        .arrivalTime(OffsetDateTime.now().plusDays(1).plusHours(3))
                        .build()
        );
    }
}

