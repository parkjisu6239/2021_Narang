package com.exp.narang.api.service;

import com.exp.narang.api.request.RoomRegisterPostReq;
import com.exp.narang.db.entity.Room;
import com.exp.narang.db.entity.User;
import com.exp.narang.db.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public List<Room> findAll() {
        return roomRepository.findAll();
    }

    @Override
    public List<Room> findByTitle(String title) {
        return roomRepository.findByTitleContains(title);
    }

    @Override
    public List<Room> findByGame(String game) {
        return roomRepository.findByGameContains(game);
    }
}
