package com.example.airline.api.dto;
import lombok.*;

import java.io.Serializable;
@Getter
@Setter
@AllArgsConstructor
public class AirlineDtos {

    public record AirlineCreateRequest(String code, String name) implements Serializable {}

    public record AirlineResponse(Long id, String code, String name) implements Serializable {}
}

