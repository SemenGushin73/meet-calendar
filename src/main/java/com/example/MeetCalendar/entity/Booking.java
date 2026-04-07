package com.example.MeetCalendar.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

/**
 * Entity representing a meeting room booking.
 */
@Entity
@Table(name = "bookings")
@Getter
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    @Setter
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Setter
    private User user;

    @Column(name = "title", nullable = false)
    @Setter
    private String title;

    @Column(name = "start_at", nullable = false)
    @Setter
    private OffsetDateTime startAt;

    @Column(name = "end_at", nullable = false)
    @Setter
    private OffsetDateTime endAt;

}