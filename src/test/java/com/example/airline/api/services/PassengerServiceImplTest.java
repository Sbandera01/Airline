package com.example.airline.api.services;

import com.example.airline.api.dto.PassengerDtos;
import com.example.airline.domain.entities.Passenger;
import com.example.airline.domain.entities.PassengerProfile;
import com.example.airline.domain.repositories.PassengerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PassengerService Tests")
class PassengerServiceImplTest {

    @Mock
    private PassengerRepository passengerRepository;

    @InjectMocks
    private PassengerServiceImpl passengerService;

    private Passenger passenger;

    @BeforeEach
    void setUp() {
        PassengerProfile profile = PassengerProfile.builder()
                .id(1L)
                .phone("+1234567890")
                .countryCode("US")
                .build();

        passenger = Passenger.builder()
                .id(1L)
                .fullName("John Doe")
                .email("john@example.com")
                .profile(profile)
                .build();
    }

    @Test
    @DisplayName("Crear pasajero exitosamente con perfil")
    void testCreatePassenger_WithProfile_Success() {
        // Arrange
        PassengerDtos.PassengerProfileRequest profileRequest =
                new PassengerDtos.PassengerProfileRequest("+1234567890", "US");
        PassengerDtos.PassengerCreateRequest request =
                new PassengerDtos.PassengerCreateRequest("John Doe", "john@example.com", profileRequest);

        when(passengerRepository.findByEmailIgnoreCase("john@example.com"))
                .thenReturn(Optional.empty());
        when(passengerRepository.save(any(Passenger.class))).thenReturn(passenger);

        // Act
        PassengerDtos.PassengerResponse response = passengerService.create(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.fullName()).isEqualTo("John Doe");
        assertThat(response.email()).isEqualTo("john@example.com");
        assertThat(response.profile()).isNotNull();
        assertThat(response.profile().phone()).isEqualTo("+1234567890");
        verify(passengerRepository, times(1)).save(any(Passenger.class));
    }

    @Test
    @DisplayName("Crear pasajero sin perfil")
    void testCreatePassenger_WithoutProfile_Success() {
        // Arrange
        PassengerDtos.PassengerCreateRequest request =
                new PassengerDtos.PassengerCreateRequest("Jane Doe", "jane@example.com", null);

        Passenger passengerWithoutProfile = Passenger.builder()
                .id(2L)
                .fullName("Jane Doe")
                .email("jane@example.com")
                .profile(null)
                .build();

        when(passengerRepository.findByEmailIgnoreCase("jane@example.com"))
                .thenReturn(Optional.empty());
        when(passengerRepository.save(any(Passenger.class))).thenReturn(passengerWithoutProfile);

        // Act
        PassengerDtos.PassengerResponse response = passengerService.create(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.profile()).isNull();
        verify(passengerRepository, times(1)).save(any(Passenger.class));
    }

    @Test
    @DisplayName("Crear pasajero - Email ya existe")
    void testCreatePassenger_EmailAlreadyExists() {
        // Arrange
        PassengerDtos.PassengerCreateRequest request =
                new PassengerDtos.PassengerCreateRequest("John Doe", "john@example.com", null);

        when(passengerRepository.findByEmailIgnoreCase("john@example.com"))
                .thenReturn(Optional.of(passenger));

        // Act & Assert
        assertThatThrownBy(() -> passengerService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");

        verify(passengerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Buscar pasajero por ID exitosamente")
    void testFindById_Success() {
        // Arrange
        when(passengerRepository.findById(1L)).thenReturn(Optional.of(passenger));

        // Act
        PassengerDtos.PassengerResponse response = passengerService.findById(1L);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        verify(passengerRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Buscar pasajero por ID - No encontrado")
    void testFindById_NotFound() {
        // Arrange
        when(passengerRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> passengerService.findById(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Passenger not found");
    }

    @Test
    @DisplayName("Buscar pasajero por email exitosamente")
    void testFindByEmail_Success() {
        // Arrange
        when(passengerRepository.findByEmailIgnoreCaseWithProfile("john@example.com"))
                .thenReturn(Optional.of(passenger));

        // Act
        PassengerDtos.PassengerResponse response = passengerService.findByEmail("john@example.com");

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.email()).isEqualTo("john@example.com");
        verify(passengerRepository, times(1)).findByEmailIgnoreCaseWithProfile("john@example.com");
    }

    @Test
    @DisplayName("Buscar pasajero por email - No encontrado")
    void testFindByEmail_NotFound() {
        // Arrange
        when(passengerRepository.findByEmailIgnoreCaseWithProfile("nonexistent@example.com"))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> passengerService.findByEmail("nonexistent@example.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Passenger not found");
    }

    @Test
    @DisplayName("Buscar todos los pasajeros")
    void testFindAll() {
        // Arrange
        List<Passenger> passengers = List.of(passenger);
        when(passengerRepository.findAll()).thenReturn(passengers);

        // Act
        List<PassengerDtos.PassengerResponse> responses = passengerService.findAll();

        // Assert
        assertThat(responses).hasSize(1);
        assertThat(responses.getFirst().email()).isEqualTo("john@example.com");
    }

    @Test
    @DisplayName("Actualizar pasajero exitosamente")
    void testUpdatePassenger_Success() {
        // Arrange
        PassengerDtos.PassengerProfileRequest profileRequest =
                new PassengerDtos.PassengerProfileRequest("+9876543210", "UK");
        PassengerDtos.PassengerCreateRequest request =
                new PassengerDtos.PassengerCreateRequest("John Updated", "john@example.com", profileRequest);

        when(passengerRepository.findById(1L)).thenReturn(Optional.of(passenger));
        when(passengerRepository.findByEmailIgnoreCase("john@example.com"))
                .thenReturn(Optional.of(passenger));
        when(passengerRepository.save(any(Passenger.class))).thenReturn(passenger);

        // Act
        PassengerDtos.PassengerResponse response = passengerService.update(1L, request);

        // Assert
        assertThat(response).isNotNull();
        verify(passengerRepository, times(1)).save(any(Passenger.class));
    }

    @Test
    @DisplayName("Actualizar pasajero - No encontrado")
    void testUpdatePassenger_NotFound() {
        // Arrange
        PassengerDtos.PassengerCreateRequest request =
                new PassengerDtos.PassengerCreateRequest("John Doe", "john@example.com", null);

        when(passengerRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> passengerService.update(999L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Passenger not found");
    }

    @Test
    @DisplayName("Actualizar pasajero - Email en uso por otro pasajero")
    void testUpdatePassenger_EmailInUse() {
        // Arrange
        Passenger anotherPassenger = Passenger.builder()
                .id(2L)
                .email("another@example.com")
                .build();

        PassengerDtos.PassengerCreateRequest request =
                new PassengerDtos.PassengerCreateRequest("John Doe", "another@example.com", null);

        when(passengerRepository.findById(1L)).thenReturn(Optional.of(passenger));
        when(passengerRepository.findByEmailIgnoreCase("another@example.com"))
                .thenReturn(Optional.of(anotherPassenger));

        // Act & Assert
        assertThatThrownBy(() -> passengerService.update(1L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already in use");
    }

    @Test
    @DisplayName("Actualizar pasajero - Crear perfil si no existe")
    void testUpdatePassenger_CreateProfileIfNotExists() {
        // Arrange
        Passenger passengerWithoutProfile = Passenger.builder()
                .id(1L)
                .fullName("John Doe")
                .email("john@example.com")
                .profile(null)
                .build();

        PassengerDtos.PassengerProfileRequest profileRequest =
                new PassengerDtos.PassengerProfileRequest("+1234567890", "US");
        PassengerDtos.PassengerCreateRequest request =
                new PassengerDtos.PassengerCreateRequest("John Doe", "john@example.com", profileRequest);

        when(passengerRepository.findById(1L)).thenReturn(Optional.of(passengerWithoutProfile));
        when(passengerRepository.findByEmailIgnoreCase("john@example.com"))
                .thenReturn(Optional.of(passengerWithoutProfile));
        when(passengerRepository.save(any(Passenger.class))).thenReturn(passenger);

        // Act
        PassengerDtos.PassengerResponse response = passengerService.update(1L, request);

        // Assert
        assertThat(response).isNotNull();
        verify(passengerRepository, times(1)).save(any(Passenger.class));
    }

    @Test
    @DisplayName("Eliminar pasajero exitosamente")
    void testDeletePassenger_Success() {
        // Arrange
        when(passengerRepository.existsById(1L)).thenReturn(true);
        doNothing().when(passengerRepository).deleteById(1L);

        // Act
        passengerService.delete(1L);

        // Assert
        verify(passengerRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Eliminar pasajero - No encontrado")
    void testDeletePassenger_NotFound() {
        // Arrange
        when(passengerRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> passengerService.delete(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Passenger not found");

        verify(passengerRepository, never()).deleteById(anyLong());
    }
}
