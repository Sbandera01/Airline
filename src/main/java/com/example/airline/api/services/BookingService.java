package com.example.airline.api.services;

import com.example.airline.api.dto.BookingDtos;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface BookingService {
    BookingDtos.BookingResponse create(BookingDtos.BookingCreateRequest request);
    BookingDtos.BookingResponse findById(Long id);
    BookingDtos.BookingResponse findByIdWithDetails(Long id);
    List<BookingDtos.BookingResponse> findAll();
    Page<BookingDtos.BookingResponse> findByPassengerEmail(String email, Pageable pageable);
    List<BookingDtos.BookingItemResponse> findBookingItems(Long bookingId);
    BigDecimal calculateBookingTotal(Long bookingId);
    void cancelBooking(Long id);
}
