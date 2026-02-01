package com.example.MeetCalendar.service;

import com.example.MeetCalendar.web.dto.BookingCalendarDTO;
import com.example.MeetCalendar.web.model.WeekWindow;

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
     * Returns bookings for the given user in a calendar-friendly representation (DTO).
     *
     * @param username   authenticated username (must not be blank)
     * @param anyDayWeek any date-time within a target week (must not be null)
     * @return list of booking calendar DTOs
     * @throws com.example.MeetCalendar.exception.BookingValidationException if input data is invalid
     * @throws com.example.MeetCalendar.exception.BookingNotFoundException   if user was not found
     */
    List<BookingCalendarDTO> getBookingCalendarForUser(String username, OffsetDateTime anyDayWeek);

    /**
     * Builds a Monday-based week window for the given date-time.
     * <p>
     * The returned window is {@code [weekStart, weekEnd)}:
     * <ul>
     *   <li>{@code weekStart} — Monday 00:00 of the week that contains {@code anyDayWeek}</li>
     *   <li>{@code weekEnd} — start of the next week (i.e., {@code weekStart + 1 week})</li>
     * </ul>
     *
     * @param anyDayWeek any date-time within the target week (must not be {@code null})
     * @return resolved week window (start inclusive, end exclusive)
     * @throws com.example.MeetCalendar.exception.BookingValidationException if {@code anyDayWeek} is {@code null}
     */
    WeekWindow weekWindow(OffsetDateTime anyDayWeek);

}