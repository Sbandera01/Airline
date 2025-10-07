package com.example.airline.api.mapper;

import com.example.airline.api.dto.BookingDtos;
import com.example.airline.domain.entities.Booking;
import com.example.airline.domain.entities.BookingItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {PassengerMapper.class, FlightMapper.class})
public interface BookingMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passenger", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "items", ignore = true)
    Booking toEntity(BookingDtos.BookingCreateRequest dto);

    @Mapping(target = "id",ignore = true)
    @Mapping(target = "booking", ignore = true)
    @Mapping(target = "flight", ignore = true)
    BookingItem toEntity(BookingDtos.BookingItemRequest dto);

    BookingDtos.BookingResponse toResponse(Booking entity);

    BookingDtos.BookingItemResponse toResponse(BookingItem entity);
}
