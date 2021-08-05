package com.exp.narang.websocket.mafia.request;

import lombok.Getter;
import lombok.Setter;
import org.checkerframework.checker.index.qual.SearchIndexBottom;

@Getter
@Setter
public class VoteMessage {
    private String username;
    private String theVoted;
    private String stage; // 낮1(누구 죽일지 투표) 낮2(최종 변론 찬반 투표) 밤1 (마피아가 시민 누구 죽일지)
    private Boolean isAgree;


    public VoteMessage() {
    }

    public VoteMessage(String userName, String theVoted) {
        this.username = userName;
        this.theVoted = theVoted;
    }


    @Override
    public String toString() {
        return "VoteMessage{" +
                "username='" + username + '\'' +
                ", theVoted='" + theVoted + '\'' +
                ", stage='" + stage + '\'' +
                ", isAgree=" + isAgree +
                '}';
    }
}
