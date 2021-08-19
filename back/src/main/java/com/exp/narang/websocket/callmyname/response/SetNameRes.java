package com.exp.narang.websocket.callmyname.response;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class SetNameRes {
    private Long targetId; // 타겟의 ID
    private Map<String, Integer> voteStatus; // <제시어, 득표수>
    private Boolean isFinished; // 집계 중인 경우 false, 집계 완료인 경우(모든 사람의 투표를 받은 경우) true
    private String result; // 최종 선택된 제시어

    public SetNameRes(){
        this.voteStatus = new HashMap<>();
        this.isFinished = false;
        this.result = "";
    }

    public SetNameRes(Long targetId, String result, Boolean isFinished, Map<String, Integer> voteStatus){
        this.targetId = targetId;
        this.result = result;
        this.isFinished = isFinished;
        if(!isFinished){
            this.voteStatus = new HashMap<>();
            for(Map.Entry<String, Integer> entry : voteStatus.entrySet()) this.voteStatus.put(entry.getKey(), entry.getValue());
        }
        else this.voteStatus = null;
    }

    public static SetNameRes returnResult(Long targetId, String result, Boolean isFinished, Map<String, Integer> voteStatus){
        return new SetNameRes(targetId, result, isFinished, voteStatus);
    }

}
