package com.example.MeetCalendar.web.api.v1.bookings;

import com.example.MeetCalendar.web.api.dto.BookingCreateRequest;
import com.example.MeetCalendar.web.api.dto.BookingResponse;
import com.example.MeetCalendar.web.api.error.ApiError;
import com.example.MeetCalendar.web.api.v1.contract.BookingRestControllerContract;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;


@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
@Tag(name = "Bookings", description = "Meeting room booking operations")
public class BookingRestController implements BookingRestControllerContract {

    private final BookingApiHandler bookingApiHandler;

    @PostMapping
    @Operation(
            summary = "Create a booking",
            description = "Creates a meeting room reservation for a specific time slot. Authentication (session cookie) is required. Overlaps are prohibited.",
            security = @SecurityRequirement(name = "session")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description ="Booking created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookingResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Incorrect input data",
                    content = @Content(mediaType = "application/json",  schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(name = "BookingValidation",
                                    value = "{\"timestamp\":\"2026-01-13T12:00:00+04:00\",\"status\":400,\"error\":\"BOOKING_VALIDATION\",\"message\":\"Incorrect input data\",\"path\":\"/api/v1/bookings\"}"))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Not authenticated",
                    content = @Content(mediaType = "application/json",   schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Room or user not found",
                    content = @Content(mediaType = "application/json",   schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Booking conflict (overlap)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(name = "BookingOverlap",
                                    value = "{\"timestamp\":\"2026-01-13T12:00:00+04:00\",\"status\":409,\"error\": \"BOOKING_OVERLAP\",\"message\":\"Room is already booked for this time\",\"path\":\"/api/v1/bookings\"}"))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(mediaType = "application/json",   schema = @Schema(implementation = ApiError.class))
            )
    }
    )
    public BookingResponse create(@Valid @RequestBody BookingCreateRequest request, Principal principal) {
        return bookingApiHandler.create(principal.getName(),  request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Cancel booking",
            description = "Cancels a reservation by ID. Only the reservation owner or administrator can do this.",
            security = @SecurityRequirement(name = "session")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Booking canceled"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Not authenticated. Body may be empty."
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Insufficient rights",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Booking not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
            )
    })
    public void cancel(@PathVariable Long id,  Principal principal) {
        bookingApiHandler.cancel(id,principal.getName());
    }

}