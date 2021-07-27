package com.exp.narang.api.service;

import com.exp.narang.api.request.RoomRegisterPostReq;
import com.exp.narang.api.request.RoomSearchGetReq;
import com.exp.narang.api.request.RoomUpdatePatchReq;
import com.exp.narang.db.entity.Room;
import com.exp.narang.db.entity.User;
import com.exp.narang.db.repository.RoomRepository;
import com.exp.narang.db.repository.RoomRepositorySupport;
import com.exp.narang.db.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

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
                        .isActivate(true) // true면 방 활성화(=입장 가능) 상태
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
    public void updateRoom(RoomUpdatePatchReq roomUpdatePatchReq, Room room) {
        room.setTitle(roomUpdatePatchReq.getTitle());
        room.setGame(roomUpdatePatchReq.getGame());
        room.setMaxPlayer(roomUpdatePatchReq.getMaxPlayer());
        roomRepository.save(room);
    }

    @Override
    public void deleteRoom(Room room, User user) {
        user.setRoom(null); // 방 나가면 유저 테이블에서도 방 정보 삭제
        userRepository.save(user);
        if(user.getUserId() == room.getOwnerId()){ // 방 나가는 사람이 방장이라면 방 자체를 삭제
            System.out.println("방번호 : "+room.getRoomId());
            List<User> userList = findUserListByRoomId(room.getRoomId());
            for(User users : userList){
                users.setRoom(null); // 방 나가면 유저 테이블에서도 방 정보 삭제
                userRepository.save(user);
            }
            roomRepository.deleteById(room.getRoomId());
        }
    }

    @Override
    public List<User> findUserListByRoomId(Long roomId) {
        return roomRepositorySupport.findUserListByRoomId(roomId);
    }

    @Override
    public Page<Room> findBySearch(RoomSearchGetReq roomSearchGetReq, Pageable pageable) {
        return roomRepositorySupport.findBySearch(roomSearchGetReq, pageable);
    }

    @Override
    public Room findById(Long roomId) {
        Optional<Room> roomOpt = roomRepository.findById(roomId);
        return roomOpt.orElse(null);
    }
}
