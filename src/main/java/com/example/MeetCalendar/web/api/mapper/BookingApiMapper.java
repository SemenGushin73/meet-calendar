package com.example.MeetCalendar.web.api.mapper;

import com.example.MeetCalendar.entity.Booking;
import com.example.MeetCalendar.web.api.dto.BookingCreateRequest;
import com.example.MeetCalendar.web.api.dto.BookingResponse;
import com.example.MeetCalendar.web.dto.BookingRequestDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

/**
 * Maps REST API DTOs to service DTOs and domain entities to API responses.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface BookingApiMapper {

    BookingRequestDTO toServiceDto(BookingCreateRequest request);

    @Mapping(source = "room.name", target = "roomName")
    @Mapping(source = "startAt", target = "startAt")
    @Mapping(source = "endAt", target = "endAt")
    @Mapping(target = "canCancel", ignore = true)
    @Mapping(target = "status", ignore = true)
    BookingResponse toResponse(Booking booking);

    /**
     * Converts OffsetDateTime to LocalDateTime (drops offset).
     */
    default LocalDateTime map(OffsetDateTime value) {
        return value == null ? null : value.toLocalDateTime();
    }
}