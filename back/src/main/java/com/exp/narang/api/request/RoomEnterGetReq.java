package com.exp.narang.api.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel("RoomEnterGetRequest")
public class RoomEnterGetReq {
    @ApiModelProperty(name="방 비밀번호 (숫자 4자리)", example="1234")
    int password;
}
