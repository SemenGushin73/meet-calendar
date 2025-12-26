package com.example.MeetCalendar.repository;

import com.example.MeetCalendar.entity.Booking;
import com.example.MeetCalendar.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Interface for interaction with booking.
 */
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUser(User user);

    List<Booking> findByRoomIdAndStartAtLessThanAndEndAtGreaterThan(Long roomId, OffsetDateTime weekEnd, OffsetDateTime weekStart);

    List<Booking> findByStartAtLessThanAndEndAtGreaterThan(OffsetDateTime weekEnd, OffsetDateTime weekStart);

    List<Booking> findByUserIdAndStartAtLessThanAndEndAtGreaterThan(Long userId, OffsetDateTime weekEnd, OffsetDateTime weekStart);
}