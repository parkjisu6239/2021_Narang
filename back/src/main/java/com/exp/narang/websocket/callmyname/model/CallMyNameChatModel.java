package com.exp.narang.websocket.callmyname.model;

import com.exp.narang.websocket.chat.model.ChatModel;
import lombok.Setter;

@Setter
public class CallMyNameChatModel extends ChatModel {
    private int vote;
    public CallMyNameChatModel(Long roomId, String userName, String content, String profileImageURL, Boolean gameStart, Boolean roomInfoChange) {
        super(roomId, userName, content, profileImageURL, gameStart, roomInfoChange);
    }
}
