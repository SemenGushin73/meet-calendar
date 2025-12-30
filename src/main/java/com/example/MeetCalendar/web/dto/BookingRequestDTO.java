package com.example.MeetCalendar.web.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Booking create request DTO (MVC form / REST payload).
 */
@Getter
@Setter
public class BookingRequestDTO {
    private Long roomId;
    private String title;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
}