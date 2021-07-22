package com.exp.narang.api.service;


import com.exp.narang.db.entity.Room;

public interface RoomService {
    void addRoom(Room room);
    void deleteRoom(Long userId);
}
