package com.example.airline.domain.repositories;

import com.example.airline.domain.entities.Flight;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

public interface FlightRepository extends JpaRepository<Flight, Long> {

    // vuelos operados por una aerolínea (por nombre)
    List<Flight> findByAirline_Name(String airlineName);

    // búsqueda por origen, destino y ventana
    Page<Flight> findByOrigin_CodeAndDestination_CodeAndDepartureTimeBetween(
            String origin,
            String destination,
            OffsetDateTime from,
            OffsetDateTime to,
            Pageable pageable
    );

    // filtro con joins y precargas
    @Query("""
           SELECT f FROM Flight f
           LEFT JOIN FETCH f.airline
           LEFT JOIN FETCH f.origin
           LEFT JOIN FETCH f.destination
           LEFT JOIN FETCH f.tags
           WHERE (:origin IS NULL OR f.origin.code = :origin)
           AND (:destination IS NULL OR f.destination.code = :destination)
           AND f.departureTime BETWEEN :from AND :to
           """)
    List<Flight> searchWithAssociations(
            @Param("origin") String origin,
            @Param("destination") String destination,
            @Param("from") OffsetDateTime from,
            @Param("to") OffsetDateTime to
    );

    // vuelos con todas las tags dadas
    @Query(value = """
           SELECT f.* FROM flight f
           JOIN flight_tags ft ON f.id = ft.flight_id
           JOIN tag t ON t.id = ft.tag_id
           WHERE t.name IN (:tags)
           GROUP BY f.id
           HAVING COUNT(DISTINCT t.name) = :required
           """, nativeQuery = true)
    List<Flight> findFlightsWithAllTags(@Param("tags") List<String> tags, @Param("required") long required);
}

