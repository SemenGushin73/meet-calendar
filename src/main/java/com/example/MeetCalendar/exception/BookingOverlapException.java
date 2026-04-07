package com.example.MeetCalendar.exception;

/**
 * An exception that indicates the room is booked.
 */
public class BookingOverlapException extends RuntimeException {
    public BookingOverlapException() {
        super("Room is already booked for this time");
    }
}