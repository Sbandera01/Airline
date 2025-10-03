package com.example.airline.api.services;

import com.example.airline.api.dto.BookingDtos;
import com.example.airline.api.dto.SeatInventoryDtos;
import com.example.airline.domain.entities.*;
import com.example.airline.domain.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookingService Tests")
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingItemRepository bookingItemRepository;

    @Mock
    private PassengerRepository passengerRepository;

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private SeatInventoryService seatInventoryService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private Passenger passenger;
    private Flight flight;
    private Booking booking;
    private BookingItem bookingItem;

    @BeforeEach
    void setUp() {
        // Configurar datos de prueba
        passenger = Passenger.builder()
                .id(1L)
                .fullName("John Doe")
                .email("john@example.com")
                .build();

        Airline airline = Airline.builder()
                .id(1L)
                .code("AA")
                .name("American Airlines")
                .build();

        Airport origin = Airport.builder()
                .id(1L)
                .code("JFK")
                .name("John F. Kennedy")
                .city("New York")
                .build();

        Airport destination = Airport.builder()
                .id(2L)
                .code("LAX")
                .name("Los Angeles International")
                .city("Los Angeles")
                .build();

        flight = Flight.builder()
                .id(1L)
                .number("AA123")
                .departureTime(OffsetDateTime.now().plusDays(1))
                .arrivalTime(OffsetDateTime.now().plusDays(1).plusHours(5))
                .airline(airline)
                .origin(origin)
                .destination(destination)
                .build();

        booking = Booking.builder()
                .id(1L)
                .createdAt(OffsetDateTime.now())
                .passenger(passenger)
                .items(new ArrayList<>())
                .build();

        bookingItem = BookingItem.builder()
                .id(1L)
                .cabin("Economy")
                .price(new BigDecimal("350.00"))
                .segmentOrder(1)
                .booking(booking)
                .flight(flight)
                .build();
    }

    @Test
    @DisplayName("Crear reserva exitosamente")
    void testCreateBooking_Success() {
        // Arrange
        BookingDtos.BookingItemRequest itemRequest = new BookingDtos.BookingItemRequest(
                1L, "Economy", new BigDecimal("350.00"), 1
        );
        BookingDtos.BookingCreateRequest request = new BookingDtos.BookingCreateRequest(
                "john@example.com",
                List.of(itemRequest)
        );

        when(passengerRepository.findByEmailIgnoreCase("john@example.com"))
                .thenReturn(Optional.of(passenger));
        when(flightRepository.existsById(1L)).thenReturn(true);
        when(seatInventoryService.hasAvailableSeats(1L, "Economy", 1)).thenReturn(true);
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingItemRepository.save(any(BookingItem.class))).thenReturn(bookingItem);

        // Act
        BookingDtos.BookingResponse response = bookingService.create(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.passenger().email()).isEqualTo("john@example.com");
        verify(bookingRepository, times(1)).save(any(Booking.class));
        verify(bookingItemRepository, times(1)).save(any(BookingItem.class));
        verify(seatInventoryService, times(1)).decreaseAvailability(1L, "Economy", 1);
    }

    @Test
    @DisplayName("Crear reserva - Pasajero no encontrado")
    void testCreateBooking_PassengerNotFound() {
        // Arrange
        BookingDtos.BookingItemRequest itemRequest = new BookingDtos.BookingItemRequest(
                1L, "Economy", new BigDecimal("350.00"), 1
        );
        BookingDtos.BookingCreateRequest request = new BookingDtos.BookingCreateRequest(
                "nonexistent@example.com",
                List.of(itemRequest)
        );

        when(passengerRepository.findByEmailIgnoreCase("nonexistent@example.com"))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> bookingService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Passenger not found");

        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Crear reserva - Sin items")
    void testCreateBooking_NoItems() {
        // Arrange
        BookingDtos.BookingCreateRequest request = new BookingDtos.BookingCreateRequest(
                "john@example.com",
                List.of()
        );

        when(passengerRepository.findByEmailIgnoreCase("john@example.com"))
                .thenReturn(Optional.of(passenger));

        // Act & Assert
        assertThatThrownBy(() -> bookingService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("at least one item");
    }

    @Test
    @DisplayName("Crear reserva - Vuelo no existe")
    void testCreateBooking_FlightNotFound() {
        // Arrange
        BookingDtos.BookingItemRequest itemRequest = new BookingDtos.BookingItemRequest(
                999L, "Economy", new BigDecimal("350.00"), 1
        );
        BookingDtos.BookingCreateRequest request = new BookingDtos.BookingCreateRequest(
                "john@example.com",
                List.of(itemRequest)
        );

        when(passengerRepository.findByEmailIgnoreCase("john@example.com"))
                .thenReturn(Optional.of(passenger));
        when(flightRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> bookingService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Flight not found");
    }

    @Test
    @DisplayName("Crear reserva - Sin asientos disponibles")
    void testCreateBooking_NoSeatsAvailable() {
        // Arrange
        BookingDtos.BookingItemRequest itemRequest = new BookingDtos.BookingItemRequest(
                1L, "Economy", new BigDecimal("350.00"), 1
        );
        BookingDtos.BookingCreateRequest request = new BookingDtos.BookingCreateRequest(
                "john@example.com",
                List.of(itemRequest)
        );

        when(passengerRepository.findByEmailIgnoreCase("john@example.com"))
                .thenReturn(Optional.of(passenger));
        when(flightRepository.existsById(1L)).thenReturn(true);
        when(seatInventoryService.hasAvailableSeats(1L, "Economy", 1)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> bookingService.create(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("No seats available");
    }

    @Test
    @DisplayName("Buscar reserva por ID exitosamente")
    void testFindById_Success() {
        // Arrange
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        // Act
        BookingDtos.BookingResponse response = bookingService.findById(1L);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        verify(bookingRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Buscar reserva por ID - No encontrada")
    void testFindById_NotFound() {
        // Arrange
        when(bookingRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> bookingService.findById(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Booking not found");
    }

    @Test
    @DisplayName("Buscar reserva con detalles")
    void testFindByIdWithDetails_Success() {
        // Arrange
        booking.setItems(List.of(bookingItem));
        when(bookingRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(booking));

        // Act
        BookingDtos.BookingResponse response = bookingService.findByIdWithDetails(1L);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.items()).hasSize(1);
        verify(bookingRepository, times(1)).findByIdWithDetails(1L);
    }

    @Test
    @DisplayName("Buscar todas las reservas")
    void testFindAll() {
        // Arrange
        List<Booking> bookings = List.of(booking);
        when(bookingRepository.findAll()).thenReturn(bookings);

        // Act
        List<BookingDtos.BookingResponse> responses = bookingService.findAll();

        // Assert
        assertThat(responses).hasSize(1);
        verify(bookingRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Buscar reservas por email de pasajero")
    void testFindByPassengerEmail() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Booking> bookingPage = new PageImpl<>(List.of(booking));
        when(bookingRepository.findByPassenger_EmailIgnoreCaseOrderByCreatedAtDesc(
                "john@example.com", pageable))
                .thenReturn(bookingPage);

        // Act
        Page<BookingDtos.BookingResponse> responses =
                bookingService.findByPassengerEmail("john@example.com", pageable);

        // Assert
        assertThat(responses.getContent()).hasSize(1);
        assertThat(responses.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("Buscar items de reserva")
    void testFindBookingItems() {
        // Arrange
        when(bookingRepository.existsById(1L)).thenReturn(true);
        when(bookingItemRepository.findByBooking_IdOrderBySegmentOrderAsc(1L))
                .thenReturn(List.of(bookingItem));

        // Act
        List<BookingDtos.BookingItemResponse> items = bookingService.findBookingItems(1L);

        // Assert
        assertThat(items).hasSize(1);
        assertThat(items.getFirst().cabin()).isEqualTo("Economy");
    }

    @Test
    @DisplayName("Buscar items de reserva - Reserva no existe")
    void testFindBookingItems_BookingNotFound() {
        // Arrange
        when(bookingRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> bookingService.findBookingItems(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Booking not found");
    }

    @Test
    @DisplayName("Calcular total de reserva")
    void testCalculateBookingTotal() {
        // Arrange
        when(bookingRepository.existsById(1L)).thenReturn(true);
        when(bookingItemRepository.calculateTotal(1L))
                .thenReturn(new BigDecimal("350.00"));

        // Act
        BigDecimal total = bookingService.calculateBookingTotal(1L);

        // Assert
        assertThat(total).isEqualByComparingTo("350.00");
    }

    @Test
    @DisplayName("Cancelar reserva exitosamente")
    void testCancelBooking_Success() {
        // Arrange
        booking.setItems(List.of(bookingItem));
        SeatInventoryDtos.SeatInventoryResponse inventoryResponse =
                new SeatInventoryDtos.SeatInventoryResponse(1L, "Economy", 150, 146);

        when(bookingRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(booking));
        when(seatInventoryService.increaseAvailability(anyLong(), anyString(), anyInt()))
                .thenReturn(inventoryResponse);
        doNothing().when(bookingRepository).deleteById(1L);

        // Act
        bookingService.cancelBooking(1L);

        // Assert
        verify(seatInventoryService, times(1)).increaseAvailability(1L, "Economy", 1);
        verify(bookingRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Cancelar reserva - No encontrada")
    void testCancelBooking_NotFound() {
        // Arrange
        when(bookingRepository.findByIdWithDetails(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> bookingService.cancelBooking(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Booking not found");
    }

    @Test
    @DisplayName("Cancelar reserva - Continúa aunque falle liberar un asiento")
    void testCancelBooking_ContinuesOnSeatReleaseError() {
        // Arrange
        BookingItem item2 = BookingItem.builder()
                .id(2L)
                .cabin("Business")
                .price(new BigDecimal("800.00"))
                .segmentOrder(2)
                .booking(booking)
                .flight(flight)
                .build();

        booking.setItems(List.of(bookingItem, item2));
        SeatInventoryDtos.SeatInventoryResponse inventoryResponse =
                new SeatInventoryDtos.SeatInventoryResponse(2L, "Business", 50, 46);

        when(bookingRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(booking));

        // Primera llamada falla, segunda exitosa
        when(seatInventoryService.increaseAvailability(eq(1L), eq("Economy"), eq(1)))
                .thenThrow(new RuntimeException("Error releasing seat"));
        when(seatInventoryService.increaseAvailability(eq(1L), eq("Business"), eq(1)))
                .thenReturn(inventoryResponse);

        doNothing().when(bookingRepository).deleteById(1L);

        // Act
        bookingService.cancelBooking(1L);

        // Assert
        verify(seatInventoryService, times(2)).increaseAvailability(anyLong(), anyString(), eq(1));
        verify(bookingRepository, times(1)).deleteById(1L);
    }
}