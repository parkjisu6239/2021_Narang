package com.exp.narang.websocket.mafia.request;

/**
 * Created by Jbee on 2017. 3. 28..
 */
public class ClientMessage {
    private String userName;
    private String content;

    public ClientMessage() {
    }

    public ClientMessage(String userName, String content) {
        this.userName = userName;
        this.content = content;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return String.format("userName:%s, content:%s", userName, content);
    }
}
