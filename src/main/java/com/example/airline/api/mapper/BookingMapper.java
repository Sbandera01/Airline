package com.example.airline.api.mapper;

import com.example.airline.api.dto.BookingDtos.*;
import com.example.airline.domain.entities.*;
import com.example.airline.domain.repositories.*;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(
        config = MapStructConfig.class,
        componentModel = "spring",
        uses = {PassengerMapper.class, FlightMapper.class}
)
public abstract class BookingMapper {

    @Autowired
    protected PassengerRepository passengerRepository;

    @Autowired
    protected FlightRepository flightRepository;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.OffsetDateTime.now())")
    @Mapping(target = "passenger", source = "passengerEmail", qualifiedByName = "emailToPassenger")
    @Mapping(target = "items", source = "items", qualifiedByName = "itemRequestsToEntities")
    public abstract Booking toEntity(BookingCreateRequest dto);

    public abstract BookingResponse toResponse(Booking entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "booking", ignore = true)
    @Mapping(target = "flight", source = "flightId", qualifiedByName = "flightIdToEntity")
    public abstract BookingItem toItemEntity(BookingItemRequest dto);

    public abstract BookingItemResponse toItemResponse(BookingItem entity);

    public abstract List<BookingItemResponse> toItemResponseList(List<BookingItem> entities);

    @Named("emailToPassenger")
    protected Passenger mapEmailToPassenger(String email) {
        return email == null ? null :
                passengerRepository.findByEmailIgnoreCase(email)
                        .orElseThrow(() -> new IllegalArgumentException("Passenger not found with email: " + email));
    }

    @Named("flightIdToEntity")
    protected Flight mapFlightId(Long flightId) {
        return flightId == null ? null :
                flightRepository.findById(flightId)
                        .orElseThrow(() -> new IllegalArgumentException("Flight not found with id: " + flightId));
    }

    @Named("itemRequestsToEntities")
    protected List<BookingItem> mapItemRequests(List<BookingItemRequest> itemRequests) {
        if (itemRequests == null || itemRequests.isEmpty()) {
            return List.of();
        }
        return itemRequests.stream()
                .map(this::toItemEntity)
                .collect(Collectors.toList());
    }

    @AfterMapping
    protected void linkItemsToBooking(@MappingTarget Booking booking) {
        if (booking.getItems() != null) {
            booking.getItems().forEach(item -> item.setBooking(booking));
        }
    }
}
