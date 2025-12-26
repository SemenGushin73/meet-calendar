package com.example.MeetCalendar.web;

import com.example.MeetCalendar.entity.Booking;
import com.example.MeetCalendar.service.BookingService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.OffsetDateTime;
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
    public String calendar(Model model, @AuthenticationPrincipal UserDetails user) {
        String username = user.getUsername();
        OffsetDateTime anyDayWeek = OffsetDateTime.now();
        List<Booking> bookings = bookingService.getMyBookingsForWeek(username, anyDayWeek);
        model.addAttribute("bookings", bookings);
        model.addAttribute("anyDayWeek", anyDayWeek);
        return "calendar";
    }
}