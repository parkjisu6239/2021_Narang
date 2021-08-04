package com.exp.narang.api.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel("RoomRegisterPostRequest")
public class RoomRegisterPostReq {
    @ApiModelProperty(name="방 제목", example="5팀 모두 모여")
    String title;
    @ApiModelProperty(name="방 최대 인원", example="5")
    int maxPlayer;
    @ApiModelProperty(name="방 비밀번호 (숫자 4자리)", example="1234")
    int password;
}
