package com.exp.narang.api.service;

import com.exp.narang.api.request.RoomRegisterPostReq;
import com.exp.narang.db.entity.Room;
import com.exp.narang.db.entity.User;
import com.exp.narang.db.repository.RoomRepository;
import com.exp.narang.db.repository.RoomRepositorySupport;
import com.exp.narang.db.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.exp.narang.db.entity.QRoom.room;

@Service
public class RoomServiceImpl implements RoomService {
    @Autowired
    RoomRepository roomRepository;
    @Autowired
    RoomRepositorySupport roomRepositorySupport;
    @Autowired
    UserRepository userRepository;


    @Override
    public Long createRoom(RoomRegisterPostReq roomRegisterPostReq, Long userId) {
        roomRepository.save(
                Room.builder()
                        .title(roomRegisterPostReq.getTitle())
                        .ownerId(userId)
                        .maxPlayer(roomRegisterPostReq.getMaxPlayer())
                        .password(roomRegisterPostReq.getPassword())
                        .isActive(false) // IsActive(방 활동 중)의 default값이 false이므로
                        .build()
        );
        return roomRepository.findByOwnerId(userId).getRoomId();
    }

    @Override
    public void enterRoom(Room room, User user) {
        user.setRoom(room);
        userRepository.save(user);
    }

    @Override
    public void deleteRoom(Room room, User user) {
        user.setRoom(null);
        userRepository.save(user);
//        userRepository.findById(userId).get().setRoom(null); // 방 나가면 유저 테이블에서도 방 정보 삭제
        if(user.getUserId() == room.getOwnerId()){
            System.out.println("방번호 : "+room.getRoomId());
            List<User> userList = roomRepositorySupport.findUserListByRoomId(room.getRoomId());
            for(User users : userList){
                users.setRoom(null);
//                userRepository.findById(user.getUserId()).get().setRoom(null); // 방 나가면 유저 테이블에서도 방 정보 삭제
            }
            roomRepository.deleteById(room.getRoomId());
        }
    }

    @Override
    public List<Room> findAll() {
        return roomRepository.findAll();
    }

    @Override
    public Page<Room> findAll(Pageable pageable) { return roomRepository.findAll(pageable); }

    @Override
    public Page<Room> findByTitle(String title, Pageable pageable) { return roomRepository.findByTitleContains(title, pageable); }

    @Override
    public Page<Room> findByGame(String game, Pageable pageable) { return roomRepository.findByGameContains(game, pageable); }

    @Override
    public Page<Room> findByIsActive(Boolean isActive, Pageable pageable) { return roomRepository.findByIsActive(isActive, pageable); }

    @Override
    public Room findById(Long roomId) {
        Optional<Room> roomOpt = roomRepository.findById(roomId);
        return roomOpt.orElse(null);
    }
}
