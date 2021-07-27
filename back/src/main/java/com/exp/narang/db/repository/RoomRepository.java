package com.exp.narang.db.repository;

import com.exp.narang.db.entity.Room;
import com.exp.narang.db.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
//    void deleteByUserEmail(Long userId);
//    void deleteByUser(User user);
    Room findByOwnerId(Long ownerId);
    Page<Room> findByTitleContains(String title, Pageable pageable);
    Page<Room> findByGameContains(String game, Pageable pageable);
    Page<Room> findByIsActive(Boolean isActive, Pageable pageable);
    List<User> findUserListByRoomId(Long roomId);
}
