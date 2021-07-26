package com.exp.narang.api.service;


import com.exp.narang.api.request.RoomRegisterPostReq;
import com.exp.narang.db.entity.Room;
import com.exp.narang.db.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RoomService {
    Long createRoom(RoomRegisterPostReq roomRegisterPostReq, Long userId);
    void enterRoom(Room room, User user);
    void deleteRoom(Room room, User user);
    List<Room> findAll();
    Page<Room> findAll(Pageable pageable);
    List<Room> findByTitle(String title);
    List<Room> findByGame(String game);
    Room findById(Long roomId);
}
