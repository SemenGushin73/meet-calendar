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
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static java.time.DayOfWeek.MONDAY;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.TemporalAdjusters.previousOrSame;

@Service
@Slf4j
public class CalendarServiceImpl implements CalendarService {
    private static final ZoneId APP_ZONE = ZoneId.systemDefault();
    private static final int START_HOUR = 8;
    private static final int END_HOUR = 20;
    private static final int HOUR_HEIGHT = 48;
    private static final int DAY_START_MIN = START_HOUR * 60;
    private static final int DAY_END_MIN = (END_HOUR + 1) * 60;
    private static final String ROLE_ADMIN = "ADMIN";
    private static final String STATUS_PAST = "PAST";
    private static final String STATUS_UPCOMING = "UPCOMING";
    private static final String STATUS_ACTIVE = "ACTIVE";
    private static final int MIN_BLOCK_HEIGHT_PX = 18;


    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final BookingCalendarMapper bookingCalendarMapper;

    public CalendarServiceImpl(UserRepository userRepository, BookingRepository bookingRepository, BookingCalendarMapper bookingCalendarMapper) {
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.bookingCalendarMapper = bookingCalendarMapper;
    }


    public List<BookingCalendarDTO> getBookingCalendarForUser(String username, OffsetDateTime anyDayWeek) {
        if (anyDayWeek == null || username == null || username.isBlank()) {
            throw new BookingValidationException();
        }

        User user = userRepository.findByUsername(username).orElseThrow(BookingNotFoundException::new);
        Long userId = user.getId();
        WeekWindow w = weekWindow(anyDayWeek);
        OffsetDateTime weekStart = w.getStart();
        OffsetDateTime weekEnd = w.getEnd();
        List<BookingCalendarRow> rows = bookingRepository.findCalendarRowsForUserWeek(userId, weekStart, weekEnd);
        log.debug("Build calendar: user={}, anyDayWeek={}, weekStart={}, weekEnd={}, rows={}", username, anyDayWeek, weekStart, weekEnd, rows.size());
        List<BookingCalendarDTO> dtos = new ArrayList<>();
        OffsetDateTime now = OffsetDateTime.now(APP_ZONE);
        boolean isAdmin = user.getRoles().stream().anyMatch(r -> ROLE_ADMIN.equals(r.getName()));
        for (BookingCalendarRow row : rows) {
            OffsetDateTime s = row.getStartAt().atZoneSameInstant(APP_ZONE).toOffsetDateTime();
            OffsetDateTime e = row.getEndAt().atZoneSameInstant(APP_ZONE).toOffsetDateTime();

            int startMin = s.getHour() * 60 + s.getMinute();
            int endMin = e.getHour() * 60 + e.getMinute();
            int visibleStart = Math.max(startMin, DAY_START_MIN);
            int visibleEnd = Math.min(endMin, DAY_END_MIN);

            if (visibleEnd <= DAY_START_MIN || visibleStart >= DAY_END_MIN) {
                continue;
            }

            BookingCalendarDTO dto = bookingCalendarMapper.toDto(row);
            dto.setDayKey(s.toLocalDate().toString());

            int topPx = (int) Math.round(((visibleStart - DAY_START_MIN) / 60.0) * HOUR_HEIGHT);
            int heightPx = (int) Math.max(MIN_BLOCK_HEIGHT_PX, Math.round(((visibleEnd - visibleStart) / 60.0) * HOUR_HEIGHT));

            dto.setTopPx(topPx);
            dto.setHeightPx(heightPx);

            boolean isOwner = row.getOwnerId().equals(userId);
            dto.setCanCancel(isOwner || isAdmin);
            if (e.isBefore(now)) {
                dto.setStatus(STATUS_PAST);
            } else if (s.isAfter(now)) {
                dto.setStatus(STATUS_UPCOMING);
            } else {
                dto.setStatus(STATUS_ACTIVE);
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