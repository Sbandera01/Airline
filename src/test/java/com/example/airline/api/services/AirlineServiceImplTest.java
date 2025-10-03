package com.example.airline.api.services;

import com.example.airline.api.dto.AirlineDtos;
import com.example.airline.domain.entities.Airline;
import com.example.airline.domain.repositories.AirlineRepository;
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
@DisplayName("AirlineService Tests")
class AirlineServiceImplTest {

    @Mock
    private AirlineRepository airlineRepository;

    @InjectMocks
    private AirlineServiceImpl airlineService;

    @Test
    @DisplayName("Crear aerolínea exitosamente")
    void testCreateAirline_Success() {
        // Arrange
        AirlineDtos.AirlineCreateRequest request = new AirlineDtos.AirlineCreateRequest("AA", "American Airlines");
        Airline airline = Airline.builder().id(1L).code("AA").name("American Airlines").build();

        when(airlineRepository.findByCode("AA")).thenReturn(Optional.empty());
        when(airlineRepository.save(any(Airline.class))).thenReturn(airline);

        // Act
        AirlineDtos.AirlineResponse response = airlineService.create(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.code()).isEqualTo("AA");
        assertThat(response.name()).isEqualTo("American Airlines");
        verify(airlineRepository, times(1)).save(any(Airline.class));
    }

    @Test
    @DisplayName("Crear aerolínea - Código ya existe")
    void testCreateAirline_CodeAlreadyExists() {
        // Arrange
        AirlineDtos.AirlineCreateRequest request = new AirlineDtos.AirlineCreateRequest("AA", "American Airlines");
        Airline existing = Airline.builder().id(1L).code("AA").name("American Airlines").build();

        when(airlineRepository.findByCode("AA")).thenReturn(Optional.of(existing));

        // Act & Assert
        assertThatThrownBy(() -> airlineService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");
        verify(airlineRepository, never()).save(any());
    }

    @Test
    @DisplayName("Buscar aerolínea por ID")
    void testFindById_Success() {
        // Arrange
        Airline airline = Airline.builder().id(1L).code("AA").name("American Airlines").build();
        when(airlineRepository.findById(1L)).thenReturn(Optional.of(airline));

        // Act
        AirlineDtos.AirlineResponse response = airlineService.findById(1L);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Buscar aerolínea por código")
    void testFindByCode_Success() {
        // Arrange
        Airline airline = Airline.builder().id(1L).code("AA").name("American Airlines").build();
        when(airlineRepository.findByCode("AA")).thenReturn(Optional.of(airline));

        // Act
        AirlineDtos.AirlineResponse response = airlineService.findByCode("AA");

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.code()).isEqualTo("AA");
    }

    @Test
    @DisplayName("Buscar todas las aerolíneas")
    void testFindAll() {
        // Arrange
        List<Airline> airlines = List.of(
                Airline.builder().id(1L).code("AA").name("American Airlines").build(),
                Airline.builder().id(2L).code("DL").name("Delta Airlines").build()
        );
        when(airlineRepository.findAll()).thenReturn(airlines);

        // Act
        List<AirlineDtos.AirlineResponse> responses = airlineService.findAll();

        // Assert
        assertThat(responses).hasSize(2);
    }

    @Test
    @DisplayName("Actualizar aerolínea")
    void testUpdateAirline_Success() {
        // Arrange
        Airline airline = Airline.builder().id(1L).code("AA").name("American Airlines").build();
        AirlineDtos.AirlineCreateRequest request = new AirlineDtos.AirlineCreateRequest("AA", "American Airlines Updated");

        when(airlineRepository.findById(1L)).thenReturn(Optional.of(airline));
        when(airlineRepository.save(any(Airline.class))).thenReturn(airline);

        // Act
        AirlineDtos.AirlineResponse response = airlineService.update(1L, request);

        // Assert
        assertThat(response).isNotNull();
        verify(airlineRepository, times(1)).save(any(Airline.class));
    }

    @Test
    @DisplayName("Eliminar aerolínea")
    void testDeleteAirline_Success() {
        // Arrange
        when(airlineRepository.existsById(1L)).thenReturn(true);

        // Act
        airlineService.delete(1L);

        // Assert
        verify(airlineRepository, times(1)).deleteById(1L);
    }
}