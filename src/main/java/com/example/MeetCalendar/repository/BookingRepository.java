package com.example.MeetCalendar.repository;

import com.example.MeetCalendar.entity.Booking;
import com.example.MeetCalendar.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Query("""
            
                select
                b.id        as bookingId,
                b.title     as title,
                r.name      as roomName,
                b.startAt   as startAt,
                b.endAt     as endAt,
                b.user.id as ownerId
            from Booking b
                join b.room r
            where
                b.user.id = :userId
                and b.startAt < :weekEnd
                and b.endAt > :weekStart
            order by b.startAt asc
            """)
    List<BookingCalendarRow> findCalendarRowsForUserWeek(
            @Param("userId") Long userId,
            @Param("weekStart") OffsetDateTime weekStart,
            @Param("weekEnd") OffsetDateTime weekEnd
    );
}