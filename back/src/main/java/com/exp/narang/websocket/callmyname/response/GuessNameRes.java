package com.exp.narang.websocket.callmyname.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GuessNameRes {
    private long nextUserId;
    private final boolean isCorrect;
    private final boolean isGameEnd;
}
