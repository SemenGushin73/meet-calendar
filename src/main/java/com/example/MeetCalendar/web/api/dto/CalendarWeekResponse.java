package com.example.MeetCalendar.web.api.dto;

import com.example.MeetCalendar.web.dto.BookingCalendarDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Response payload representing calendar data for a single week.
 */
@Getter
@Setter
@Schema(description = "Response payload for calendar week")
public class CalendarWeekResponse {
    @Schema(
            description = "Date of start week",
            example = "2026-01-13T00:00:00+04:00",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private OffsetDateTime weekStart;
    @Schema(
            description = "Date of end week",
            example = "2026-01-20T00:00:00+04:00",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private OffsetDateTime weekEnd;
    @Schema(
            description = "Previous week key (yyyy-MM-dd)",
            example = "2026-01-05",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String previousWeek;
    @Schema(
            description = "Next week key (yyyy-MM-dd)",
            example = "2026-01-19",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String nextWeek;
    @Schema(
            description = "Bookings for the week (calendar layout DTOs)",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<BookingCalendarDTO> bookings;
}