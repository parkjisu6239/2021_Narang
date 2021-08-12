package com.exp.narang.websocket.callmyname.model.manager;

import com.exp.narang.websocket.callmyname.request.NameReq;
import com.exp.narang.websocket.callmyname.response.GuessNameRes;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GameManager {
    private final Map<Long, String> nameMap;
    private final Set<Long> userIdSet;
//    private final List<Long> winList;
    private final Queue<Long> userIdQueue;
    private int currentSetNameTurn;
    private int currentSetterTurn;
    private int currentGuessTurn;
    private final int playerCnt;

    public GameManager(int playerCnt){
        this.playerCnt = playerCnt;
        nameMap = new ConcurrentHashMap<>();
        userIdSet = new HashSet<>();
//        winList = new ArrayList<>();
        userIdQueue = new ArrayDeque<>();
        currentSetNameTurn = 1;
        currentSetterTurn = 2;
        currentGuessTurn = 1;
    }

//    /**
//     * TODO : 토너먼트 방식으로 변경, 2명씩 묶어주기
//     * 게임을 시작할 때 호출되는 메서드
//     * @return 처음 이름을 정할 사용자의 id, 이름을 정해주는 사용자의 id를 가진 SetNameRes 객체
//     */
//    public SetNameRes getFirstUserIds(){
//        return SetNameRes.Companion.of(userSet.get(0).getUserId(), userSet.get(1).getUserId());
//    }

    /**
     * 게임에 참여한 사용자의 userId를 저장하는 메서드
     * @param userId : 사용자의 userId
     */
    public boolean addPlayer(long userId) {
        userIdSet.add(userId);
        boolean allConnected = userIdSet.size() == playerCnt;
        if(allConnected) {
            userIdQueue.addAll(userIdSet);
            // TODO : 붙을 사람 정하기
        }
        return allConnected;
    }

//    /**
//     * 정한 이름을 저장하는 메서드
//     * @param req : userId와 정해진 이름을 멤버변수로 가진 객체
//     * @return 다음으로 이름을 정할 사용자의 id, 이름을 정해주는 사용자의 id를 가진 SetNameRes 객체
//     */
//    public SetNameRes setName(NameReq req){
//        nameMap.put(req.getUserId(), req.getName());
//        if(currentSetNameTurn < userSet.size())
//            return SetNameRes.Companion.of(
//                    userSet.get(currentSetNameTurn++).getUserId(),
//                    userSet.get(currentSetterTurn++).getUserId());
//        else return SetNameRes.Companion.getEndInstance();
//    }

//    /** TODO : 중간에 누군가 나가면 어떻게 처리할지 정하기
//     * @return 다음 질문시간을 갖는 사용자의 userId
//     */
//    public long getNextUserId(){
//        currentGuessTurn++;
//        currentGuessTurn %= userSet.size();
//        return userSet.get(currentGuessTurn).getUserId();
//    }

    /** TODO : 중간에 누군가 나가면 어떻게 처리할지 정하기
     * 사용자가 자신의 이름을 맞힐 때 호출되는 메서드
     * @param req : userId와 정해진 이름이 있는 객체
     * @return 답이 맞았는지, nameMap 이 비었는지 여부를 멤버변수로 가진 객체
     */
    public GuessNameRes guessName(NameReq req){
        currentGuessTurn %= userIdSet.size();
        boolean isCorrect = nameMap.get(req.getUserId()).equals(req.getName());
        // 맞으면
        if(isCorrect){
            // Map에서 삭제
            nameMap.remove(req.getUserId());
            // 정답자 처리
            winList.add(req.getUserId());
            userIdSet.remove(req.getUserId());
        }
        return new GuessNameRes(isCorrect, nameMap.isEmpty());
    }

//    public List<Long> getRank(){
//        return winList;
//    }
}