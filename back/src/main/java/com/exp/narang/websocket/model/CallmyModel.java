package com.exp.narang.websocket.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

// Data 어노테이션은 getter, setter를 자동 생성합니다.
@Data
@ToString
// AllArgsConstructor 어노테이션은 생성자를 자동 생성합니다.
@AllArgsConstructor
public class CallmyModel {
    private Long roomId;

    // 'user_id username' 맨 앞 질문 후 뒤로
    private String order;

    // 먼저 맞힌 사람부터 | 단위로 추가
    private String removed;
}
