package com.exp.narang.api.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomSearchGetReq {
    @ApiModelProperty(name="방 제목", example="5팀 모두 모여")
    String title;
    @ApiModelProperty(name="게임 종류", example="mafia")
    String game;
    @ApiModelProperty(name="방 활성화", example="true")
    Boolean isActivate;

    public static RoomSearchGetReq of(String title, String game, Boolean isActivate) {
        RoomSearchGetReq req = new RoomSearchGetReq();
        req.setGame(game);
        req.setTitle(title);
        req.setIsActivate(isActivate);
        return req;
    }
}