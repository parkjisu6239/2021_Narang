package com.exp.narang.websocket.mafia.controller;

import com.exp.narang.api.model.service.RoomService;
import com.exp.narang.websocket.mafia.model.Player;
import com.exp.narang.websocket.mafia.response.GameResult;
import com.exp.narang.websocket.mafia.request.*;
import com.exp.narang.websocket.mafia.response.RoleResult;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

import com.exp.narang.websocket.mafia.model.manager.GameManager;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class MafiaController {
    private static final Logger log = LoggerFactory.getLogger(MafiaController.class);
    private static Map<Long, GameManager> gameManagerMap;

    @Autowired
    private RoomService roomService;

    @PostConstruct
    public void init() { gameManagerMap = new ConcurrentHashMap<>();
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
    public RoleResult broadcasting(@DestinationVariable Long roomId, @DestinationVariable String username) throws Exception {
        log.debug("GameStart arrived: /gameStart/{}/{}, gameStart: {}", roomId, username);
        System.out.println("당신의 역할은???!!");
        return gameManagerMap.get(roomId).findRoleNameByUsername(username);
    }

    // 투표합시다 (낮 1 : day1, 낮 2 : day2, 밤 : night)
    @MessageMapping("/mafia/vote/{roomId}")
    @SendTo("/from/mafia/vote/{roomId}")
    public GameResult broadcasting(VoteMessage voteMessage, @DestinationVariable long roomId) throws Exception {
    	log.debug("voteMessage arrived: /vote/{}, voteMessage: {}", roomId, voteMessage);
        return gameManagerMap.get(roomId).returnVoteResult(voteMessage);
    }

    // 마피아들끼리 통신. 미션 성공 여부 전달, 투표 가능 여부 반환
    @MessageMapping("/mafia/mafias/{roomId}")
    @SendTo("/from/mafia/mafias/{roomId}")
    public int broadcasting(MafiaMessage mafiaMessage, @DestinationVariable long roomId) throws Exception {
        System.out.println("마피아끼리 통신 중 : "+mafiaMessage.getUsername() +", "+mafiaMessage.getIsMissionComplete());
        return gameManagerMap.get(roomId).isEveryMafiaComplete(mafiaMessage);
    }

    @MessageMapping("/mafia/players/{roomId}")
    @SendTo("/from/mafia/players/{roomId}")
    public List<String> getPlayers(@DestinationVariable long roomId) throws Exception {
        List<Player> list =gameManagerMap.get(roomId).getGamePlayers().getPlayers();
        List<String> players = new ArrayList<>();
        for(int i = 0; i < list.size(); i++) {
            players.add(list.get(i).getUser().getUsername());
        }
        return players;
    }
}
