package com.exp.narang.api.service;

import com.exp.narang.api.request.RoomRegisterPostReq;
import com.exp.narang.db.entity.Room;
import com.exp.narang.db.entity.User;
import com.exp.narang.db.repository.RoomRepository;
import com.exp.narang.db.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RoomServiceImpl implements RoomService {
    @Autowired
    RoomRepository roomRepository;
    UserRepository userRepository;


    @Override
    public Long createRoom(Room room) {
        roomRepository.save(room);
        return roomRepository.findByOwnerId(room.getOwnerId()).getRoomId();
    }

    @Override
    public void enterRoom(Room room, Long userId) {
        userRepository.findById(userId).get().setRoom(room);
    }

    @Override
    public void deleteRoom(Room room, Long userId) {
        userRepository.findById(userId).get().setRoom(null); // 방 나가면 유저 테이블에서도 방 정보 삭제
        if(userId == room.getOwnerId()){
            List<User> userList = roomRepository.findByRoomId(room.getRoomId());
            for(User user : userList){
                userRepository.findById(user.getUserId()).get().setRoom(null); // 방 나가면 유저 테이블에서도 방 정보 삭제
            }
            roomRepository.deleteById(room.getRoomId());
        }
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

    @Override
    public Room findById(Long roomId) {
        Optional<Room> roomOpt = roomRepository.findById(roomId);
        return roomOpt.orElse(null);
    }
}
