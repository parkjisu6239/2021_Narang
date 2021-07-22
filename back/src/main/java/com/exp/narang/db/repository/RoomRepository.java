package com.exp.narang.db.repository;

import com.exp.narang.db.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
//    void deleteByUserEmail(Long userId);
    void deleteByUser(Long userId);
}
