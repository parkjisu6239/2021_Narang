package com.exp.narang.websocket.mafia.request;

import lombok.Getter;
import lombok.Setter;
import org.checkerframework.checker.index.qual.SearchIndexBottom;

@Getter
@Setter
public class VoteMessage {
    private String username; // 자기 username (본인)
    private String theVoted; // 누구 선택했는지 (username or null)
    private String stage; // 낮1(누구 죽일지 투표, day1) 낮2(최종 변론 찬반 투표, day2) 밤1 (마피아가 시민 누구 죽일지., night)
    private Boolean isAgree; // 최후 변론시 false : 살린다 , true : 죽인다
    private String secondVoteUsername; // 2차 투표 진행시 해당 유저의 이름을 관리하는 변수이다.

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
