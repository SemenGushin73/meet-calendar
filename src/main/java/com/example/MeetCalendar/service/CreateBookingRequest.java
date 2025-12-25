package com.example.MeetCalendar.service;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class CreateBookingRequest {
    private Long roomId;
    private String title;
    private OffsetDateTime startAt;
    private OffsetDateTime endAt;
}