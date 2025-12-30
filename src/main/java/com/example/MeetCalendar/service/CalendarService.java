package com.example.MeetCalendar.service;

import com.example.MeetCalendar.web.dto.BookingCalendarDTO;
import com.example.MeetCalendar.web.model.CalendarPageModel;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Calendar page operations.
 * <p>
 * Builds view models for rendering the calendar and
 * provides calendar-specific booking projection (DTO).
 */
public interface CalendarService {

    /**
     * Builds a model for calendar page rendering.
     *
     * @param username authenticated username (must not be blank)
     * @param week     optional week selector in format {@code yyyy-MM-dd}; if null/blank, current week is used
     * @return prepared calendar page model
     * @throws com.example.MeetCalendar.exception.BookingValidationException if week format is invalid or input is invalid
     * @throws com.example.MeetCalendar.exception.BookingNotFoundException   if user was not found
     */
    CalendarPageModel buildCalendarPage(String username, String week);

    /**
     * Returns bookings for the given user in a calendar-friendly representation (DTO).
     *
     * @param username   authenticated username (must not be blank)
     * @param anyDayWeek any date-time within a target week (must not be null)
     * @return list of booking calendar DTOs
     * @throws com.example.MeetCalendar.exception.BookingValidationException if input data is invalid
     * @throws com.example.MeetCalendar.exception.BookingNotFoundException   if user was not found
     */
    List<BookingCalendarDTO> getBookingCalendarForUser(String username, OffsetDateTime anyDayWeek);

}