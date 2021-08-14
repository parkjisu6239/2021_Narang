package com.exp.narang.websocket.callmyname.response;

import com.exp.narang.websocket.callmyname.request.SetNameReq;
import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
@AllArgsConstructor
public class SetNameRes {
    private Map<String, Integer> voteStatus; // <제시어, 득표수>
    private Boolean isFinished; // 집계 중인 경우 false, 집계 완료인 경우(모든 사람의 투표를 받은 경우) true
    private String result; // 최종 선택된 제시어

    public SetNameRes(){ voteStatus = new ConcurrentHashMap<>(); }
    public SetNameRes(String result, Boolean isFinished){
        this.result = result;
        this.isFinished = isFinished;
    }

    /**
     * 투표 상황 관리하는 메서드
     * @param req : 투표자 ID, 타겟 ID, 이름, 투표 여부, 종료 여부 가진 객체
     * @param playerCnt : 플레이어 수
     * @return 타겟 ID, 투표 결과 담긴 Map, 집계 상태, 최종 제시어 가진 객체
     */
    public SetNameRes handleVote(SetNameReq req, int playerCnt){
        if(req.getVote() == 1) this.voteStatus.put(req.getContent(), this.voteStatus.get(req.getContent()) + 1);
        else if(req.getVote() == -1) this.voteStatus.put(req.getContent(), this.voteStatus.get(req.getContent()) - 1);
        else {
            // 첫 제시어 추가인 경우 voteStatus 초기화
            if(this.voteStatus.size() == playerCnt - 2) new SetNameRes();
            this.voteStatus.put(req.getContent(), 0);
        }
        return new SetNameRes("", false);
    }

    /**
     * 개표하는 메서드
     * @param req : 투표자 ID, 타겟 ID, 이름, 투표 여부, 종료 여부 가진 객체
     * @param voteCompleteCnt : 투표 완료된 사람 수
     * @param playerCnt : 플레이어 수
     * @return 타겟 ID, 투표 결과 담긴 Map, 집계 상태, 최종 제시어 가진 객체
     */
    public SetNameRes determineResult(SetNameReq req, int voteCompleteCnt, int playerCnt){
        System.out.println("디털마인 들어옴");
        System.out.println("보트ㅡ카운트:" + voteCompleteCnt);
        System.out.println("플레이어 카운트:" + playerCnt);
        if(voteCompleteCnt == playerCnt - 2){ // 모든 사람 투표 완료
            String result = "";
            int max = -1;
            // 최다 득표 이름 찾음
            for(String content : voteStatus.keySet()){
                if(voteStatus.get(content) > max){
                    result = content;
                    max = voteStatus.get(content);
                }
            }
            return new SetNameRes(result, true);
        }
        return new SetNameRes("", false); // 아직 모든 사람의 투표가 완료되지 않음
    }
}
