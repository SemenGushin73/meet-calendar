package com.example.MeetCalendar.web.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Request payload for creating a meeting room booking.
 */
@Getter
@Setter
@Schema(description = "Request payload for creating a meeting room booking")
public class BookingCreateRequest {
    @NotNull
    @Schema(
            description = "Meeting room ID",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long roomId;
    @NotBlank
    @Size(max = 200)
    @Schema(
            description = "Booking title",
            example = "RnD Daily",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String title;
    @NotNull
    @Schema(
            description = "Start date/time of booking (ISO-8601)",
            example = "2026-01-13T10:30:00",
            format = "date-time",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private LocalDateTime startAt;
    @NotNull
    @Schema(
            description = "End date/time of booking (ISO-8601), must be after startAt",
            example = "2026-01-13T11:30:00",
            format = "date-time",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private LocalDateTime endAt;
}