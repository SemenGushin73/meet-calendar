package com.example.MeetCalendar.repository;

import java.time.OffsetDateTime;

public interface BookingCalendarRow {
    Long getBookingId();

    String getTitle();

    String getRoomName();

    OffsetDateTime getStartAt();

    OffsetDateTime getEndAt();

    Long getOwnerId();

}