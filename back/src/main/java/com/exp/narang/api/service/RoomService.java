package com.exp.narang.api.service;


import com.exp.narang.api.request.RoomRegisterPostReq;
import com.exp.narang.db.entity.Room;
import com.exp.narang.db.entity.User;

import java.util.List;

public interface RoomService {
    Long createRoom(RoomRegisterPostReq roomRegisterPostReq);
    void deleteRoom(User user);
    List<Room> findAll();
    List<Room> findByTitle(String title);
    List<Room> findByGame(String game);
}
