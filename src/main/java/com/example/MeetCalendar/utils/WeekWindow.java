package com.example.MeetCalendar.utils;

import lombok.Getter;

import java.time.OffsetDateTime;

/**
 * Model for a week.
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