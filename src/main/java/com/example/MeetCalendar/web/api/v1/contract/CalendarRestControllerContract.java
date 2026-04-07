package com.example.MeetCalendar.web.api.v1.contract;

import com.example.MeetCalendar.web.api.dto.CalendarWeekResponse;

import java.security.Principal;

/**
 * REST contract for calendar endpoints.
 * <p>
 * This interface defines calendar-related HTTP operations exposed under {@code /api/v1/calendar}.
 * Implementation is provided by {@code CalendarRestController}.
 * <p>
 * Authentication is based on an HTTP session (JSESSIONID cookie). The {@link Principal} identifies
 * the currently authenticated user.
 */
public interface CalendarRestControllerContract {
    /**
     * Returns calendar data for a selected week.
     * <p>
     * The week is selected by a date key in {@code yyyy-MM-dd} format. The response contains:
     * week window (start/end), navigation keys for previous/next weeks, and bookings projected
     * into a calendar layout representation.
     * <p>
     * If {@code week} is not provided, the current week is used.
     *
     * @param week      optional week selector in {@code yyyy-MM-dd} format (any day within the week)
     * @param principal authenticated user principal (must not be {@code null})
     * @return calendar data for the resolved week
     * @throws com.example.MeetCalendar.exception.BookingValidationException if week format is invalid
     * @throws com.example.MeetCalendar.exception.BookingNotFoundException   if the user was not found
     */
    CalendarWeekResponse week(String week, Principal principal);
}