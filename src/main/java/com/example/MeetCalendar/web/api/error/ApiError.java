package com.example.MeetCalendar.web.api.error;


import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;

/**
 * Standard error payload returned by REST API endpoints.
 */
@Schema(description = "Standard error payload for REST API")
public record ApiError(
        @Schema(
                description = "Time when the error occurred (server time)",
                example = "2026-01-13T12:00:00+04:00",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        OffsetDateTime timestamp,
        @Schema(
                description = "HTTP status code",
                example = "400",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        int status,
        @Schema(
                description = "Machine-readable error code",
                example = "BOOKING_VALIDATION",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String error,
        @Schema(
                description = "Human-readable error message",
                example = "Incorrect input data",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String message,
        @Schema(
                description = "Request path",
                example = "/api/v1/bookings",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String path
) {
}