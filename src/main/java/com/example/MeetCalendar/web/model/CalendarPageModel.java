package com.example.MeetCalendar.web.model;

import com.example.MeetCalendar.web.dto.BookingCalendarDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * View model for calendar page rendering.
 */
@Getter
@Setter
public class CalendarPageModel {
    OffsetDateTime anyDayWeek;
    LocalDate previousWeek;
    LocalDate nextWeek;
    List<LocalDate> days;
    List<BookingCalendarDTO> bookings;
}