package com.example.airline.domain.repositories;

import com.example.airline.domain.entities.PassengerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PassengerProfileRepository extends JpaRepository<PassengerProfile, Long> {
}
