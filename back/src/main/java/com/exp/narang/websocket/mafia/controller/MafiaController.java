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
        gameManagerMap.put(roomId, new GameManager(roomId));
    }

    // 역할 확인하기 버튼을 누르면 roomId, username 파라미터를 통하여 각자 역할을 확인한다.
    @MessageMapping("/mafia/role/{roomId}/{username}")
    @SendTo("/from/mafia/role/{roomId}/{username}")
    public String broadcasting(@DestinationVariable Long roomId, @DestinationVariable String username) throws Exception {
        log.debug("GameStart arrived: /gameStart/{}/{}, gameStart: {}", roomId, username);
        List<Player> players = gameManagerMap.get(roomId).getGamePlayers().getPlayers();

        return gameManagerMap.get(roomId).findRoleNameByUsername(username);
    }
//
//
//    @MessageMapping("/invest/{roomId}/{userName}")
//    @SendTo("/from/invest/{roomId}/{userName}")
//    public String broadcasting(@DestinationVariable String userName, InvestMessage investMessage, @DestinationVariable long roomId) throws Exception {
//        Room gameRoom = lobby.getRoom(roomId);
//        return gameRoom.getUserRoleNameInGame(investMessage.getTheVoted());
//    }
//
//    @MessageMapping("/vote/{roomId}")
//    @SendTo("/from/vote/{roomId}")
//    public GameResult broadcasting(VoteMessage voteMessage, @DestinationVariable long roomId) throws Exception {
//    	log.debug("voteMessage arrived: /vote/{}, voteMessage: {}", roomId, voteMessage);
//        Room gameRoom = lobby.getRoom(roomId);
//        return gameRoom.returnVoteResult(voteMessage);
//    }
//
//
//    private final SimpMessagingTemplate template;
//    // /server 메시지를 받을 endpoint로 설정합니다.
//    @MessageMapping("/mafia/dam")
//    // 1) /server에서 메시지를 받고, /client로 메시지를 보내줍니다.
//    public void sendMessage(MafiaModel mafiaModel) {
//        Long roomId = mafiaModel.getRoomId();
//        log.debug(mafiaModel.toString());
//        // /client/roomId 로 메시지를 반환합니다. 프론트에서 구독한 endpoint 경로이름
//        template.convertAndSend("/mafia/" + roomId, mafiaModel);
//    }
}
