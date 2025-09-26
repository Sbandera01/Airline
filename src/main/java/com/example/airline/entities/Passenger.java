package com.example.airline.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Passenger {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String fullName;
    private String email;

    @OneToOne
    @JoinColumn(name = "profile_id")
    private PassengerProfile profile;
}

