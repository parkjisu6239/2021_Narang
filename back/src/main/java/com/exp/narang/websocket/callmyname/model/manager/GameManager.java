package com.exp.narang.websocket.callmyname.model.manager;

import com.exp.narang.api.model.service.RoomService;
import com.exp.narang.api.model.service.RoomServiceImpl;
import com.exp.narang.websocket.callmyname.request.NameReq;
import com.exp.narang.websocket.callmyname.request.SetNameReq;
import com.exp.narang.websocket.callmyname.response.GuessNameRes;
import com.exp.narang.websocket.callmyname.response.GameStatusRes;
import com.exp.narang.websocket.callmyname.response.SetNameRes;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.kurento.jsonrpc.client.JsonRpcClient.log;

@Slf4j
public class GameManager {
    private RoomService roomService;
    private SetNameRes setNameRes;
    private Map<String, Integer> voteStatus;
    private final Map<Long, String> nameMap;
    private final Set<Long> userIdSet;
    private final Queue<Long> userIdQueue;
    private final String defaultName [] = {"너랑이", "아이유", "해리포터", "타노스", "유재석", "모닝수박", "지수박", "담흥민", "동윤신", "준환킴"};
    private final int playerCnt;
    private int voteCompleteCnt;
    private boolean isGameStarted;
    private static final int SETTING = 0, PLAYING = 1;
    private static final String USER_ID = "userId", NICKNAME = "nickname";
    private int round, status;
    private long playingUserId1, playingUserId2;

    public GameManager(Long roomId){
        roomService = new RoomServiceImpl();
        this.playerCnt = roomService.findUserListByRoomId(roomId).size();
        this.setNameRes = new SetNameRes();
        nameMap = new ConcurrentHashMap<>();
        voteStatus = new HashMap<>();
        userIdSet = new HashSet<>();
        setNameRes = new SetNameRes();
        userIdQueue = new ArrayDeque<>();
        round = 0;
        status = SETTING;
    }

    /**
     * 게임에 참여한 사용자의 userId를 저장하는 메서드
     * @param userId : 사용자의 userId
     * @return 게임을 시작할지 여부
     */
    public boolean addPlayer(long userId) {
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
     * TODO : 테스트용으로 모든 플레이어가 투표 했을 경우 완료되게 해놓음. 찐은 playerCnt - 2
     * 정한 이름을 저장하는 메서드
     * @param req : 투표자 ID, 타겟 ID, 이름, 투표 여부, 종료 여부 가진 객체
     * @return 타겟 ID, 투표 결과 담긴 Map, 집계 상태, 최종 제시어 가진 객체
     */
    public SetNameRes setName(SetNameReq req){
        log.debug("플레이어 수 :"+playerCnt);
        // 투표 현황 관리
        if(!req.isFinished()) {
            if(req.getVote() == 1) voteStatus.put(req.getContent(), voteStatus.get(req.getContent()) + 1);
            else if(req.getVote() == -1) voteStatus.put(req.getContent(), voteStatus.get(req.getContent()) - 1);
            else {
                log.debug("첫 제시어 추가입니다:"+req.getContent());
                log.debug("첫 제시어 추가, voteStatus 사이즈:"+voteStatus.size());
                // 첫 제시어 추가인 경우 voteStatus 초기화 (두 번째 사람 이름 정할 때 걸림)
                if(voteStatus.size() == playerCnt) voteStatus = new HashMap<>();
                voteStatus.put(req.getContent(), 0);
            }
            return SetNameRes.returnResult("", false, voteStatus);
        }
        // 개표 현황 관리
        else {
            // 모든 사람 투표 완료한 경우
            if(++voteCompleteCnt == playerCnt){
                String result = defaultName[(int)(Math.random() * 100) % 10]; // 0~9까지 랜덤 인덱스로 이름 들어감
                int max = -1;
                // 최다 득표 이름 찾음
                for(String content : voteStatus.keySet()){
                    if(voteStatus.get(content) > max){
                        result = content;
                        max = voteStatus.get(content);
                    }
                }
                nameMap.put(req.getTargetId(), setNameRes.getResult());
                voteCompleteCnt = 0;
                return SetNameRes.returnResult(result, true, voteStatus);
            }
            // 아직 모든 사람의 투표가 완료되지 않은 경우
            return SetNameRes.returnResult("", false, voteStatus);
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
     * 현재 게임 라운드, 상태, 이름 정할 userId, 빈 문자열 반환
     * @return GameStatusRes
     */
    public GameStatusRes getNextUsers() {
        Map<String, Object> user1 = new HashMap<>();
        Map<String, Object> user2 = new HashMap<>();

        playingUserId1 = userIdQueue.poll();
        user1.put(USER_ID, playingUserId1);
        user1.put(NICKNAME, "");

        playingUserId2 = userIdQueue.poll();
        user2.put(USER_ID, playingUserId2);
        user2.put(NICKNAME, "");

        return GameStatusRes.of(++round, status, user1, user2);
    }

    /**
     * 현재 게임 라운드, 상태, userId, 정한 이름 반환
     * @return GameStatusRes
     */
    public GameStatusRes getGameStatus() {
        Map<String, Object> user1 = new HashMap<>();
        Map<String, Object> user2 = new HashMap<>();

        user1.put(USER_ID, playingUserId1);
        user1.put(NICKNAME, nameMap.get(playingUserId1));

        user2.put(USER_ID, playingUserId2);
        user2.put(NICKNAME, nameMap.get(playingUserId2));

        return GameStatusRes.of(++round, status, user1, user2);
    }
}