package com.example.MeetCalendar.repository;

import java.time.OffsetDateTime;

/**
 * Projection for calendar booking rows returned by repository queries.
 */
public interface BookingCalendarRow {
    Long getBookingId();

    String getTitle();

    String getRoomName();

    OffsetDateTime getStartAt();

    OffsetDateTime getEndAt();

    Long getOwnerId();

}