package com.example.airline.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private OffsetDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "passenger_id")
    private Passenger passenger;

    @OneToMany(mappedBy = "booking")
    private List<BookingItem> items;
}

