package com.exp.narang.websocket.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

// Data 어노테이션은 getter, setter를 자동 생성합니다.
@Data
@ToString
// AllArgsConstructor 어노테이션은 생성자를 자동 생성합니다.
@AllArgsConstructor
public class ChatModel {
    private Long roomId;
    
    // 유저의 이름을 저장하기 위한 변수
    private String userName;

    // 메시지의 내용을 저장하기 위한 변수
    private String content;

    // 프로필 사진 저장하는 변수
    private String profileImageURL;

    // 방장의 게임 시작 여부
    private Boolean isGameStart;

    // 게임 종류
    private String gameSelected;
}
