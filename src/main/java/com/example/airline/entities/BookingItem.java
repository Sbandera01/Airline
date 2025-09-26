package com.example.airline.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String cabin;
    private BigDecimal price;
    private Integer segmentOrder;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @ManyToOne
    @JoinColumn(name = "flight_id")
    private Flight flight;
}
