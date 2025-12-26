package com.example.MeetCalendar.exception;

/**
 * An exception that indicates that the data is not valid.
 */
public class BookingValidationException extends RuntimeException {
    public BookingValidationException(String message) {
        super(message);
    }
}