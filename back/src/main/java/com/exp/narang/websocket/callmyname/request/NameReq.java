package com.exp.narang.websocket.callmyname.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NameReq {
    private long userId;
    private String name;
}