package com.exp.narang.api.service;

import com.exp.narang.db.entity.Room;
import com.exp.narang.db.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoomServiceImpl implements RoomService {
    @Autowired
    RoomRepository roomRepository;

    @Override
    public void addRoom(Room room) {
        roomRepository.save(room);
    }

    @Transactional
    @Override
    public void deleteRoom(Long userId) {
        roomRepository.deleteByUser(userId);
    }
}
