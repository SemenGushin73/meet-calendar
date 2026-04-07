package com.example.MeetCalendar.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

/**
 * Calendar booking DTO used for rendering calendar UI.
 */
@Getter
@Setter
@Schema(description = "Calendar booking DTO used for rendering week calendar UI")
public class BookingCalendarDTO {
    @Schema(
            description = "Booking ID",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long bookingId;
    @Schema(
            description = "Booking title",
            example = "RnD Daily",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String title;
    @Schema(
            description = "Room name",
            example = "Юг Подвал",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String roomName;
    @Schema(
            description = "Time of start booking",
            example = "2026-01-20T00:00:00+04:00",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private OffsetDateTime startAt;
    @Schema(
            description = "Time of end booking",
            example = "2026-01-20T01:00:00+04:00",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private OffsetDateTime endAt;
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
    @Schema(
            description = "Top position of booking block in pixels (relative to day column)",
            example = "120",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Integer topPx;
    @Schema(
            description = "Height of booking block in pixels",
            example = "60",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Integer heightPx;
    @Schema(
            description = "Day identifier used in calendar layout (yyyy-MM-dd)",
            example = "2026-01-20",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String dayKey;
}