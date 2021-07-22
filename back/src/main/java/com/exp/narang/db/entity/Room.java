package com.exp.narang.db.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 유저 모델 정의.
 */
@Entity
@Getter
@Setter
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;
    String thumbnailUrl;
    String title;
    String description;
    Boolean isActive;

    @Temporal(TemporalType.TIMESTAMP)
    Date callStartTime;
    @Temporal(TemporalType.TIMESTAMP)
    Date callEndTime;

    // TODO : 고민
    @OneToOne
    @JoinColumn(name = "owner_id")
    User user;

    @OneToMany(mappedBy = "room")
    List<User> userList = new ArrayList<>();
}
