package com.example.MeetCalendar.service;

import com.example.MeetCalendar.entity.Booking;
import com.example.MeetCalendar.entity.Room;
import com.example.MeetCalendar.entity.User;
import com.example.MeetCalendar.exception.BookingValidationException;
import com.example.MeetCalendar.repository.BookingRepository;
import com.example.MeetCalendar.repository.RoomRepository;
import com.example.MeetCalendar.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.OffsetDateTime;

@Service
@Transactional
public class BookingService {
    private final static Duration MIN = Duration.ofMinutes(30);
    private final static Duration MAX = Duration.ofHours(24);
    private static final String OVERLAP_CONSTRAINT = "booking_no_overlap";

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;

    public BookingService(UserRepository userRepository, BookingRepository bookingRepository,
                          RoomRepository roomRepository) {
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
    }

    public Booking createBooking(String username, CreateBookingRequest request) {

        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new BookingValidationException("Username not found" + username));

        Room room = roomRepository.findById(request.getRoomId()).orElseThrow(() ->
                new BookingValidationException("Room not found" + request.getRoomId()));

        OffsetDateTime start = request.getStartAt();
        OffsetDateTime end = request.getEndAt();
        validateTime(start, end);

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setRoom(room);
        booking.setStartAt(start);
        booking.setEndAt(end);
        booking.setTitle(normalizeTitle(request.getTitle()));

        try {
            return bookingRepository.save(booking);
        } catch (DataIntegrityViolationException e) {
            if (isOverlap(e)) {
                throw new BookingValidationException("Room is already booked for this time");
            }
            throw e;
        }
    }

    private String normalizeTitle(String title) {

        if (title == null) {
            throw new BookingValidationException("Title is null");
        }

        String normalizedTitle = title.trim();

        if (normalizedTitle.isBlank()) {
            throw new BookingValidationException("Title is blank");
        }

        if (normalizedTitle.length() > 200) {
            throw new BookingValidationException("Title is too long");
        }

        return normalizedTitle;
    }

    private boolean isOverlap(DataIntegrityViolationException e) {
        Throwable cur = e;
        while (cur != null) {
            String msg = cur.getMessage();
            if (msg != null && msg.contains(OVERLAP_CONSTRAINT)) {
                return true;
            }
            cur = cur.getCause();
        }
        return false;
    }

    private void validateTime(OffsetDateTime start, OffsetDateTime end) {

        if (start ==  null || end == null) {
            throw new  BookingValidationException("Start and end time cannot be null");
        }
        if (!end.isAfter(start)) {
            throw new  BookingValidationException("End time must be after start time");
        }

        Duration duration = Duration.between(start, end);

        if (duration.compareTo(MIN) < 0 || duration.compareTo(MAX) > 0) {
            throw new BookingValidationException("Booking time cannot be less than 30 minutes and cannot be greater than 24 hours");
        }
    }

}