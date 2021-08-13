package com.exp.narang.websocket.callmyname.model.manager;

import com.exp.narang.websocket.callmyname.request.NameReq;
import com.exp.narang.websocket.callmyname.response.CheckConnectRes;
import com.exp.narang.websocket.callmyname.response.GuessNameRes;
import com.exp.narang.websocket.callmyname.response.SetNameRes;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GameManager {
    private final Map<Long, String> nameMap;
    private final Set<Long> userIdSet;
    private final Queue<Long> userIdQueue;
    private final int playerCnt;
    private boolean isGameStarted;

    public GameManager(int playerCnt){
        this.playerCnt = playerCnt;
        nameMap = new ConcurrentHashMap<>();
        userIdSet = new HashSet<>();
        userIdQueue = new ArrayDeque<>();
    }

    /**
     * 게임에 참여한 사용자의 userId를 저장하는 메서드
     * @param userId : 사용자의 userId
     */
    public CheckConnectRes addPlayer(long userId) {
        userIdSet.add(userId);
        boolean allConnected = userIdSet.size() == playerCnt;
        // 전부 연결 되었을 때
        if(allConnected) {
            // 첫 대진표 만들기
            makeRandomDraw();
            // 이미 게임이 시작되었으면 null 반환
            if (isGameStarted) return null;
            // 게임이 시작되지 않았으면 게임 시작 표시
            isGameStarted = true;
            return new CheckConnectRes(userIdQueue.poll(), userIdQueue.poll());
        }
        return null;
    }

    /**
     * 랜덤 대진표를 만드는 메서드
     */
    private void makeRandomDraw(){
        boolean[] selected = new boolean[playerCnt];
        Long[] userIdArr = (Long[]) userIdSet.toArray();
        int sCnt = 0;
        Random r = new Random();
        while(sCnt < playerCnt){
            int ri = r.nextInt(playerCnt);
            if(!selected[ri]){
                selected[ri] = true;
                userIdQueue.offer(userIdArr[ri]);
                sCnt++;
            }
        }
    }

    /**
     * 정한 이름을 저장하는 메서드
     * @param req : userId와 정해진 이름을 멤버변수로 가진 객체
     * @return 다음으로 이름을 정할 사용자의 id, 이름을 정해주는 사용자의 id를 가진 SetNameRes 객체
     */
    public SetNameRes setName(NameReq req){
        nameMap.put(req.getUserId(), req.getName());
//        if(currentSetNameTurn < userSet.size())
//            return SetNameRes.Companion.of(
//                    userSet.get(currentSetNameTurn++).getUserId(),
//                    userSet.get(currentSetterTurn++).getUserId());
//        else
            return SetNameRes.Companion.getEndInstance();
    }

    /** TODO : 중간에 누군가 나가면 어떻게 처리할지 정하기
     * 사용자가 자신의 이름을 맞힐 때 호출되는 메서드
     * @param req : userId와 정해진 이름이 있는 객체
     * @return 답이 맞았는지, nameMap 이 비었는지 여부를 멤버변수로 가진 객체
     */
    public GuessNameRes guessName(NameReq req){
        boolean isCorrect = nameMap.get(req.getUserId()).equals(req.getName());
        // 맞으면
        if(isCorrect){
            // Map에서 삭제
            nameMap.remove(req.getUserId());
            // 정답자 처리
            userIdQueue.offer(req.getUserId());
            // 우승 ~
            if(userIdQueue.size() == 1) return new GuessNameRes(req.getUserId(), true, true);
        }
        return new GuessNameRes(req.getUserId(), isCorrect, nameMap.isEmpty());
    }

//    public List<Long> getRank(){
//        return winList;
//    }
}