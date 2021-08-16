package com.exp.narang.websocket.callmyname.model.manager;

import com.exp.narang.api.model.service.RoomService;
import com.exp.narang.websocket.callmyname.request.NameReq;
import com.exp.narang.websocket.callmyname.request.SetNameReq;
import com.exp.narang.websocket.callmyname.response.GuessNameRes;
import com.exp.narang.websocket.callmyname.response.GameStatusRes;
import com.exp.narang.websocket.callmyname.response.SetNameRes;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class GameManager {
    private SetNameRes setNameRes;
    private Map<String, Integer> voteStatus;
    private final Map<Long, String> nameMap;
    private final Set<Long> userIdSet;
    private final Queue<Long> userIdQueue;
    private final String defaultName [] = {"너랑이", "아이유", "해리포터", "타노스", "유재석", "모닝수박", "지수박", "담흥민", "동윤신", "준환킴", "드라큘라", "김연경", "지석진"};
    private Boolean isGotDefault[];
    private final int playerCnt;
    private int round, nowCnt, nextCnt, voteCompleteCnt, defaultCnt;
    private static final int SETTING = 0, PLAYING = 1, DEFAULT_NAME_SIZE = 13;
    private static final String USER_ID = "userId", NICKNAME = "nickname", NEXT = "next";
    private long playingUserId1, playingUserId2;
    private boolean isGameStarted;

    public GameManager(Long roomId, RoomService roomService){
        log.debug("GameManager 객체 생성 ~~");
        this.playerCnt = roomService.findUserListByRoomId(roomId).size();
        this.setNameRes = new SetNameRes();
        nameMap = new ConcurrentHashMap<>();
        voteStatus = new HashMap<>();
        userIdSet = new HashSet<>();
        setNameRes = new SetNameRes();
        userIdQueue = new ArrayDeque<>();
        round = 0;
        nowCnt = 0;
        nextCnt = 0;
        defaultCnt = 0;
    }

    /**
     * 게임에 참여한 사용자의 userId를 저장하는 메서드
     * @param userId : 사용자의 userId
     * @return 게임을 시작할지 여부
     */
    public boolean addPlayer(long userId) {
        log.debug("addPlayer 실행 ~~");
        userIdSet.add(userId);
        boolean allConnected = userIdSet.size() == playerCnt;
        // 전부 연결 되었을 때
        if(allConnected) {
            // 첫 대진표 만들기
            makeRandomDraw();
            // 이미 게임이 시작되었으면 null 반환
            if (isGameStarted) return false;
            // 게임이 시작되지 않았으면 게임 시작 표시
            isGameStarted = true;
            return true;
        }
        return false;
    }

    /**
     * 랜덤 대진표를 만드는 메서드
     */
    private void makeRandomDraw(){
        boolean[] selected = new boolean[playerCnt];
        Object[] userIdArr = userIdSet.toArray();
        int sCnt = 0;
        Random r = new Random();
        while(sCnt < playerCnt){
            int ri = r.nextInt(playerCnt);
            if(!selected[ri]){
                selected[ri] = true;
                userIdQueue.offer((long)userIdArr[ri]);
                sCnt++;
            }
        }
    }

    /**
     * Default 이름을 중복 없이 랜덤으로 지정하는 메서드
     * @return 이름
     */
    public String defaultName(){
        if(defaultCnt++ == 0) isGotDefault = new Boolean[DEFAULT_NAME_SIZE];
        int idx = (int)(Math.random() * 100) % DEFAULT_NAME_SIZE;
        while(isGotDefault[idx]){ idx = (int)(Math.random() * 100) % DEFAULT_NAME_SIZE; }
        isGotDefault[idx] = true;
        if(defaultCnt == playerCnt) defaultCnt = 0;
        return defaultName[idx];
    }

    /**
     * TODO : 테스트용으로 모든 플레이어가 투표 했을 경우 완료되게 해놓음. 찐은 playerCnt - 1
     * 정한 이름을 저장하는 메서드
     * @param req : 투표자 ID, 타겟 ID, 이름, 투표 여부, 종료 여부 가진 객체
     * @return 타겟 ID, 투표 결과 담긴 Map, 집계 상태, 최종 제시어 가진 객체
     */
    public SetNameRes setName(SetNameReq req){
        System.out.println("플레이어 수 :" + playerCnt);
        // 투표 현황 관리
        if(!req.isFinished()) {
            if(req.getVote() == 1) voteStatus.put(req.getContent(), voteStatus.get(req.getContent()) + 1);
            else if(req.getVote() == -1) voteStatus.put(req.getContent(), voteStatus.get(req.getContent()) - 1);
            else {
                // 첫 제시어 추가인 경우 voteStatus 초기화 (두 번째 사람 이름 정할 때 걸림)
                if(voteStatus.size() == playerCnt - 1) voteStatus = new HashMap<>();
                voteStatus.put(req.getContent(), 0);
            }
            return SetNameRes.returnResult(req.getTargetId(), "", false, voteStatus);
        }
        // 개표 현황 관리
        else {
            // 모든 사람 투표 완료한 경우
            if(++voteCompleteCnt == playerCnt - 1){
                String result = defaultName[(int)(Math.random() * 100) % DEFAULT_NAME_SIZE]; // 0~12까지 랜덤 인덱스로 이름 들어감
                int max = -1;
                // 최다 득표 이름 찾음
                for(String content : voteStatus.keySet()){
                    if(voteStatus.get(content) > max){
                        result = content;
                        max = voteStatus.get(content);
                    }
                }
                nameMap.put(req.getTargetId(), result);
                voteCompleteCnt = 0;
                return SetNameRes.returnResult(req.getTargetId(), result, true, voteStatus);
            }
            // 아직 모든 사람의 투표가 완료되지 않은 경우
            return SetNameRes.returnResult(req.getTargetId(), "", false, voteStatus);
        }
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

    /**
     * 현재 게임 라운드, 상태, userId, 이름 반환
     * @return GameStatusRes
     */
    public GameStatusRes getGameStatus(String type) {
        Map<String, Object> user1 = new HashMap<>();
        Map<String, Object> user2 = new HashMap<>();
        String userNick1 = "";
        String userNick2 = "";
        int status = PLAYING;

        log.debug("게임 정보 리턴~");
        // 다음 게임
        if(type.equals(NEXT)) {
            nextCnt++;
            if(nextCnt < playerCnt) return null;
            round++;
            log.debug("다음 게임ㄱㄱ");
            playingUserId1 = userIdQueue.poll();
            playingUserId2 = userIdQueue.poll();
            status = SETTING;
        }else{
            nowCnt++;
            if(nowCnt < playerCnt) return null;
            log.debug("이름 정했으니 게임ㄱㄱ");
            userNick1 = nameMap.get(playingUserId1);
            userNick2 = nameMap.get(playingUserId2);
        }

        user1.put(USER_ID, playingUserId1);
        user1.put(NICKNAME, userNick1);

        user2.put(USER_ID, playingUserId2);
        user2.put(NICKNAME, userNick2);

        nextCnt = 0;
        nowCnt = 0;
        return GameStatusRes.of(round, status, user1, user2);
    }

    /**
     * @return userIdQueue
     */
    public Queue<Long> getUserIdQueue(){
        return userIdQueue;
    }
}