package com.exp.narang.api.model.service;


import com.exp.narang.api.model.request.RoomRegisterPostReq;
import com.exp.narang.api.model.request.RoomSearchGetReq;
import com.exp.narang.api.model.request.RoomUpdatePatchReq;
import com.exp.narang.api.model.db.entity.Room;
import com.exp.narang.api.model.db.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RoomService {
    Long createRoom(RoomRegisterPostReq roomRegisterPostReq, Long userId);
    void enterRoom(Room room, User user);
    void deleteRoom(Room room, User user);
    void updateRoom(RoomUpdatePatchReq roomUpdatePatchReq, Room room);
    List<User> findUserListByRoomId(Long roomId);
    Page<Room> findBySearch(RoomSearchGetReq roomSearchGetReq, Pageable pageable);
    Room findById(Long roomId);
}
