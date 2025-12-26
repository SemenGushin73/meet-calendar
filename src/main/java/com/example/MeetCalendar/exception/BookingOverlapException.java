package com.example.MeetCalendar.exception;

/**
 * An exception that indicates the room is booked.
 */
public class BookingOverlapException extends RuntimeException {
    public BookingOverlapException(String message) {
        super(message);
    }
}