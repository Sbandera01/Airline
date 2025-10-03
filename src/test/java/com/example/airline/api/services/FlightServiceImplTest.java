package com.example.airline.api.services;

import com.example.airline.api.dto.FlightDtos;
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

import java.time.OffsetDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FlightService Tests")
class FlightServiceImplTest {

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private AirlineRepository airlineRepository;

    @Mock
    private AirportRepository airportRepository;

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private FlightServiceImpl flightService;

    private Flight flight;
    private Airline airline;
    private Airport origin;
    private Airport destination;
    private Tag tag;

    @BeforeEach
    void setUp() {
        airline = Airline.builder()
                .id(1L)
                .code("AA")
                .name("American Airlines")
                .build();

        origin = Airport.builder()
                .id(1L)
                .code("JFK")
                .name("John F. Kennedy")
                .city("New York")
                .build();

        destination = Airport.builder()
                .id(2L)
                .code("LAX")
                .name("Los Angeles International")
                .city("Los Angeles")
                .build();

        tag = Tag.builder()
                .id(1L)
                .name("Direct")
                .build();

        flight = Flight.builder()
                .id(1L)
                .number("AA123")
                .departureTime(OffsetDateTime.now().plusDays(1))
                .arrivalTime(OffsetDateTime.now().plusDays(1).plusHours(5))
                .airline(airline)
                .origin(origin)
                .destination(destination)
                .tags(new HashSet<>(Set.of(tag)))
                .build();
    }

    @Test
    @DisplayName("Crear vuelo exitosamente")
    void testCreateFlight_Success() {
        // Arrange
        FlightDtos.FlightCreateRequest request = new FlightDtos.FlightCreateRequest(
                "AA123",
                OffsetDateTime.now().plusDays(1),
                OffsetDateTime.now().plusDays(1).plusHours(5),
                1L,
                1L,
                2L,
                Set.of("Direct", "Popular")
        );

        Tag popularTag = Tag.builder().id(2L).name("Popular").build();

        when(airlineRepository.findById(1L)).thenReturn(Optional.of(airline));
        when(airportRepository.findById(1L)).thenReturn(Optional.of(origin));
        when(airportRepository.findById(2L)).thenReturn(Optional.of(destination));
        when(tagRepository.findByName("Direct")).thenReturn(Optional.of(tag));
        when(tagRepository.findByName("Popular")).thenReturn(Optional.empty());
        when(tagRepository.save(any(Tag.class))).thenReturn(popularTag);
        when(flightRepository.save(any(Flight.class))).thenReturn(flight);

        // Act
        FlightDtos.FlightResponse response = flightService.create(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.number()).isEqualTo("AA123");
        verify(flightRepository, times(1)).save(any(Flight.class));
        verify(tagRepository, times(1)).save(any(Tag.class)); // Se crea el tag "Popular"
    }

    @Test
    @DisplayName("Crear vuelo - Aerolínea no encontrada")
    void testCreateFlight_AirlineNotFound() {
        // Arrange
        FlightDtos.FlightCreateRequest request = new FlightDtos.FlightCreateRequest(
                "AA123",
                OffsetDateTime.now().plusDays(1),
                OffsetDateTime.now().plusDays(1).plusHours(5),
                999L,
                1L,
                2L,
                null
        );

        when(airlineRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> flightService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Airline not found");
    }

    @Test
    @DisplayName("Crear vuelo - Aeropuerto de origen no encontrado")
    void testCreateFlight_OriginAirportNotFound() {
        // Arrange
        FlightDtos.FlightCreateRequest request = new FlightDtos.FlightCreateRequest(
                "AA123",
                OffsetDateTime.now().plusDays(1),
                OffsetDateTime.now().plusDays(1).plusHours(5),
                1L,
                999L,
                2L,
                null
        );

        when(airlineRepository.findById(1L)).thenReturn(Optional.of(airline));
        when(airportRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> flightService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Origin airport not found");
    }

    @Test
    @DisplayName("Crear vuelo - Aeropuerto de destino no encontrado")
    void testCreateFlight_DestinationAirportNotFound() {
        // Arrange
        FlightDtos.FlightCreateRequest request = new FlightDtos.FlightCreateRequest(
                "AA123",
                OffsetDateTime.now().plusDays(1),
                OffsetDateTime.now().plusDays(1).plusHours(5),
                1L,
                1L,
                999L,
                null
        );

        when(airlineRepository.findById(1L)).thenReturn(Optional.of(airline));
        when(airportRepository.findById(1L)).thenReturn(Optional.of(origin));
        when(airportRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> flightService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Destination airport not found");
    }

    @Test
    @DisplayName("Buscar vuelo por ID exitosamente")
    void testFindById_Success() {
        // Arrange
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));

        // Act
        FlightDtos.FlightResponse response = flightService.findById(1L);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.number()).isEqualTo("AA123");
        verify(flightRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Buscar vuelo por ID - No encontrado")
    void testFindById_NotFound() {
        // Arrange
        when(flightRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> flightService.findById(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Flight not found");
    }

    @Test
    @DisplayName("Buscar todos los vuelos")
    void testFindAll() {
        // Arrange
        List<Flight> flights = List.of(flight);
        when(flightRepository.findAll()).thenReturn(flights);

        // Act
        List<FlightDtos.FlightResponse> responses = flightService.findAll();

        // Assert
        assertThat(responses).hasSize(1);
        assertThat(responses.getFirst().number()).isEqualTo("AA123");
    }

    @Test
    @DisplayName("Buscar vuelos por nombre de aerolínea")
    void testFindByAirlineName() {
        // Arrange
        when(flightRepository.findByAirline_Name("American Airlines"))
                .thenReturn(List.of(flight));

        // Act
        List<FlightDtos.FlightResponse> responses =
                flightService.findByAirlineName("American Airlines");

        // Assert
        assertThat(responses).hasSize(1);
        assertThat(responses.getFirst().airline().name()).isEqualTo("American Airlines");
    }

    @Test
    @DisplayName("Buscar vuelos con paginación")
    void testSearchFlights() {
        // Arrange
        OffsetDateTime from = OffsetDateTime.now().plusDays(1);
        OffsetDateTime to = OffsetDateTime.now().plusDays(7);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Flight> flightPage = new PageImpl<>(List.of(flight));

        when(flightRepository.findByOrigin_CodeAndDestination_CodeAndDepartureTimeBetween(
                "JFK", "LAX", from, to, pageable))
                .thenReturn(flightPage);

        // Act
        Page<FlightDtos.FlightResponse> responses =
                flightService.searchFlights("JFK", "LAX", from, to, pageable);

        // Assert
        assertThat(responses.getContent()).hasSize(1);
        assertThat(responses.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("Buscar vuelos con asociaciones precargadas")
    void testSearchWithAssociations() {
        // Arrange
        OffsetDateTime from = OffsetDateTime.now().plusDays(1);
        OffsetDateTime to = OffsetDateTime.now().plusDays(7);

        when(flightRepository.searchWithAssociations("JFK", "LAX", from, to))
                .thenReturn(List.of(flight));

        // Act
        List<FlightDtos.FlightResponse> responses =
                flightService.searchWithAssociations("JFK", "LAX", from, to);

        // Assert
        assertThat(responses).hasSize(1);
        assertThat(responses.getFirst().origin().code()).isEqualTo("JFK");
        assertThat(responses.getFirst().destination().code()).isEqualTo("LAX");
    }

    @Test
    @DisplayName("Buscar vuelos con todos los tags especificados")
    void testFindFlightsWithAllTags() {
        // Arrange
        List<String> tags = List.of("Direct", "Popular");
        when(flightRepository.findFlightsWithAllTags(tags, 2L))
                .thenReturn(List.of(flight));

        // Act
        List<FlightDtos.FlightResponse> responses =
                flightService.findFlightsWithAllTags(tags);

        // Assert
        assertThat(responses).hasSize(1);
        verify(flightRepository, times(1)).findFlightsWithAllTags(tags, 2L);
    }

    @Test
    @DisplayName("Buscar vuelos con tags - Lista vacía")
    void testFindFlightsWithAllTags_EmptyList() {
        // Act
        List<FlightDtos.FlightResponse> responses =
                flightService.findFlightsWithAllTags(List.of());

        // Assert
        assertThat(responses).isEmpty();
        verify(flightRepository, never()).findFlightsWithAllTags(anyList(), anyLong());
    }

    @Test
    @DisplayName("Actualizar vuelo exitosamente")
    void testUpdateFlight_Success() {
        // Arrange
        FlightDtos.FlightCreateRequest request = new FlightDtos.FlightCreateRequest(
                "AA124",
                OffsetDateTime.now().plusDays(2),
                OffsetDateTime.now().plusDays(2).plusHours(5),
                1L,
                1L,
                2L,
                Set.of("Updated")
        );

        Tag updatedTag = Tag.builder().id(3L).name("Updated").build();

        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));
        when(airlineRepository.findById(1L)).thenReturn(Optional.of(airline));
        when(airportRepository.findById(1L)).thenReturn(Optional.of(origin));
        when(airportRepository.findById(2L)).thenReturn(Optional.of(destination));
        when(tagRepository.findByName("Updated")).thenReturn(Optional.of(updatedTag));
        when(flightRepository.save(any(Flight.class))).thenReturn(flight);

        // Act
        FlightDtos.FlightResponse response = flightService.update(1L, request);

        // Assert
        assertThat(response).isNotNull();
        verify(flightRepository, times(1)).save(any(Flight.class));
    }

    @Test
    @DisplayName("Actualizar vuelo - No encontrado")
    void testUpdateFlight_NotFound() {
        // Arrange
        FlightDtos.FlightCreateRequest request = new FlightDtos.FlightCreateRequest(
                "AA124",
                OffsetDateTime.now().plusDays(2),
                OffsetDateTime.now().plusDays(2).plusHours(5),
                1L,
                1L,
                2L,
                null
        );

        when(flightRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> flightService.update(999L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Flight not found");
    }

    @Test
    @DisplayName("Eliminar vuelo exitosamente")
    void testDeleteFlight_Success() {
        // Arrange
        when(flightRepository.existsById(1L)).thenReturn(true);
        doNothing().when(flightRepository).deleteById(1L);

        // Act
        flightService.delete(1L);

        // Assert
        verify(flightRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Eliminar vuelo - No encontrado")
    void testDeleteFlight_NotFound() {
        // Arrange
        when(flightRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> flightService.delete(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Flight not found");

        verify(flightRepository, never()).deleteById(anyLong());
    }
}
