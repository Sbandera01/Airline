package com.example.airline.api.mapper;

import com.example.airline.api.dto.BookingDtos;
import com.example.airline.domain.entities.Booking;
import com.example.airline.domain.entities.BookingItem;

import java.util.List;
import java.util.stream.Collectors;

public class BookingMapper {

    public static Booking toEntity(BookingDtos.BookingCreateRequest dto) {
        if (dto == null) return null;
        Booking booking = new Booking();
        // passenger se asocia en el service
        return booking;
    }

    public static BookingItem toEntity(BookingDtos.BookingItemRequest dto) {
        if (dto == null) return null;
        BookingItem item = new BookingItem();
        item.setCabin(dto.cabin());
        item.setPrice(dto.price());
        item.setSegmentOrder(dto.segmentOrder());
        // flight se asigna en el service
        return item;
    }

    public static BookingDtos.BookingResponse toResponse(Booking entity) {
        if (entity == null) return null;
        List<BookingDtos.BookingItemResponse> items = entity.getItems() == null ? List.of() :
                entity.getItems().stream().map(BookingMapper::toResponse).collect(Collectors.toList());
        return new BookingDtos.BookingResponse(
                entity.getId(),
                entity.getCreatedAt(),
                PassengerMapper.toResponse(entity.getPassenger()),
                items
        );
    }

    public static BookingDtos.BookingItemResponse toResponse(BookingItem entity) {
        if (entity == null) return null;
        return new BookingDtos.BookingItemResponse(
                entity.getId(),
                entity.getCabin(),
                entity.getPrice(),
                entity.getSegmentOrder(),
                FlightMapper.toResponse(entity.getFlight())
        );
    }
}

