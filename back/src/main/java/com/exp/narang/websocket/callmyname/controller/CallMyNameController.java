package com.exp.narang.websocket.callmyname.controller;

import com.exp.narang.websocket.callmyname.model.CallMyNameChatModel;
import com.exp.narang.websocket.callmyname.model.manager.GameManager;
import com.exp.narang.websocket.callmyname.request.NameReq;
import com.exp.narang.websocket.callmyname.response.CheckConnectRes;
import com.exp.narang.websocket.callmyname.response.GuessNameRes;
import com.exp.narang.websocket.callmyname.response.SetNameRes;
import com.exp.narang.websocket.chat.model.ChatModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
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

    /**
     * 게임을 시작하는 메서드
     * TODO : 게임 참가 인원 몇명인지 받기!!
     * @param roomId : path로 받는 roomId
     */
    @MessageMapping("/call/start/{roomId}")
    public void startGame(@DestinationVariable long roomId, int playerCnt){
        ManagerHolder.gameManagerMap.put(roomId, new GameManager(playerCnt));
    }

    /**
     * 콜마넴 게임방에 참여한 사용자를 추가하는 메서드
     * @param roomId : 방의 roomId
     * @param userId : 참여한 사용자의 userId
     */
    @MessageMapping("/call/addPlayer/{roomId}")
    public void addPlayer(@DestinationVariable long roomId, long userId){
        CheckConnectRes res = ManagerHolder.gameManagerMap.get(roomId).addPlayer(userId);
        if(res != null) broadcastAllConnected(roomId, res);
    }

    /**
     * 모든 사용자가 들어왔는지 메세지를 전달하는 메서드
     * @param roomId
     */
    public void broadcastAllConnected(long roomId, CheckConnectRes res){
        ManagerHolder.gameManagerMap.get(roomId);
        // 디폴트 이름과 이번에 게임할 userId 보내기
        template.convertAndSend("/from/call/checkConnect/" + roomId, res);
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

//    /**
//     * 정해진 이름을 저장하는 메서드
//     * TODO : 2명이서 하도록 변경. 순서대로 되는 로직인지 확인하기
//     * @param roomId : path로 받는 roomId (PK)
//     * @param req : userId와 정해진 이름을 멤버변수로 가진 객체
//     * @return 다음으로 이름을 정할 사용자의 userId, 이름 다 정하면 -1
//     */
//    @MessageMapping("/call/set-name/{roomId}")
//    @SendTo("/from/call/set-name/{roomId}")
//    public SetNameRes setName(@DestinationVariable long roomId, NameReq req){
//        return ManagerHolder.gameManagerMap.get(roomId).setName(req);
//    }
//
//    /**
//     * 다음 질문 순서 userId를 반환하는 메서드
//     * TODO : 2명이서 하도록 변경. 순서대로 되는 로직인지 확인하기
//     * @param roomId : path로 받는 roomId (PK)
//     * @return 다음에 질문할 사용자의 userId
//     */
//    @MessageMapping("/call/set-qtime/{roomId}")
//    @SendTo("/from/call/set-qtime/{roomId}")
//    public long setQuestionTime(@DestinationVariable long roomId){
//        return ManagerHolder.gameManagerMap.get(roomId).getNextUserId();
//    }

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

//    /**
//     * 정답 순서를 반환하는 메서드
//     * @param roomId
//     * @return 정답을 맞힌 순서대로 userId가 담긴 리스트
//     */
//    @MessageMapping("/call/get-rank/{roomId}")
//    @SendTo("/from/call/get-rank/{roomId}")
//    public List<Long> getRank(@DestinationVariable long roomId){
//        return ManagerHolder.gameManagerMap.get(roomId).getRank();
//    }
}