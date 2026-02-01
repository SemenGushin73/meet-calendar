package com.example.MeetCalendar.web.api.v1.contract;

import com.example.MeetCalendar.web.api.dto.BookingCreateRequest;
import com.example.MeetCalendar.web.api.dto.BookingResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;

/**
 * REST contract for booking endpoints.
 * <p>
 * This interface defines booking-related HTTP operations exposed under {@code /api/v1/bookings}.
 * Implementation is provided by {@code BookingRestController}.
 * <p>
 * Authentication is based on an HTTP session (JSESSIONID cookie). The {@link Principal} identifies
 * the currently authenticated user.
 */
public interface BookingRestControllerContract {
    /**
     * Creates a new booking for the authenticated user.
     * <p>
     * The booking is created for the meeting room specified in the request for the given time range.
     * Overlapping bookings are not allowed.
     *
     * @param request   request payload containing room id, title and start/end time (validated)
     * @param principal authenticated user principal (must not be {@code null})
     * @return created booking response
     * @throws com.example.MeetCalendar.exception.BookingValidationException if request data is invalid
     * @throws com.example.MeetCalendar.exception.BookingNotFoundException   if the room or user was not found
     * @throws com.example.MeetCalendar.exception.BookingOverlapException    if the room is already booked for this time
     */
    BookingResponse create(@Valid @RequestBody BookingCreateRequest request, Principal principal);

    /**
     * Cancels (deletes) an existing booking by id.
     * <p>
     * Only the booking owner or an administrator is allowed to cancel a booking.
     * Cancellation is forbidden if the booking has already ended.
     *
     * @param id        booking identifier
     * @param principal authenticated user principal (must not be {@code null})
     * @throws com.example.MeetCalendar.exception.BookingValidationException   if cancellation is not allowed
     * @throws com.example.MeetCalendar.exception.BookingNotFoundException     if the booking or user was not found
     * @throws com.example.MeetCalendar.exception.BookingAccessDeniedException if the user has no rights to cancel
     */
    void cancel(@PathVariable Long id, Principal principal);
}