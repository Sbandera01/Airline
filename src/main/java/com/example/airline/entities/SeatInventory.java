package com.example.airline.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String cabin;
    private Integer totalSeats;
    private Integer availableSeats;

    @ManyToOne
    @JoinColumn(name = "flight_id")
    private Flight flight;
}
