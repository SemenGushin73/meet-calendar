package com.example.MeetCalendar.service;

import com.example.MeetCalendar.entity.Booking;
import com.example.MeetCalendar.web.dto.BookingRequestDTO;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Booking domain operations.
 * <p>
 * Provides use cases for creating and cancelling bookings
 * and fetching bookings for a specific week.
 */
public interface BookingService {
    /**
     * Creates a new booking for the given user.
     *
     * @param username authenticated username (must not be blank)
     * @param request  booking request data (must not be null)
     * @return created booking entity
     * @throws com.example.MeetCalendar.exception.BookingValidationException if input data is invalid
     * @throws com.example.MeetCalendar.exception.BookingNotFoundException   if user or room was not found
     * @throws com.example.MeetCalendar.exception.BookingOverlapException    if room is already booked for the requested time
     */
    Booking createBooking(String username, BookingRequestDTO request);

    /**
     * Cancels an existing booking.
     *
     * @param bookingId booking id (must not be null)
     * @param username  authenticated username (must not be blank)
     * @throws com.example.MeetCalendar.exception.BookingNotFoundException     if booking or user was not found
     * @throws com.example.MeetCalendar.exception.BookingAccessDeniedException if the user has no rights to cancel this booking
     * @throws com.example.MeetCalendar.exception.BookingValidationException   if cancellation is not allowed (e.g. booking already ended)
     */
    void cancelBooking(Long bookingId, String username);

    /**
     * Returns all bookings for the given room within the week containing {@code anyDayWeek}.
     *
     * @param roomId     room id (must not be null)
     * @param anyDayWeek any date-time within a target week (must not be null)
     * @return list of bookings for the week, sorted by start time
     * @throws com.example.MeetCalendar.exception.BookingNotFoundException   if room was not found
     * @throws com.example.MeetCalendar.exception.BookingValidationException if input data is invalid
     */
    List<Booking> getRoomBookingsForWeek(Long roomId, OffsetDateTime anyDayWeek);

    /**
     * Returns all bookings for the given user within the week containing {@code anyDayWeek}.
     *
     * @param username   authenticated username (must not be blank)
     * @param anyDayWeek any date-time within a target week (must not be null)
     * @return list of bookings for the week, sorted by start time
     * @throws com.example.MeetCalendar.exception.BookingNotFoundException   if user was not found
     * @throws com.example.MeetCalendar.exception.BookingValidationException if input data is invalid
     */
    List<Booking> getMyBookingsForWeek(String username, OffsetDateTime anyDayWeek);

}