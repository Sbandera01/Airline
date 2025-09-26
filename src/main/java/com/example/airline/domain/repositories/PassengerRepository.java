package com.example.airline.domain.repositories;

import com.example.airline.domain.entities.Passenger;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PassengerRepository extends JpaRepository<Passenger, Long> {

    // Busca por email ignorando mayúsculas/minúsculas
    Optional<Passenger> findByEmailIgnoreCase(String email);

    // Busca por email ignorando mayúsculas y precarga el profile
    @Query("SELECT p FROM Passenger p LEFT JOIN FETCH p.profile WHERE LOWER(p.email) = LOWER(:email)")
    Optional<Passenger> findByEmailIgnoreCaseWithProfile(@Param("email") String email);
}

