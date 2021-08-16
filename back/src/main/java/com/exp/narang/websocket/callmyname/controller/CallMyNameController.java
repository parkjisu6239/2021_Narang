package com.exp.narang.websocket.callmyname.controller;

import com.exp.narang.api.model.service.RoomService;
import com.exp.narang.websocket.callmyname.model.CallMyNameChatModel;
import com.exp.narang.websocket.callmyname.model.manager.GameManager;
import com.exp.narang.websocket.callmyname.request.NameReq;
import com.exp.narang.websocket.callmyname.request.SetNameReq;
import com.exp.narang.websocket.callmyname.response.GuessNameRes;
import com.exp.narang.websocket.callmyname.response.GameStatusRes;
import com.exp.narang.websocket.callmyname.response.SetNameRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@Slf4j
@RequiredArgsConstructor
public class CallMyNameController {
    private static class ManagerHolder{
        private static final Map<Long, GameManager> gameManagerMap = new ConcurrentHashMap<>();
    }

    private final SimpMessagingTemplate template;
    private final RoomService roomService;

    /**
     * 게임을 시작하는 메서드
     * TODO : 게임 참가 인원 몇명인지 받기!!
     * @param roomId : path로 받는 roomId
     */
    @MessageMapping("/call/start/{roomId}")
    public void startGame(@DestinationVariable long roomId){
        log.debug(" | roomId 번방 시작 ~~");
        ManagerHolder.gameManagerMap.put(roomId, new GameManager(roomId, roomService));
        log.debug(ManagerHolder.gameManagerMap.toString());
    }

    /**
     * 콜마넴 게임방에 참여한 사용자를 추가하는 메서드
     * @param roomId : 방의 roomId
     * @param userId : 참여한 사용자의 userId
     */
    @MessageMapping("/call/addPlayer/{roomId}")
    public void addPlayer(@DestinationVariable long roomId, long userId){
        if(ManagerHolder.gameManagerMap.get(roomId).addPlayer(userId))
            broadcastAllConnected(roomId);
        log.debug(userId + " 들어옴");
    }

    /**
     * 모든 사용자가 들어왔는지 메세지를 전달하는 메서드
     * @param roomId
     */
    public void broadcastAllConnected(long roomId){
        template.convertAndSend("/from/call/checkConnect/" + roomId, ManagerHolder.gameManagerMap.get(roomId).getUserIdQueue());
        log.debug("다 들어옴");
    }

    /**
     * 게임 방 안에서 채팅할 때 호출되는 메서드
     * @param chatModel
     * @return 채팅에 필요한 내용을 가진 객체
     */
    @MessageMapping("/call/chat/{roomId}")
    @SendTo("/from/call/chat/{roomId}")
    public CallMyNameChatModel sendMessage(CallMyNameChatModel chatModel){
        return chatModel;
    }

    /**
     * Default 이름 지정하는 메서드
     * @param roomId : path로 받는 roomId (PK)
     * @param userId : path로 받는 userId (PK)
     * @return 이름
     */
    @MessageMapping("/call/default-name/{roomId}/{userId}")
    @SendTo("/from/call/default-name/{roomId}/{userId}")
    public String defaultName(@DestinationVariable long roomId, @DestinationVariable long userId){
        return ManagerHolder.gameManagerMap.get(roomId).defaultName();
    }

    /**
     * 이름 투표하는 메서드
     * TODO : 2명을 제외한 사람들이 투표. 동점이면 첫 번째 거 채택
     * @param roomId : path로 받는 roomId (PK)
     * @param setNameReq : 투표자 ID, 타겟 ID, 이름, 투표 여부, 종료 여부 가진 객체
     * @return 타겟 ID, 투표 결과 담긴 Map, 집계 상태, 최종 제시어 가진 객체
     */
    @MessageMapping("/call/set-name/{roomId}")
    @SendTo("/from/call/set-name/{roomId}")
    public SetNameRes setName(@DestinationVariable long roomId, SetNameReq setNameReq){
        return ManagerHolder.gameManagerMap.get(roomId).setName(setNameReq);
    }

    /**
     * 다음 게임으로 넘어가거나 이름을 모두 정하면 호출되는 메서드
     * @param roomId : path로 받는 roomId (PK)
     * @param type : 반환 값이 다음 게임의 정보인지 현재 게임 정보인지 구분하기 위한 변수
     */
    @MessageMapping("/call/play/{roomId}")
    public void getGameStatus(@DestinationVariable long roomId, String type){
        type = type.substring(1, type.length() - 1);
        log.debug("이것이 type이다. " + type);
        GameStatusRes res = ManagerHolder.gameManagerMap.get(roomId).getGameStatus(type);
        if(res != null) broadcastGameStatus(roomId, res);
    }

    /**
     * 게임 정보와 게임 중인 userId를 반환하는 메서드
     * @param roomId : 게임중인 방의 roomId
     * @param res : 게임 진행 정보
     */
    public void broadcastGameStatus(long roomId, GameStatusRes res){
        log.debug("게임 정보 반환~~");
        log.debug(res.toString());
        template.convertAndSend("/from/call/play/" + roomId, res);
    }

    /**
     * 사용자가 자신의 이름을 맞힐 때 호출되는 메서드
     * TODO : 반환값 필요한거 추가하기
     * @param roomId : path로 받는 roomId (PK)
     * @param req : userId와 정해진 이름이 있는 객체
     * @return 답이 맞았는지, 게임이 끝났는지 여부를 멤버변수로 가진 객체
     */
    @MessageMapping("/call/guess-name/{roomId}")
    @SendTo("/from/call/guess-name/{roomId}")
    public GuessNameRes guessName(@DestinationVariable long roomId, NameReq req){
        GuessNameRes res = ManagerHolder.gameManagerMap.get(roomId).guessName(req);
        // 게임 끝나면 Map에서 Manager 삭제
        if(res.isGameEnd()) ManagerHolder.gameManagerMap.remove(roomId);
        return res;
    }
}