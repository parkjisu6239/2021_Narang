package com.exp.narang.db.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 유저 모델 정의.
 */
@Entity
@Getter
@Setter
public class User{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    String username;
    String email;
    @JsonIgnore
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    String password;

    @Builder
    private User(String email, String password, String username) {
        this.email = email;
        this.password = password;
        this.username = username;
    }

    public User() {}

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    Room room;
}
