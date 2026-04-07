package com.example.MeetCalendar.web.api.v1.bookings;

import com.example.MeetCalendar.entity.Booking;
import com.example.MeetCalendar.service.BookingService;
import com.example.MeetCalendar.web.api.dto.BookingCreateRequest;
import com.example.MeetCalendar.web.api.dto.BookingResponse;
import com.example.MeetCalendar.web.api.mapper.BookingApiMapper;
import com.example.MeetCalendar.web.dto.BookingRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Facade for booking REST API: maps DTOs and delegates to booking domain services.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BookingApiHandler {
    private final BookingService bookingService;
    private final BookingApiMapper bookingApiMapper;

    /**
     * Creates a new booking for the authenticated user.
     */
    public BookingResponse create(String username, BookingCreateRequest request) {
        log.debug("API create booking: user={}, roomId={}, startAt={}, endAt={}", username, request.getRoomId(), request.getStartAt(), request.getEndAt());
        BookingRequestDTO dto = bookingApiMapper.toServiceDto(request);
        Booking booking = bookingService.createBooking(username, dto);
        return bookingApiMapper.toResponse(booking);
    }

    /**
     * Cancels an existing booking by id.
     */
    public void cancel(Long id, String username) {
        log.debug("API cancel booking: user={}, bookingId={}", username, id);
        bookingService.cancelBooking(id, username);
    }
}