package com.exp.narang.websocket.callmyname.controller;

import com.exp.narang.websocket.callmyname.model.manager.GameManager;
import com.exp.narang.websocket.callmyname.request.NameReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@Slf4j
public class CallMyNameController {
    private static class ManagerHolder{
        private static final Map<Long, GameManager> gameManagerMap = new ConcurrentHashMap<>();
    }

    // 게임 시작
    @MessageMapping("/call/start/{roomId}")
    @SendTo("/from/call/start/{roomtId}")
    public void startGame(@DestinationVariable long roomId){
        ManagerHolder.gameManagerMap.put(roomId, new GameManager());
    }

    // 이름 정하기
    @MessageMapping("/call/set-name/{roomId}")
    @SendTo("/from/call/set-name/{roomId}")
    public void setName(@DestinationVariable long roomId, NameReq req){
        ManagerHolder.gameManagerMap.get(roomId).setIdentity(req);
    }

    // 이름 맞히기
    // TODO : 반환값 정하기
    @MessageMapping("/call/guess-name/{roomId}")
    @SendTo("/from/call/guess-name/{roomId}")
    public void guessName(@DestinationVariable long roomId, NameReq req){
        ManagerHolder.gameManagerMap.get(roomId).setIdentity(req);
    }
}