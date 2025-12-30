package com.example.MeetCalendar.service.impl;

import com.example.MeetCalendar.entity.User;
import com.example.MeetCalendar.exception.BookingNotFoundException;
import com.example.MeetCalendar.exception.BookingValidationException;
import com.example.MeetCalendar.mapper.BookingCalendarMapper;
import com.example.MeetCalendar.repository.BookingCalendarRow;
import com.example.MeetCalendar.repository.BookingRepository;
import com.example.MeetCalendar.repository.UserRepository;
import com.example.MeetCalendar.service.CalendarService;
import com.example.MeetCalendar.web.model.WeekWindow;
import com.example.MeetCalendar.web.dto.BookingCalendarDTO;
import com.example.MeetCalendar.web.model.CalendarPageModel;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import static java.time.DayOfWeek.MONDAY;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.TemporalAdjusters.previousOrSame;

@Service
public class CalendarServiceImpl implements CalendarService {
    static final ZoneId APP_ZONE = ZoneId.systemDefault();
    private static final int START_HOUR = 8;
    private static final int END_HOUR = 20;
    private static final int HOUR_HEIGHT = 48;
    private static final int dayStartMin = START_HOUR * 60;
    private static final int dayEndMin = (END_HOUR + 1) * 60;


    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final BookingCalendarMapper bookingCalendarMapper;

    public CalendarServiceImpl(UserRepository userRepository, BookingRepository bookingRepository, BookingCalendarMapper bookingCalendarMapper) {
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.bookingCalendarMapper = bookingCalendarMapper;
    }

    public CalendarPageModel buildCalendarPage(String username, String week) {
        OffsetDateTime anyDayWeek;
        if (week == null || week.isBlank()) {
            anyDayWeek = OffsetDateTime.now();
        } else {
            try {
                LocalDate day = LocalDate.parse(week);
                anyDayWeek = day.atStartOfDay(APP_ZONE).toOffsetDateTime();
            } catch (DateTimeParseException e) {
                throw new BookingValidationException();
            }
        }
        WeekWindow w = weekWindow(anyDayWeek);
        LocalDate weekStartDate = w.getStart().toLocalDate();
        LocalDate previousWeek = weekStartDate.minusWeeks(1);
        LocalDate nextWeek = weekStartDate.plusWeeks(1);
        List<BookingCalendarDTO> bookings = getBookingCalendarForUser(username, anyDayWeek);
        CalendarPageModel calendarPageModel = new CalendarPageModel();
        calendarPageModel.setAnyDayWeek(anyDayWeek);
        calendarPageModel.setBookings(bookings);
        calendarPageModel.setPreviousWeek(previousWeek);
        calendarPageModel.setNextWeek(nextWeek);
        return calendarPageModel;
    }

    public List<BookingCalendarDTO> getBookingCalendarForUser(String username, OffsetDateTime anyDayWeek) {
        if (anyDayWeek == null) {
            throw new BookingValidationException();
        }
        if (username == null) {
            throw new BookingValidationException();
        }
        if (username.isBlank()) {
            throw new BookingValidationException();
        }
        User user = userRepository.findByUsername(username).orElseThrow(BookingNotFoundException::new);
        Long userId = user.getId();
        WeekWindow w = weekWindow(anyDayWeek);
        OffsetDateTime weekStart = w.getStart();
        OffsetDateTime weekEnd = w.getEnd();
        List<BookingCalendarRow> rows = bookingRepository.findCalendarRowsForUserWeek(userId, weekStart, weekEnd);
        List<BookingCalendarDTO> dtos = new ArrayList<>();
        OffsetDateTime now = OffsetDateTime.now();
        boolean isAdmin = user.getRoles().stream().anyMatch(r -> "ADMIN".equals(r.getName()));
        for (BookingCalendarRow row : rows) {
            BookingCalendarDTO dto = bookingCalendarMapper.toDto(row);
            LocalDate eventDay = row.getStartAt().atZoneSameInstant(APP_ZONE).toLocalDate();
            dto.setDayKey(eventDay.toString());
            OffsetDateTime s = row.getStartAt().atZoneSameInstant(APP_ZONE).toOffsetDateTime();
            OffsetDateTime e = row.getEndAt().atZoneSameInstant(APP_ZONE).toOffsetDateTime();

            int startMin = s.getHour() * 60 + s.getMinute();
            int endMin = e.getHour() * 60 + e.getMinute();
            int visibleStart = Math.max(startMin, dayStartMin);
            int visibleEnd = Math.min(endMin, dayEndMin);

            if (visibleEnd <= dayStartMin || visibleStart >= dayEndMin) {
                continue;
            }

            int topPx = (int) Math.round(((visibleStart - dayStartMin) / 60.0) * HOUR_HEIGHT);
            int heightPx = (int) Math.max(18, Math.round(((visibleEnd - visibleStart) / 60.0) * HOUR_HEIGHT));

            dto.setTopPx(topPx);
            dto.setHeightPx(heightPx);

            boolean isOwner = row.getOwnerId().equals(userId);
            dto.setCanCancel(isOwner || isAdmin);
            if (row.getEndAt().isBefore(now)) {
                dto.setStatus("PAST");
            } else if (row.getStartAt().isAfter(now)) {
                dto.setStatus("UPCOMING");
            } else {
                dto.setStatus("ACTIVE");
            }
            dtos.add(dto);
        }
        return dtos;
    }

    public WeekWindow weekWindow(OffsetDateTime anyDayInWeek) {
        if (anyDayInWeek == null) {
            throw new BookingValidationException();
        }
        OffsetDateTime start = anyDayInWeek.with(previousOrSame(MONDAY)).truncatedTo(DAYS);
        OffsetDateTime end = start.plusWeeks(1);
        return new WeekWindow(start, end);

    }
}