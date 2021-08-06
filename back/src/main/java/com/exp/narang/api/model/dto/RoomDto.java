package com.exp.narang.api.model.dto;

import com.exp.narang.api.model.db.entity.Room;
import com.exp.narang.api.model.db.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RoomDto {
    Room room;
    List<User> joinUsers;
}
