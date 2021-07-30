package com.exp.narang.api.model;

import com.exp.narang.db.entity.Room;
import com.exp.narang.db.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RoomDto {
    Room room;
    List<User> joinUsers;
}
