package com.exp.narang.websocket.callmyname.response;

import lombok.Data;
import lombok.Setter;

import java.util.Map;

@Data
public class GameStatusRes {
    private static class ResHolder{
        private static final GameStatusRes res = new GameStatusRes();
    }

    private int round;
    private int status;
    private Map<String, Object> user1;
    private Map<String, Object> user2;

    private GameStatusRes(){}

    public static GameStatusRes of(int round, int status, Map<String, Object> user1, Map<String, Object> user2){
        ResHolder.res.setRound(round);
        ResHolder.res.setStatus(status);
        ResHolder.res.setUser1(user1);
        ResHolder.res.setUser2(user2);
        return ResHolder.res;
    }
}
