package com.example.airline.api.mapper;

import com.example.airline.api.dto.FlightDtos.*;
import com.example.airline.domain.entities.*;
import com.example.airline.domain.repositories.*;
import lombok.Builder;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;
import java.util.stream.Collectors;

@Builder
@Mapper(
        config = MapStructConfig.class,
        componentModel = "spring",
        uses = {AirlineMapper.class, AirportMapper.class, TagMapper.class}
)
public abstract class FlightMapper {

    @Autowired
    protected AirlineRepository airlineRepository;

    @Autowired
    protected AirportRepository airportRepository;

    @Autowired
    protected TagRepository tagRepository;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "airline", source = "airlineId", qualifiedByName = "airlineIdToEntity")
    @Mapping(target = "origin", source = "originAirportId", qualifiedByName = "airportIdToEntity")
    @Mapping(target = "destination", source = "destinationAirportId", qualifiedByName = "airportIdToEntity")
    @Mapping(target = "tags", source = "tags", qualifiedByName = "tagNamesToEntities")
    public abstract Flight toEntity(FlightCreateRequest dto);

    public abstract FlightResponse toResponse(Flight entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "airline", ignore = true)
    @Mapping(target = "origin", ignore = true)
    @Mapping(target = "destination", ignore = true)
    @Mapping(target = "tags", ignore = true)
    public abstract void updateEntity(@MappingTarget Flight entity, FlightCreateRequest dto);

    @Named("airlineIdToEntity")
    protected Airline mapAirlineId(Long airlineId) {
        return airlineId == null ? null :
                airlineRepository.findById(airlineId)
                        .orElseThrow(() -> new IllegalArgumentException("Airline not found with id: " + airlineId));
    }

    @Named("airportIdToEntity")
    protected Airport mapAirportId(Long airportId) {
        return airportId == null ? null :
                airportRepository.findById(airportId)
                        .orElseThrow(() -> new IllegalArgumentException("Airport not found with id: " + airportId));
    }

    @Named("tagNamesToEntities")
    protected Set<Tag> mapTagNames(Set<String> tagNames) {
        if (tagNames == null || tagNames.isEmpty()) {
            return Set.of();
        }

        return tagNames.stream()
                .map(name -> tagRepository.findByName(name)
                        .orElseGet(() -> Tag.builder().name(name).build())) // ⚠️ Solo funciona si Tag tiene @Builder
                .collect(Collectors.toSet());
    }
}
