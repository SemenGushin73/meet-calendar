package com.example.MeetCalendar.web;

import com.example.MeetCalendar.dto.BookingRequestDTO;
import com.example.MeetCalendar.service.BookingService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Controller for displaying the booking page.
 */
@Controller
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/bookings")
    public String addBooking(@ModelAttribute BookingRequestDTO request, @AuthenticationPrincipal UserDetails user) {
        String username = user.getUsername();

        bookingService.createBooking(username, request);
        return "redirect:/calendar?ok=created";
    }

    @PostMapping("/bookings/{id}/cancel")
    public String cancelBooking(@PathVariable("id") Long id, @AuthenticationPrincipal UserDetails user) {
        String username = user.getUsername();
        bookingService.cancelBooking(id, username);
        return "redirect:/calendar?ok=cancelled";
    }
}