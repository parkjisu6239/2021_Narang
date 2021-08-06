package com.exp.narang.api.model.service;

import com.exp.narang.api.model.request.RoomRegisterPostReq;
import com.exp.narang.api.model.request.RoomSearchGetReq;
import com.exp.narang.api.model.request.RoomUpdatePatchReq;
import com.exp.narang.api.model.db.entity.Room;
import com.exp.narang.api.model.db.entity.User;
import com.exp.narang.api.model.db.repository.RoomRepository;
import com.exp.narang.api.model.db.repository.RoomRepositorySupport;
import com.exp.narang.api.model.db.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
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
        room.setPassword(roomUpdatePatchReq.getPassword());
        roomRepository.save(room);
    }

    @Override
    public void deleteRoom(Room room, User user) {
        log.error("이것이 방번호 다 : "+room.getRoomId());
        user.setRoom(null); // 방 나가면 유저 테이블에서도 방 정보 삭제
        userRepository.save(user);

        List<User> userList = findUserListByRoomId(room.getRoomId());
        if(!room.getOwnerId().equals(user.getUserId())) return;

        userList.remove(user);
        // 방에 0명만 있으면 방 삭제
        if(userList.size() == 0) {
            roomRepository.deleteById(room.getRoomId());
            return;
        }

        // 방 나가는 사람이 방장이라면 방장 위임
        User u = userList.get(0);
        room.setOwnerId(u.getUserId());
        roomRepository.save(room);
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
