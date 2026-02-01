package com.example.MeetCalendar.web.api.v1.calendar;

import com.example.MeetCalendar.web.api.dto.CalendarWeekResponse;
import com.example.MeetCalendar.web.api.error.ApiError;
import com.example.MeetCalendar.web.api.v1.contract.CalendarRestControllerContract;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;


@RestController
@RequestMapping(("/api/v1/calendar"))
@RequiredArgsConstructor
@Tag(name = "Calendar", description = "Displaying calendar")
public class CalendarRestController implements CalendarRestControllerContract {
    private final CalendarApiHandler calendarApiHandler;

    @Operation(
            summary = "Get calendar week",
            description = "Returns calendar data for the week that contains the given date (yyyy-MM-dd). If omitted, uses current week",
            security = @SecurityRequirement(name = "session")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Week calendar data",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CalendarWeekResponse.class),
                            examples = @ExampleObject(name = "WeekEmpty", value = "{\"weekStart\":\"2026-01-12T00:00:00+04:00\",\"weekEnd\":\"2026-01-19T00:00:00+04:00\",\"previousWeek\":\"2026-01-05\",\"nextWeek\":\"2026-01-19\",\"bookings\":[]}"))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid week format (expected yyyy-MM-dd)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(name = "InvalidWeekFormat", value = "{\"timestamp\":\"2026-01-13T12:00:00+04:00\",\"status\":400,\"error\":\"BOOKING_VALIDATION\",\"message\":\"Incorrect input data\",\"path\":\"/api/v1/calendar/week\"}"))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Not authenticated"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(name = "InternalError", value = "{\"timestamp\":\"2026-01-13T12:00:00+04:00\",\"status\":500,\"error\":\"INTERNAL_ERROR\",\"message\":\"Internal error\",\"path\":\"/api/v1/calendar/week\"}"))
            )
    })

    @GetMapping("/week")
    public CalendarWeekResponse week(@Parameter(
            description = "Week selector in format yyyy-MM-dd (Monday-based week). If absent, current week is used.",
            example = "2026-01-12",
            required = false
    ) @RequestParam(required = false) String week, Principal principal) {
        return calendarApiHandler.week(principal.getName(), week);
    }
}