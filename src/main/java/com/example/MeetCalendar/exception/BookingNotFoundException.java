package com.example.MeetCalendar.exception;

/**
 * An exception indicating that the requested data was not found.
 */
public class BookingNotFoundException extends RuntimeException {
    public BookingNotFoundException() {
        super("The requested data was not found");
    }
}