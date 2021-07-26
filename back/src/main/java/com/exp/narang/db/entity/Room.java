package com.exp.narang.db.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    Long ownerId;

    int maxPlayer;
    int password;
    String thumbnailUrl;
    String title;
    String game;
    Boolean isActive;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    @CreationTimestamp
    private LocalDateTime createTime;
//    @Column(name="created_time")
//    private LocalDateTime createdTime = LocalDateTime.now();


    @OneToMany(mappedBy = "room")
    List<User> userList = new ArrayList<>();

    @Builder
    private Room(String title, Long ownerId, int maxPlayer, int password, Boolean isActive) {
        this.title = title;
        this.ownerId = ownerId;
        this.maxPlayer = maxPlayer;
        this.password = password;
        this.isActive = isActive;
    }

    public Room() {}
}
