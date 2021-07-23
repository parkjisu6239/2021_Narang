package com.exp.narang.db.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    int maxPlayer;
    String thumbnailUrl;
    String title;
    Boolean isActive;
    Long ownerId;
    String game;

    @CreationTimestamp
    @Column(name="created_time")
    private LocalDateTime createdTime = LocalDateTime.now();


    @OneToMany(mappedBy = "room")
    List<User> userList = new ArrayList<>();

    @Builder
    private Room(String title, Long ownerId, int maxPlayer) {
        this.title = title;
        this.ownerId = ownerId;
        this.maxPlayer = maxPlayer;
    }

    public Room() {}
}
