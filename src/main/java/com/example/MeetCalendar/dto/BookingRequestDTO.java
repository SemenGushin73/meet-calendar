package com.example.MeetCalendar.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO for request booking.
 */
@Getter
@Setter
public class BookingRequestDTO {
    private Long roomId;
    private String title;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
}