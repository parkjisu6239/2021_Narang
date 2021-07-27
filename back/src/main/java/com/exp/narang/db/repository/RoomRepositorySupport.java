package com.exp.narang.db.repository;

import com.exp.narang.api.request.RoomSearchGetReq;
import com.exp.narang.db.entity.QRoom;
import com.exp.narang.db.entity.QUser;
import com.exp.narang.db.entity.Room;
import com.exp.narang.db.entity.User;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * 유저 모델 관련 디비 쿼리 생성을 위한 구현 정의.
 */
@Slf4j
@Repository
public class RoomRepositorySupport extends QuerydslRepositorySupport {
    private final JPAQueryFactory jpaQueryFactory;
    private final EntityManager entityManager;
    QUser qUser = QUser.user;
    QRoom qRoom = QRoom.room;

    public RoomRepositorySupport(JPAQueryFactory jpaQueryFactory, EntityManager entityManager) {
        super(Room.class);
        this.jpaQueryFactory = jpaQueryFactory;
        this.entityManager = entityManager;
    }

    public PageImpl<Room> findBySearch(RoomSearchGetReq roomSearchGetReq, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();
        System.out.println(roomSearchGetReq.getGame());
        if(roomSearchGetReq.getGame() != null && roomSearchGetReq.getGame() != "") {
            builder.and(qRoom.game.eq(roomSearchGetReq.getGame()));
        }
        if(roomSearchGetReq.getIsActivate() != null) {
            builder.and(qRoom.isActivate.eq(roomSearchGetReq.getIsActivate()));
        }
        System.out.println(roomSearchGetReq.getTitle());
        if(roomSearchGetReq.getTitle() != null && roomSearchGetReq.getTitle() != "") {
            builder.and(qRoom.title.contains(roomSearchGetReq.getTitle()));
        }

        JPQLQuery<Room> query = jpaQueryFactory.select(qRoom).from(qRoom).where(builder);
        System.out.println(query.toString());
        long totalCount = query.fetchCount();
        System.out.println("total Count " + totalCount);
        System.out.println(pageable.toString());
        System.out.println(getQuerydsl().applyPagination(pageable, query).toString());
        List<Room> result = getQuerydsl().applyPagination(pageable, query).fetch();
        return new PageImpl<>(result, pageable, totalCount);
    }

    public List<User> findUserListByRoomId(Long roomId) {
        List<User> userList = jpaQueryFactory.select(qUser).from(qRoom).innerJoin(qRoom.userList, qUser).where(qRoom.roomId.eq(roomId)).fetch();
        return userList;
    }


}
