package com.example.MeetCalendar.web.model;

import lombok.Getter;

import java.time.OffsetDateTime;

/**
 * Represents start and end of a calendar week.
 */
@Getter
public class WeekWindow {
    private final OffsetDateTime start;
    private final OffsetDateTime end;

    public WeekWindow(OffsetDateTime start, OffsetDateTime end) {
        this.start = start;
        this.end = end;
    }
}