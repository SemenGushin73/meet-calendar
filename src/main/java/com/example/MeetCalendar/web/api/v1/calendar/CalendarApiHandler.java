package com.example.MeetCalendar.web.api.v1.calendar;

import com.example.MeetCalendar.exception.BookingValidationException;
import com.example.MeetCalendar.service.CalendarService;
import com.example.MeetCalendar.web.api.dto.CalendarWeekResponse;
import com.example.MeetCalendar.web.model.WeekWindow;
import com.example.MeetCalendar.web.dto.BookingCalendarDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.List;


/**
 * Facade for calendar REST API: resolves week parameters and delegates to calendar services.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CalendarApiHandler {

    private static final ZoneId APP_ZONE = ZoneId.systemDefault();

    private final CalendarService calendarService;

    /**
     * Builds calendar week response for the given user.
     */
    public CalendarWeekResponse week(String username, String week) {
        log.debug("Get calendar week: user={}, weekParam={}", username, week);
        OffsetDateTime anyDayWeek;
        if (username == null || username.isBlank()) {
            throw new BookingValidationException();
        }
        if (week == null || week.isBlank()) {
            anyDayWeek = OffsetDateTime.now(APP_ZONE);
        } else {
            try {
                LocalDate day = LocalDate.parse(week);
                anyDayWeek = day.atStartOfDay(APP_ZONE).toOffsetDateTime();
            } catch (DateTimeParseException e) {
                throw new BookingValidationException();
            }
        }

        WeekWindow w = calendarService.weekWindow(anyDayWeek);
        List<BookingCalendarDTO> bookings = calendarService.getBookingCalendarForUser(username, anyDayWeek);
        CalendarWeekResponse resp = new CalendarWeekResponse();
        resp.setWeekStart(w.getStart());
        resp.setWeekEnd(w.getEnd());
        resp.setPreviousWeek(w.getStart().toLocalDate().minusWeeks(1).toString());
        resp.setNextWeek(w.getStart().toLocalDate().plusWeeks(1).toString());
        resp.setBookings(bookings);
        log.debug("Calendar week built: user={}, weekStart={}, bookings={}", username, w.getStart(), bookings.size());
        return resp;
    }
}