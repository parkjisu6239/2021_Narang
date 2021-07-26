package com.exp.narang.api.request;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel("RoomReadGetRequest")
public class RoomReadGetReq {
    int password;
}
