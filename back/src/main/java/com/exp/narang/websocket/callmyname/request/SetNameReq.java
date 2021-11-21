package com.exp.narang.websocket.callmyname.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SetNameReq {
    private long userId; // 투표자의 userId (send 요청을 보낸 장본인)
    private long targetId; // 타겟의 userId
    private String content; // 제시어
    private int vote; // 투표 추가 시 1, 투표 철회 시 -1, 제시어 입력 시 0
    private boolean isFinished; // 제한시간 끝나면 true, 아니면 false
}