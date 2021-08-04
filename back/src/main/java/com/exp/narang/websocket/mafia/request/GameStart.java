package com.exp.narang.websocket.mafia.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameStart {

    private String userName;

    @Override
    public String toString() {
        return String.format("userName:%s started!", userName);
    }

}
