package com.example.MeetCalendar.web.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Response payload representing a meeting room booking.
 */
@Getter
@Setter
@Schema(description = "Response payload for creating a meeting room booking")
public class BookingResponse {
    @Schema(
            description = "Booking ID",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long id;
    @Schema(
            description = "Room name",
            example = "Север Подвал",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String roomName;
    @Schema(
            description = "Booking title",
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
    @Schema(
            description = "Indicates whether the current user can cancel this booking (owner or administrator)",
            example = "true",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private boolean canCancel;
    @Schema(
            description = "Booking status",
            allowableValues = {"PAST", "ACTIVE", "UPCOMING"},
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String status;
}
