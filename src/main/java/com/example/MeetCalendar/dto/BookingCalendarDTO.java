package com.example.MeetCalendar.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

/**
 * DTO to send to calendar.
 */
@Getter
@Setter
public class BookingCalendarDTO {
    private Long bookingId;
    private String title;
    private String roomName;
    private OffsetDateTime startAt;
    private OffsetDateTime endAt;
    private boolean canCancel;
    private String status;
}