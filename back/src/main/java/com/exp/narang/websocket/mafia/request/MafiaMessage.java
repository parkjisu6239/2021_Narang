package com.exp.narang.websocket.mafia.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MafiaMessage {
    private String username; // 자기 username (마피아 본인)
    private Boolean isMissionComplete; // 미션 성공 여부
}
