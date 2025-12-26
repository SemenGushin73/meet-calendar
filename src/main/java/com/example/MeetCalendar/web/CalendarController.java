package com.example.MeetCalendar.web;

import com.example.MeetCalendar.dto.BookingCalendarDTO;
import com.example.MeetCalendar.service.BookingService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

/**
 * Controller for displaying the main calendar page.
 */
@Controller
public class CalendarController {
    private final BookingService bookingService;

    public CalendarController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/calendar")
    public String calendar(Model model, @AuthenticationPrincipal UserDetails user, @RequestParam(required = false) String week) {
        OffsetDateTime anyDayWeek;
        if (week == null || week.isBlank()) {
            anyDayWeek = OffsetDateTime.now();
        } else {
            LocalDate day = LocalDate.parse(week);
            anyDayWeek = day.atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime();
        }
        OffsetDateTime prevWeek = anyDayWeek.minusDays(7);
        OffsetDateTime nextWeek = anyDayWeek.plusDays(7);
        String username = user.getUsername();
        List<BookingCalendarDTO> bookings = bookingService.getBookingCalendarForUser(username, anyDayWeek);
        model.addAttribute("bookings", bookings);
        model.addAttribute("anyDayWeek", anyDayWeek);
        model.addAttribute("prevWeek", prevWeek);
        model.addAttribute("nextWeek", nextWeek);
        return "calendar";
    }
}