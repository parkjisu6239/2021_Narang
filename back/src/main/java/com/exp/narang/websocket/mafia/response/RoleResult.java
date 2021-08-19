package com.exp.narang.websocket.mafia.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// 역할과 미션 번호를 부여한다.
public class RoleResult {
    private String roleName;
    private int missionNumber;
}
