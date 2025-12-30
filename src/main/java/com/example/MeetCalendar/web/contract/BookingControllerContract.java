package com.example.MeetCalendar.web.contract;

import com.example.MeetCalendar.web.dto.BookingRequestDTO;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * MVC contract for booking actions.
 * <p>
 * Declares endpoints for creating and cancelling bookings.
 */
public interface BookingControllerContract {
    /**
     * Creates a booking and redirects back to calendar page.
     *
     * @param request booking request
     * @param user    authenticated user
     * @return redirect view name
     */
    String addBooking(BookingRequestDTO request, UserDetails user);

    /**
     * Cancels booking by id and redirects back to calendar page.
     *
     * @param id   booking id
     * @param user authenticated user
     * @return redirect view name
     */
    String cancelBooking(Long id, UserDetails user);
}