package com.exp.narang.api.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel("RoomUpdatePatchRequest")
public class RoomUpdatePatchReq {
    @ApiModelProperty(name="방 제목", example="5팀 모두 모여")
    String title;
    @ApiModelProperty(name="게임 종류", example="mafia")
    String game;
    @ApiModelProperty(name="방 최대 인원", example="5")
    int maxPlayer;
    @ApiModelProperty(name="방 비밀번호", example="1234")
    int password;
}
