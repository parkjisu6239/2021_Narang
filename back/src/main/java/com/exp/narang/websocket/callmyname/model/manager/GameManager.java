package com.exp.narang.websocket.callmyname.model.manager;

import com.exp.narang.api.model.db.entity.User;
import com.exp.narang.api.model.service.RoomService;
import com.exp.narang.websocket.callmyname.request.NameReq;
import com.exp.narang.websocket.callmyname.response.GuessNameRes;
import com.exp.narang.websocket.callmyname.response.SetNameRes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameManager {
    private final Map<Long, String> nameMap;
    private final List<User> users;
    private final List<Long> winList;
    private int currentSetNameTurn;
    private int currentSetterTurn;
    private int currentGuessTurn;

    public GameManager(long roomId, RoomService roomService){
        nameMap = new ConcurrentHashMap<>();
        users = roomService.findUserListByRoomId(roomId);
        winList = new ArrayList<>();
        currentSetNameTurn = 1;
        currentSetterTurn = 2;
        currentGuessTurn = 1;
    }

    /**
     * 게임을 시작할 때 호출되는 메서드
     * @return 처음 이름을 정할 사용자의 id, 이름을 정해주는 사용자의 id를 가진 SetNameRes 객체
     */
    public SetNameRes getFirstUserIds(){
        return SetNameRes.Companion.of(users.get(0).getUserId(), users.get(1).getUserId());
    }

    /**
     * 정한 이름을 저장하는 메서드
     * @param req : userId와 정해진 이름을 멤버변수로 가진 객체
     * @return 다음으로 이름을 정할 사용자의 id, 이름을 정해주는 사용자의 id를 가진 SetNameRes 객체
     */
    public SetNameRes setName(NameReq req){
        nameMap.put(req.getUserId(), req.getName());
        if(currentSetNameTurn < users.size())
            return SetNameRes.Companion.of(
                    users.get(currentSetNameTurn++).getUserId(),
                    users.get(currentSetterTurn++).getUserId());
        else return SetNameRes.Companion.getEndInstance();
    }

    /** TODO : 중간에 누군가 나가면 어떻게 처리할지 정하기
     * @return 다음 질문시간을 갖는 사용자의 userId
     */
    public long getNextUserId(){
        currentGuessTurn++;
        currentGuessTurn %= users.size();
        return users.get(currentGuessTurn).getUserId();
    }

    /** TODO : 중간에 누군가 나가면 어떻게 처리할지 정하기
     * 사용자가 자신의 이름을 맞힐 때 호출되는 메서드
     * @param req : userId와 정해진 이름이 있는 객체
     * @return 답이 맞았는지, nameMap 이 비었는지 여부를 멤버변수로 가진 객체
     */
    public GuessNameRes guessName(NameReq req){
        currentGuessTurn %= users.size();
        boolean isCorrect = nameMap.get(req.getUserId()).equals(req.getName());
        // 맞으면
        if(isCorrect){
            // Map에서 삭제
            nameMap.remove(req.getUserId());
            // 정답자 처리
            winList.add(req.getUserId());
            users.removeIf(it -> it.getUserId().equals(req.getUserId()));
        }
        return new GuessNameRes(isCorrect, nameMap.isEmpty());
    }

    public List<Long> getRank(){
        return winList;
    }
}