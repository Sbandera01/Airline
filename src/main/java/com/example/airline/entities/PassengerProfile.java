package com.example.airline.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PassengerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String phone;
    private String countryCode;
}

