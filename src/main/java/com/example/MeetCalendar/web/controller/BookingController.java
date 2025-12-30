package com.example.MeetCalendar.web.controller;

import com.example.MeetCalendar.service.BookingService;
import com.example.MeetCalendar.web.contract.BookingControllerContract;
import com.example.MeetCalendar.web.dto.BookingRequestDTO;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/bookings")
public class BookingController implements BookingControllerContract {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public String addBooking(@ModelAttribute BookingRequestDTO request, @AuthenticationPrincipal UserDetails user) {
        String username = user.getUsername();
        bookingService.createBooking(username, request);
        return "redirect:/calendar?ok=created";
    }

    @PostMapping("/{id}/cancel")
    public String cancelBooking(@PathVariable("id") Long id, @AuthenticationPrincipal UserDetails user) {
        String username = user.getUsername();
        bookingService.cancelBooking(id, username);
        return "redirect:/calendar?ok=cancelled";
    }
}