package com.exp.narang.api.service;

import com.exp.narang.api.request.RoomRegisterPostReq;
import com.exp.narang.db.entity.Room;
import com.exp.narang.db.entity.User;
import com.exp.narang.db.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoomServiceImpl implements RoomService {
    @Autowired
    RoomRepository roomRepository;


    @Override
    public Long createRoom(RoomRegisterPostReq roomRegisterPostReq) {
        roomRepository.save(
                Room.builder()
                    .title((roomRegisterPostReq.getTitle()))
                    .maxPlayer(roomRegisterPostReq.getMaxPlayer())
                    .ownerId(roomRegisterPostReq.getOwnerId()).build());


        return roomRepository.findByOwnerId(roomRegisterPostReq.getOwnerId()).getRoomId();
    }

    @Override
    public void deleteRoom(User user) {

    }
}
