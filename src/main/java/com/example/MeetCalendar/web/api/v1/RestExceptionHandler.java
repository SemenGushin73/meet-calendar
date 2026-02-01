package com.example.MeetCalendar.web.api.v1;

import com.example.MeetCalendar.exception.BookingAccessDeniedException;
import com.example.MeetCalendar.exception.BookingNotFoundException;
import com.example.MeetCalendar.exception.BookingOverlapException;
import com.example.MeetCalendar.exception.BookingValidationException;
import com.example.MeetCalendar.web.api.error.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;


/**
 * Global REST exception handler.
 * <p>
 * Converts domain and validation exceptions into standardized API error responses.
 */
@RestControllerAdvice
public class RestExceptionHandler {
    /**
     * Builds a standard API error response.
     */
    private ResponseEntity<ApiError> buildError(int status, String code, String message, HttpServletRequest request) {
        OffsetDateTime timestamp = OffsetDateTime.now();
        String path = request.getRequestURI();
        ApiError apiError = new ApiError(timestamp, status, code, message, path);
        return ResponseEntity.status(status).body(apiError);
    }
    /**
     * Handles access denied errors.
     */
    @ExceptionHandler(BookingAccessDeniedException.class)
    public ResponseEntity<ApiError> denied(BookingAccessDeniedException e, HttpServletRequest request) {
        return buildError(403, "BOOKING_ACCESS_DENIED", e.getMessage(), request);
    }

    /**
     * Handles not found errors.
     */
    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<ApiError> notFound(BookingNotFoundException e, HttpServletRequest request) {
        return buildError(404, "BOOKING_NOT_FOUND", e.getMessage(), request);
    }

    /**
     * Handles booking overlap conflicts.
     */
    @ExceptionHandler(BookingOverlapException.class)
    public ResponseEntity<ApiError> conflict(BookingOverlapException e, HttpServletRequest request) {
        return buildError(409, "BOOKING_OVERLAP", e.getMessage(), request);
    }

    /**
     * Handles validation errors.
     */
    @ExceptionHandler(BookingValidationException.class)
    public ResponseEntity<ApiError> validation(BookingValidationException e, HttpServletRequest request) {
        return buildError(400, "BOOKING_VALIDATION", e.getMessage(), request);
    }

    /**
     * Handles unexpected internal errors.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> other(Exception e, HttpServletRequest request) {
        return buildError(500, "INTERNAL_ERROR", "Internal error", request);
    }

}