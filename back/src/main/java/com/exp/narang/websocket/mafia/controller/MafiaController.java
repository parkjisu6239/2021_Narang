package com.exp.narang.websocket.mafia.controller;

import com.exp.narang.api.service.RoomService;
import com.exp.narang.db.entity.Room;
import com.exp.narang.db.entity.User;
import com.exp.narang.websocket.mafia.model.Player;
import com.exp.narang.websocket.mafia.model.service.GameResult;
import com.exp.narang.websocket.mafia.request.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exp.narang.websocket.mafia.model.manager.GameManager;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class MafiaController {
    private static final Logger log = LoggerFactory.getLogger(MafiaController.class);
    private static Map<Long, GameManager> gameManagerMap;

    @Autowired
    private RoomService roomService;

    @PostConstruct
    public void init() {
        gameManagerMap = new ConcurrentHashMap<>();
    }

    // 방장이 gameStart 버튼을 누르면 roomId를 전송하여 mafia 게임에 필요한 설정을 한다. (역할 분배)
    @MessageMapping("/mafia/start/{roomId}")
    @SendTo("/from/mafia/start/{roomId}")
    public void broadcasting(@DestinationVariable long roomId) throws Exception {
        gameManagerMap.put(roomId, new GameManager(roomId, roomService));
    }

    // 역할 확인하기 버튼을 누르면 roomId, username 파라미터를 통하여 각자 역할을 확인한다.
    @MessageMapping("/mafia/role/{roomId}/{username}")
    @SendTo("/from/mafia/role/{roomId}/{username}")
    public String broadcasting(@DestinationVariable Long roomId, @DestinationVariable String username) throws Exception {
        log.debug("GameStart arrived: /gameStart/{}/{}, gameStart: {}", roomId, username);
        return gameManagerMap.get(roomId).findRoleNameByUsername(username);
    }
    // 투표합시다 (낮 1 : day1, 낮 2 : day2, 밤 : night)
    @MessageMapping("/mafia/vote/{roomId}")
    @SendTo("/from/mafia/vote/{roomId}")
    public GameResult broadcasting(VoteMessage voteMessage, @DestinationVariable long roomId) throws Exception {
    	log.debug("voteMessage arrived: /vote/{}, voteMessage: {}", roomId, voteMessage);
        return gameManagerMap.get(roomId).returnVoteResult(voteMessage);
    }
}
