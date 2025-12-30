package com.example.MeetCalendar.exception;

/**
 * An exception that indicates the insufficient access rights.
 */
public class BookingAccessDeniedException extends RuntimeException {
    public BookingAccessDeniedException() {
        super("Insufficient access rights");
    }
}