package com.example.MeetCalendar.web.contract;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;

/**
 * MVC contract for calendar page.
 */
public interface CalendarControllerContract {
    /**
     * Renders calendar page for a selected week.
     *
     * @param week      optional week selector (format {@code yyyy-MM-dd})
     * @param principal authenticated user
     * @param model     UI model
     * @return template name
     */
    String calendar(String week, UserDetails principal, Model model);
}