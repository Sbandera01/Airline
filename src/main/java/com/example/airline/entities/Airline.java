package com.example.airline.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Airline {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String code;
    private String name;

    @OneToMany(mappedBy = "airline")
    private List<Flight> flights;
}
