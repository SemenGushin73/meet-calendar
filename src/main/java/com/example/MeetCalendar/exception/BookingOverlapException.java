package com.example.MeetCalendar.exception;

public class BookingOverlapException extends RuntimeException {
    public BookingOverlapException(String message) {
        super(message);
    }
}