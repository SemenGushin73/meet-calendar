package com.example.MeetCalendar.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Booking create request DTO (MVC form / REST payload).
 */
@Getter
@Setter
@Schema(description = "Service-level booking request DTO")
public class BookingRequestDTO {
    @Schema(
            description = "Room ID",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long roomId;
    @Schema(
            description = "Booking request title",
            example = "RnD Daily",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String title;
    @Schema(
            description = "Start date/time of booking (ISO-8601)",
            example = "2026-01-13T10:30:00",
            format = "date-time",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private LocalDateTime startAt;
    @Schema(
            description = "End date/time of booking (ISO-8601), must be after startAt",
            example = "2026-01-13T11:30:00",
            format = "date-time",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private LocalDateTime endAt;
}