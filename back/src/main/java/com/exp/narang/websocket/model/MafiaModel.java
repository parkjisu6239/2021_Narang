package com.exp.narang.websocket.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

// Data 어노테이션은 getter, setter를 자동 생성합니다.
@Data
@ToString
// AllArgsConstructor 어노테이션은 생성자를 자동 생성합니다.
@AllArgsConstructor
public class MafiaModel {
    private Long roomId;

    // 'user_id 0' -> 살아 있는 마피아 | 'user_id 1' -> 죽어 있는 마피아
     private String mafias;
//    private Boolean isMafia;

    // 'user_id 0' -> 살아 있는 시민 | 'user_id 1' -> 죽어 있는 시민
     private String citizens;
//    private Boolean isCitizen;

    // 'day' -> 낮 | 'night' -> 밤
    private String turn;

    // 'user_id 득표수'
    private String vote;
}
