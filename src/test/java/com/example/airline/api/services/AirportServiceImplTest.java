package com.example.airline.api.services;

import com.example.airline.api.dto.AirportDtos;
import com.example.airline.domain.entities.Airport;
import com.example.airline.domain.repositories.AirportRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AirportService Tests")
class AirportServiceImplTest {

    @Mock
    private AirportRepository airportRepository;

    @InjectMocks
    private AirportServiceImpl airportService;

    @Test
    @DisplayName("Crear aeropuerto exitosamente")
    void testCreateAirport_Success() {
        // Arrange
        AirportDtos.AirportCreateRequest request =
                new AirportDtos.AirportCreateRequest("JFK", "John F. Kennedy", "New York");
        Airport airport = Airport.builder()
                .id(1L)
                .code("JFK")
                .name("John F. Kennedy")
                .city("New York")
                .build();

        when(airportRepository.findByCode("JFK")).thenReturn(Optional.empty());
        when(airportRepository.save(any(Airport.class))).thenReturn(airport);

        // Act
        AirportDtos.AirportResponse response = airportService.create(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.code()).isEqualTo("JFK");
        assertThat(response.city()).isEqualTo("New York");
        verify(airportRepository, times(1)).save(any(Airport.class));
    }

    @Test
    @DisplayName("Crear aeropuerto - Código ya existe")
    void testCreateAirport_CodeAlreadyExists() {
        // Arrange
        AirportDtos.AirportCreateRequest request =
                new AirportDtos.AirportCreateRequest("JFK", "John F. Kennedy", "New York");
        Airport existing = Airport.builder().id(1L).code("JFK").build();

        when(airportRepository.findByCode("JFK")).thenReturn(Optional.of(existing));

        // Act & Assert
        assertThatThrownBy(() -> airportService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    @DisplayName("Buscar aeropuerto por ID")
    void testFindById_Success() {
        // Arrange
        Airport airport = Airport.builder().id(1L).code("JFK").name("John F. Kennedy").city("New York").build();
        when(airportRepository.findById(1L)).thenReturn(Optional.of(airport));

        // Act
        AirportDtos.AirportResponse response = airportService.findById(1L);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.code()).isEqualTo("JFK");
    }

    @Test
    @DisplayName("Buscar aeropuerto por código")
    void testFindByCode_Success() {
        // Arrange
        Airport airport = Airport.builder().id(1L).code("JFK").name("John F. Kennedy").city("New York").build();
        when(airportRepository.findByCode("JFK")).thenReturn(Optional.of(airport));

        // Act
        AirportDtos.AirportResponse response = airportService.findByCode("JFK");

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.code()).isEqualTo("JFK");
    }

    @Test
    @DisplayName("Buscar todos los aeropuertos")
    void testFindAll() {
        // Arrange
        List<Airport> airports = List.of(
                Airport.builder().id(1L).code("JFK").name("John F. Kennedy").city("New York").build(),
                Airport.builder().id(2L).code("LAX").name("Los Angeles Intl").city("Los Angeles").build()
        );
        when(airportRepository.findAll()).thenReturn(airports);

        // Act
        List<AirportDtos.AirportResponse> responses = airportService.findAll();

        // Assert
        assertThat(responses).hasSize(2);
    }

    @Test
    @DisplayName("Actualizar aeropuerto")
    void testUpdateAirport_Success() {
        // Arrange
        Airport airport = Airport.builder().id(1L).code("JFK").name("John F. Kennedy").city("New York").build();
        AirportDtos.AirportCreateRequest request =
                new AirportDtos.AirportCreateRequest("JFK", "JFK International", "New York");

        when(airportRepository.findById(1L)).thenReturn(Optional.of(airport));
        when(airportRepository.save(any(Airport.class))).thenReturn(airport);

        // Act
        AirportDtos.AirportResponse response = airportService.update(1L, request);

        // Assert
        assertThat(response).isNotNull();
        verify(airportRepository, times(1)).save(any(Airport.class));
    }

    @Test
    @DisplayName("Eliminar aeropuerto")
    void testDeleteAirport_Success() {
        // Arrange
        when(airportRepository.existsById(1L)).thenReturn(true);

        // Act
        airportService.delete(1L);

        // Assert
        verify(airportRepository, times(1)).deleteById(1L);
    }
}