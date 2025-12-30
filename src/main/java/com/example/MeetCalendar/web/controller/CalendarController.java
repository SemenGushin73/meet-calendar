package com.example.MeetCalendar.web.controller;

import com.example.MeetCalendar.service.CalendarService;
import com.example.MeetCalendar.web.contract.CalendarControllerContract;
import com.example.MeetCalendar.web.model.CalendarPageModel;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/calendar")
public class CalendarController implements CalendarControllerContract {
    private final CalendarService calendarService;

    public CalendarController(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    @GetMapping
    public String calendar(@RequestParam(required = false) String week, @AuthenticationPrincipal UserDetails principal, Model model) {
        CalendarPageModel page = calendarService.buildCalendarPage(principal.getUsername(), week);
        model.addAttribute("page", page);
        return "calendar";
    }
}