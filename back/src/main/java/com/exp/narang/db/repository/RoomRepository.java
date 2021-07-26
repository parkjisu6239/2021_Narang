package com.exp.narang.db.repository;

import com.exp.narang.db.entity.Room;
import com.exp.narang.db.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
//    void deleteByUserEmail(Long userId);
//    void deleteByUser(User user);
    Room findByOwnerId(Long ownerId);
    List<Room> findByTitleContains(String title);
    List<Room> findByGameContains(String game);
    List<User> findUserListByRoomId(Long roomId);
}
