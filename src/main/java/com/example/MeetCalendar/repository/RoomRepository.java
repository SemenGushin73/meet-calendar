package com.example.MeetCalendar.repository;

import com.example.MeetCalendar.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {
    String findNameByName(String name);
}