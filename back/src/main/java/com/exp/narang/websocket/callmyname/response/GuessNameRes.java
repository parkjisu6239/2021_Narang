package com.exp.narang.websocket.callmyname.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GuessNameRes {
    private final long userId;
    private final boolean isCorrect;
    private final boolean isGameEnd;
}
