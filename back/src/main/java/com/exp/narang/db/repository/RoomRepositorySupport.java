package com.exp.narang.db.repository;

import com.exp.narang.db.entity.QRoom;
import com.exp.narang.db.entity.QUser;
import com.exp.narang.db.entity.User;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 유저 모델 관련 디비 쿼리 생성을 위한 구현 정의.
 */
@Slf4j
@Repository
public class RoomRepositorySupport {
    @Autowired
    private JPAQueryFactory jpaQueryFactory;
    QUser qUser = QUser.user;
    QRoom qRoom = QRoom.room;

    public List<User> findUserListByRoomId(Long roomId) {
        List<User> userList = jpaQueryFactory.select(qUser).from(qRoom).innerJoin(qRoom.userList, qUser).where(qRoom.roomId.eq(roomId)).fetch();
//        List<User> userList = jpaQueryFactory.select(qUser).from(qRoom).join(qUser).on(qUser.room.roomId.eq(qRoom.roomId)).where(qRoom.roomId.eq(roomId)).fetch();
        return userList;

//        JPQLQuery<User> query = jpaQueryFactory.from(qRoom).innerJoin(qUser)
//                                                .select(qUser).where(qRoom.roomId.eq(roomId));
//        return query.fetch();


//        return jpaQueryFactory.select(qUser).from(qRoom)
//                .where(qRoom.roomId.eq(roomId)).fetch();
    }
}
