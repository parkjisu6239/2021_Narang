package com.exp.narang.api.service;


import com.exp.narang.api.request.RoomRegisterPostReq;
import com.exp.narang.api.request.RoomSearchGetReq;
import com.exp.narang.api.request.RoomUpdatePatchReq;
import com.exp.narang.db.entity.Room;
import com.exp.narang.db.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoomService {
    Long createRoom(RoomRegisterPostReq roomRegisterPostReq, Long userId);
    void enterRoom(Room room, User user);
    void deleteRoom(Room room, User user);
    void updateRoom(RoomUpdatePatchReq roomUpdatePatchReq, Room room);
    Page<Room> findBySearch(RoomSearchGetReq roomSearchGetReq, Pageable pageable);
    Room findById(Long roomId);
}
