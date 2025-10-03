package com.example.airline.api.services;

import com.example.airline.api.dto.SeatInventoryDtos;
import com.example.airline.domain.entities.*;
import com.example.airline.domain.repositories.FlightRepository;
import com.example.airline.domain.repositories.SeatInventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SeatInventoryService Tests")
class SeatInventoryServiceImplTest {

    @Mock
    private SeatInventoryRepository seatInventoryRepository;

    @Mock
    private FlightRepository flightRepository;

    @InjectMocks
    private SeatInventoryServiceImpl seatInventoryService;

    private Flight flight;
    private SeatInventory seatInventory;

    @BeforeEach
    void setUp() {
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

        seatInventory = SeatInventory.builder()
                .id(1L)
                .cabin("Economy")
                .totalSeats(150)
                .availableSeats(150)
                .flight(flight)
                .build();
    }

    @Test
    @DisplayName("Crear inventario de asientos exitosamente")
    void testCreateSeatInventory_Success() {
        // Arrange
        SeatInventoryDtos.SeatInventoryCreateRequest request =
                new SeatInventoryDtos.SeatInventoryCreateRequest(1L, "Economy", 150);

        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));
        when(seatInventoryRepository.findByFlight_IdAndCabin(1L, "Economy"))
                .thenReturn(Optional.empty());
        when(seatInventoryRepository.save(any(SeatInventory.class))).thenReturn(seatInventory);

        // Act
        SeatInventoryDtos.SeatInventoryResponse response = seatInventoryService.create(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.cabin()).isEqualTo("Economy");
        assertThat(response.totalSeats()).isEqualTo(150);
        assertThat(response.availableSeats()).isEqualTo(150);
        verify(seatInventoryRepository, times(1)).save(any(SeatInventory.class));
    }

    @Test
    @DisplayName("Crear inventario - Vuelo no encontrado")
    void testCreateSeatInventory_FlightNotFound() {
        // Arrange
        SeatInventoryDtos.SeatInventoryCreateRequest request =
                new SeatInventoryDtos.SeatInventoryCreateRequest(999L, "Economy", 150);

        when(flightRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> seatInventoryService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Flight not found");

        verify(seatInventoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("Crear inventario - Ya existe para ese vuelo y cabina")
    void testCreateSeatInventory_AlreadyExists() {
        // Arrange
        SeatInventoryDtos.SeatInventoryCreateRequest request =
                new SeatInventoryDtos.SeatInventoryCreateRequest(1L, "Economy", 150);

        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));
        when(seatInventoryRepository.findByFlight_IdAndCabin(1L, "Economy"))
                .thenReturn(Optional.of(seatInventory));

        // Act & Assert
        assertThatThrownBy(() -> seatInventoryService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");

        verify(seatInventoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("Buscar inventario por ID exitosamente")
    void testFindById_Success() {
        // Arrange
        when(seatInventoryRepository.findById(1L)).thenReturn(Optional.of(seatInventory));

        // Act
        SeatInventoryDtos.SeatInventoryResponse response = seatInventoryService.findById(1L);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Buscar inventario por ID - No encontrado")
    void testFindById_NotFound() {
        // Arrange
        when(seatInventoryRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> seatInventoryService.findById(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Seat inventory not found");
    }

    @Test
    @DisplayName("Buscar inventario por vuelo y cabina")
    void testFindByFlightAndCabin_Success() {
        // Arrange
        when(seatInventoryRepository.findByFlight_IdAndCabin(1L, "Economy"))
                .thenReturn(Optional.of(seatInventory));

        // Act
        SeatInventoryDtos.SeatInventoryResponse response =
                seatInventoryService.findByFlightAndCabin(1L, "Economy");

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.cabin()).isEqualTo("Economy");
    }

    @Test
    @DisplayName("Buscar inventario por vuelo y cabina - No encontrado")
    void testFindByFlightAndCabin_NotFound() {
        // Arrange
        when(seatInventoryRepository.findByFlight_IdAndCabin(1L, "Business"))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> seatInventoryService.findByFlightAndCabin(1L, "Business"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found");
    }

    @Test
    @DisplayName("Buscar todos los inventarios")
    void testFindAll() {
        // Arrange
        List<SeatInventory> inventories = List.of(seatInventory);
        when(seatInventoryRepository.findAll()).thenReturn(inventories);

        // Act
        List<SeatInventoryDtos.SeatInventoryResponse> responses = seatInventoryService.findAll();

        // Assert
        assertThat(responses).hasSize(1);
    }

    @Test
    @DisplayName("Verificar asientos disponibles - Hay suficientes")
    void testHasAvailableSeats_True() {
        // Arrange
        when(seatInventoryRepository.hasAvailableSeats(1L, "Economy", 10)).thenReturn(true);

        // Act
        boolean result = seatInventoryService.hasAvailableSeats(1L, "Economy", 10);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Verificar asientos disponibles - No hay suficientes")
    void testHasAvailableSeats_False() {
        // Arrange
        when(seatInventoryRepository.hasAvailableSeats(1L, "Economy", 200)).thenReturn(false);

        // Act
        boolean result = seatInventoryService.hasAvailableSeats(1L, "Economy", 200);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Decrementar disponibilidad exitosamente")
    void testDecreaseAvailability_Success() {
        // Arrange
        when(seatInventoryRepository.findByFlight_IdAndCabin(1L, "Economy"))
                .thenReturn(Optional.of(seatInventory));

        SeatInventory updatedInventory = SeatInventory.builder()
                .id(1L)
                .cabin("Economy")
                .totalSeats(150)
                .availableSeats(145)
                .flight(flight)
                .build();

        when(seatInventoryRepository.save(any(SeatInventory.class))).thenReturn(updatedInventory);

        // Act
        SeatInventoryDtos.SeatInventoryResponse response =
                seatInventoryService.decreaseAvailability(1L, "Economy", 5);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.availableSeats()).isEqualTo(145);
        verify(seatInventoryRepository, times(1)).save(any(SeatInventory.class));
    }

    @Test
    @DisplayName("Decrementar disponibilidad - Asientos insuficientes")
    void testDecreaseAvailability_InsufficientSeats() {
        // Arrange
        seatInventory.setAvailableSeats(5);
        when(seatInventoryRepository.findByFlight_IdAndCabin(1L, "Economy"))
                .thenReturn(Optional.of(seatInventory));

        // Act & Assert
        assertThatThrownBy(() -> seatInventoryService.decreaseAvailability(1L, "Economy", 10))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Not enough available seats");

        verify(seatInventoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("Decrementar disponibilidad - Inventario no encontrado")
    void testDecreaseAvailability_NotFound() {
        // Arrange
        when(seatInventoryRepository.findByFlight_IdAndCabin(1L, "Business"))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> seatInventoryService.decreaseAvailability(1L, "Business", 5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found");
    }

    @Test
    @DisplayName("Incrementar disponibilidad exitosamente")
    void testIncreaseAvailability_Success() {
        // Arrange
        seatInventory.setAvailableSeats(140);
        when(seatInventoryRepository.findByFlight_IdAndCabin(1L, "Economy"))
                .thenReturn(Optional.of(seatInventory));

        SeatInventory updatedInventory = SeatInventory.builder()
                .id(1L)
                .cabin("Economy")
                .totalSeats(150)
                .availableSeats(145)
                .flight(flight)
                .build();

        when(seatInventoryRepository.save(any(SeatInventory.class))).thenReturn(updatedInventory);

        // Act
        SeatInventoryDtos.SeatInventoryResponse response =
                seatInventoryService.increaseAvailability(1L, "Economy", 5);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.availableSeats()).isEqualTo(145);
        verify(seatInventoryRepository, times(1)).save(any(SeatInventory.class));
    }

    @Test
    @DisplayName("Incrementar disponibilidad - Excede total de asientos")
    void testIncreaseAvailability_ExceedsTotalSeats() {
        // Arrange
        seatInventory.setAvailableSeats(145);
        when(seatInventoryRepository.findByFlight_IdAndCabin(1L, "Economy"))
                .thenReturn(Optional.of(seatInventory));

        // Act & Assert
        assertThatThrownBy(() -> seatInventoryService.increaseAvailability(1L, "Economy", 10))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot increase availability beyond total seats");

        verify(seatInventoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("Actualizar inventario exitosamente")
    void testUpdateSeatInventory_Success() {
        // Arrange
        SeatInventoryDtos.SeatInventoryCreateRequest request =
                new SeatInventoryDtos.SeatInventoryCreateRequest(1L, "Economy", 180);

        when(seatInventoryRepository.findById(1L)).thenReturn(Optional.of(seatInventory));
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));
        when(seatInventoryRepository.save(any(SeatInventory.class))).thenReturn(seatInventory);

        // Act
        SeatInventoryDtos.SeatInventoryResponse response = seatInventoryService.update(1L, request);

        // Assert
        assertThat(response).isNotNull();
        verify(seatInventoryRepository, times(1)).save(any(SeatInventory.class));
    }

    @Test
    @DisplayName("Actualizar inventario - No encontrado")
    void testUpdateSeatInventory_NotFound() {
        // Arrange
        SeatInventoryDtos.SeatInventoryCreateRequest request =
                new SeatInventoryDtos.SeatInventoryCreateRequest(1L, "Economy", 180);

        when(seatInventoryRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> seatInventoryService.update(999L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found");
    }

    @Test
    @DisplayName("Eliminar inventario exitosamente")
    void testDeleteSeatInventory_Success() {
        // Arrange
        when(seatInventoryRepository.existsById(1L)).thenReturn(true);
        doNothing().when(seatInventoryRepository).deleteById(1L);

        // Act
        seatInventoryService.delete(1L);

        // Assert
        verify(seatInventoryRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Eliminar inventario - No encontrado")
    void testDeleteSeatInventory_NotFound() {
        // Arrange
        when(seatInventoryRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> seatInventoryService.delete(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found");

        verify(seatInventoryRepository, never()).deleteById(anyLong());
    }
}