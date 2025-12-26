package com.example.MeetCalendar.service;

import com.example.MeetCalendar.dto.BookingCalendarDTO;
import com.example.MeetCalendar.dto.BookingRequestDTO;
import com.example.MeetCalendar.entity.Booking;
import com.example.MeetCalendar.entity.Room;
import com.example.MeetCalendar.entity.User;
import com.example.MeetCalendar.exception.BookingOverlapException;
import com.example.MeetCalendar.exception.BookingValidationException;
import com.example.MeetCalendar.repository.BookingCalendarRow;
import com.example.MeetCalendar.repository.BookingRepository;
import com.example.MeetCalendar.repository.RoomRepository;
import com.example.MeetCalendar.repository.UserRepository;
import com.example.MeetCalendar.utils.WeekWindow;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.time.DayOfWeek.MONDAY;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.TemporalAdjusters.previousOrSame;

/**
 * Service class for booking.
 */
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

    /**
     * Method for creating a reservation.
     */
    public Booking createBooking(String username, BookingRequestDTO request) {

        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new BookingValidationException("Username not found: " + username));

        Room room = roomRepository.findById(request.getRoomId()).orElseThrow(() ->
                new BookingValidationException("Room not found: " + request.getRoomId()));

        LocalDateTime startLdt = request.getStartAt();
        LocalDateTime endLdt = request.getEndAt();

        ZoneId zone = ZoneId.systemDefault(); // или ZoneOffset.UTC
        OffsetDateTime start = startLdt.atZone(zone).toOffsetDateTime();
        OffsetDateTime end = endLdt.atZone(zone).toOffsetDateTime();
        if (start.isBefore(OffsetDateTime.now())) {
            throw new BookingOverlapException("Start date is before end date");
        }
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
                throw new BookingOverlapException("Room is already booked for this time");
            }
            throw e;
        }
    }

    /**
     * Method for filtering titles.
     */
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

    /**
     * Method for indicating whether a room is booked or not.
     */
    private boolean isOverlap(Throwable e) {
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

    /**
     * Method for filtering time.
     */
    private void validateTime(OffsetDateTime start, OffsetDateTime end) {

        if (start == null || end == null) {
            throw new BookingValidationException("Start and end time cannot be null");
        }
        if (!end.isAfter(start)) {
            throw new BookingValidationException("End time must be after start time");
        }

        Duration duration = Duration.between(start, end);

        if (duration.compareTo(MIN) < 0 || duration.compareTo(MAX) > 0) {
            throw new BookingValidationException("Booking time cannot be less than 30 minutes and cannot be greater than 24 hours");
        }
    }

    /**
     * Method for indicating the ability to delete a reservation.
     */
    private boolean canCancel(User user, Booking booking) {
        boolean isOwner = user.getId().equals(booking.getUser().getId());
        boolean isAdmin = user.getRoles().stream().anyMatch(r -> "ADMIN".equals(r.getName()));
        return isOwner || isAdmin;
    }

    /**
     * Method to delete a booking.
     */
    public void cancelBooking(Long bookingId, String username) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new BookingValidationException("Booking not found: " + bookingId));
        User user = userRepository.findByUsername(username).orElseThrow(() -> new BookingValidationException("User not found: " + username));
        if (!canCancel(user, booking)) {
            throw new BookingValidationException("Access denied");
        }
        if (booking.getEndAt().isBefore(OffsetDateTime.now())) {
            throw new BookingValidationException("Can not cancel booking after end time");
        }
        bookingRepository.deleteById(bookingId);
    }

    /**
     * Method for calculating the current week.
     */
    private WeekWindow weekWindow(OffsetDateTime anyDayInWeek) {
        if (anyDayInWeek == null) {
            throw new BookingValidationException("anyDayInWeek cannot be null");
        }
        OffsetDateTime start = anyDayInWeek.with(previousOrSame(MONDAY)).truncatedTo(DAYS);
        OffsetDateTime end = start.plusDays(7);
        return new WeekWindow(start, end);

    }

    /**
     * Method for displaying bookings for the current week.
     */
    public List<Booking> getRoomBookingsForWeek(Long roomId, OffsetDateTime anyDayWeek) {
        if (anyDayWeek == null) {
            throw new BookingValidationException("anyDayWeek cannot be null");
        }
        if (roomId == null) {
            throw new BookingValidationException("roomId cannot be null");
        }
        roomRepository.findById(roomId).orElseThrow(() -> new BookingValidationException("Room not found: " + roomId));
        WeekWindow w = weekWindow(anyDayWeek);
        List<Booking> bookings = bookingRepository.findByRoomIdAndStartAtLessThanAndEndAtGreaterThan(roomId, w.getEnd(), w.getStart());
        bookings.sort(Comparator.comparing(Booking::getStartAt));
        return bookings;
    }

    public List<Booking> getMyBookingsForWeek(String username, OffsetDateTime anyDayWeek) {
        if (username == null) {
            throw new BookingValidationException("username cannot be null");
        }
        if (username.isBlank()) {
            throw new BookingValidationException("username cannot be blank");
        }
        User user = userRepository.findByUsername(username).orElseThrow(() -> new BookingValidationException("User not found: " + username));
        WeekWindow w = weekWindow(anyDayWeek);
        List<Booking> bookings = bookingRepository.findByUserIdAndStartAtLessThanAndEndAtGreaterThan(user.getId(), w.getEnd(), w.getStart());
        bookings.sort(Comparator.comparing(Booking::getStartAt));
        return bookings;
    }

    public List<BookingCalendarDTO> getBookingCalendarForUser(String username, OffsetDateTime anyDayWeek) {
        if (anyDayWeek == null) {
            throw new BookingValidationException("anyDayWeek cannot be null");
        }
        if (username == null) {
            throw new BookingValidationException("username cannot be null");
        }
        if (username.isBlank()) {
            throw new BookingValidationException("username cannot be blank");
        }
        User user = userRepository.findByUsername(username).orElseThrow(() -> new BookingValidationException("User not found: " + username));
        Long userId = user.getId();
        WeekWindow w = weekWindow(anyDayWeek);
        OffsetDateTime weekStart = w.getStart();
        OffsetDateTime weekEnd = w.getEnd();
        List<BookingCalendarRow> rows = bookingRepository.findCalendarRowsForUserWeek(userId, weekStart, weekEnd);
        List<BookingCalendarDTO> dtos = new ArrayList<>();
        OffsetDateTime now = OffsetDateTime.now();
        boolean isAdmin = user.getRoles().stream().anyMatch(r -> "ADMIN".equals(r.getName()));
        for (BookingCalendarRow row : rows) {
            BookingCalendarDTO dto = new BookingCalendarDTO();
            dto.setBookingId(row.getBookingId());
            dto.setTitle(row.getTitle());
            dto.setRoomName(row.getRoomName());
            dto.setStartAt(row.getStartAt());
            dto.setEndAt(row.getEndAt());
            boolean isOwner = row.getOwnerId().equals(userId);
            dto.setCanCancel(isOwner || isAdmin);
            if (row.getEndAt().isBefore(now)) {
                dto.setStatus("PAST");
            } else if (row.getStartAt().isAfter(now)) {
                dto.setStatus("UPCOMING");
            } else {
                dto.setStatus("ACTIVE");
            }
            dtos.add(dto);
        }
        return dtos;
    }
}