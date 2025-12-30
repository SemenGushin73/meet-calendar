package com.example.MeetCalendar.service.impl;

import com.example.MeetCalendar.exception.BookingAccessDeniedException;
import com.example.MeetCalendar.exception.BookingNotFoundException;
import com.example.MeetCalendar.service.BookingService;
import com.example.MeetCalendar.web.dto.BookingRequestDTO;
import com.example.MeetCalendar.entity.Booking;
import com.example.MeetCalendar.entity.Room;
import com.example.MeetCalendar.entity.User;
import com.example.MeetCalendar.exception.BookingOverlapException;
import com.example.MeetCalendar.exception.BookingValidationException;
import com.example.MeetCalendar.repository.BookingRepository;
import com.example.MeetCalendar.repository.RoomRepository;
import com.example.MeetCalendar.repository.UserRepository;
import com.example.MeetCalendar.web.model.WeekWindow;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.Comparator;
import java.util.List;

import static java.time.DayOfWeek.MONDAY;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.TemporalAdjusters.previousOrSame;


@Service
@Transactional
public class BookingServiceImpl implements BookingService {
    private final static Duration MIN = Duration.ofMinutes(30);
    private final static Duration MAX = Duration.ofHours(24);
    private static final String OVERLAP_CONSTRAINT = "booking_no_overlap";
    static final ZoneId APP_ZONE = ZoneId.systemDefault();

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;

    public BookingServiceImpl(UserRepository userRepository, BookingRepository bookingRepository,
                              RoomRepository roomRepository) {
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
    }

    public Booking createBooking(String username, BookingRequestDTO request) {
        if (request == null) {
            throw new BookingValidationException();
        }

        User user = userRepository.findByUsername(username).orElseThrow(BookingNotFoundException::new);

        Room room = roomRepository.findById(request.getRoomId()).orElseThrow(BookingNotFoundException::new);

        LocalDateTime startLdt = request.getStartAt();
        LocalDateTime endLdt = request.getEndAt();

        OffsetDateTime start = startLdt.atZone(APP_ZONE).toOffsetDateTime();
        OffsetDateTime end = endLdt.atZone(APP_ZONE).toOffsetDateTime();
        OffsetDateTime now = OffsetDateTime.now();
        validateTime(start, end);
        if (start.isBefore(now)) {
            throw new BookingValidationException();
        }

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setRoom(room);
        booking.setStartAt(start);
        booking.setEndAt(end);
        booking.setTitle(normalizeTitle(request.getTitle()));

        try {
            bookingRepository.save(booking);
        } catch (DataIntegrityViolationException e) {
            if (isOverlap(e)) {
                throw new BookingOverlapException();
            }
            throw e;
        }
        return booking;
    }

    private String normalizeTitle(String title) {

        if (title == null) {
            throw new BookingValidationException();
        }

        String normalizedTitle = title.trim();

        if (normalizedTitle.isBlank()) {
            throw new BookingValidationException();
        }

        if (normalizedTitle.length() > 200) {
            throw new BookingValidationException();
        }

        return normalizedTitle;
    }

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

    private void validateTime(OffsetDateTime start, OffsetDateTime end) {

        if (start == null || end == null) {
            throw new BookingValidationException();
        }
        if (!end.isAfter(start)) {
            throw new BookingValidationException();
        }

        Duration duration = Duration.between(start, end);

        if (duration.compareTo(MIN) < 0 || duration.compareTo(MAX) > 0) {
            throw new BookingValidationException();
        }
    }

    public void cancelBooking(Long bookingId, String username) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(BookingNotFoundException::new);
        User user = userRepository.findByUsername(username).orElseThrow(BookingNotFoundException::new);
        OffsetDateTime now = OffsetDateTime.now();
        if (!canCancel(user, booking)) {
            throw new BookingAccessDeniedException();
        }
        if (booking.getEndAt().isBefore(now)) {
            throw new BookingValidationException();
        }
        bookingRepository.deleteById(bookingId);
    }

    public List<Booking> getRoomBookingsForWeek(Long roomId, OffsetDateTime anyDayWeek) {
        if (anyDayWeek == null) {
            throw new BookingValidationException();
        }
        if (roomId == null) {
            throw new BookingValidationException();
        }
        roomRepository.findById(roomId).orElseThrow(BookingNotFoundException::new);
        WeekWindow w = weekWindow(anyDayWeek);
        List<Booking> bookings = bookingRepository.findByRoomIdAndStartAtLessThanAndEndAtGreaterThan(roomId, w.getEnd(), w.getStart());
        bookings.sort(Comparator.comparing(Booking::getStartAt));
        return bookings;
    }

    public List<Booking> getMyBookingsForWeek(String username, OffsetDateTime anyDayWeek) {
        if (username == null) {
            throw new BookingValidationException();
        }
        if (username.isBlank()) {
            throw new BookingValidationException();
        }
        User user = userRepository.findByUsername(username).orElseThrow(BookingNotFoundException::new);
        WeekWindow w = weekWindow(anyDayWeek);
        List<Booking> bookings = bookingRepository.findByUserIdAndStartAtLessThanAndEndAtGreaterThan(user.getId(), w.getEnd(), w.getStart());
        bookings.sort(Comparator.comparing(Booking::getStartAt));
        return bookings;
    }

    public WeekWindow weekWindow(OffsetDateTime anyDayInWeek) {
        if (anyDayInWeek == null) {
            throw new BookingValidationException();
        }
        OffsetDateTime start = anyDayInWeek.with(previousOrSame(MONDAY)).truncatedTo(DAYS);
        OffsetDateTime end = start.plusWeeks(1);
        return new WeekWindow(start, end);

    }

    private boolean canCancel(User user, Booking booking) {
        boolean isOwner = user.getId().equals(booking.getUser().getId());
        boolean isAdmin = user.getRoles().stream().anyMatch(r -> "ADMIN".equals(r.getName()));
        return isOwner || isAdmin;
    }

}