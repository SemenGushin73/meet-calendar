package com.example.MeetCalendar.web.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

/**
 * Calendar booking DTO used for rendering calendar UI.
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
    private Integer topPx;
    private Integer heightPx;
    private String dayKey;
}