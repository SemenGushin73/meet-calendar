package com.example.MeetCalendar.repository;

import com.example.MeetCalendar.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Interface for interaction with rooms.
 */
public interface RoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findNameByName(String name);
}