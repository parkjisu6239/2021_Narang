package com.exp.narang.api.service;


import com.exp.narang.api.request.RoomRegisterPostReq;
import com.exp.narang.db.entity.User;

public interface RoomService {
    Long createRoom(RoomRegisterPostReq roomRegisterPostReq);
    void deleteRoom(User user);
}
