package com.exp.narang.websocket.mafia.request;

public class VoteMessage {
    private String userName;
    private String theVoted;
    private String stage;

    public VoteMessage() {
    }

    public VoteMessage(String userName, String theVoted) {
        this.userName = userName;
        this.theVoted = theVoted;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTheVoted() {
        return theVoted;
    }

    public void setTheVoted(String theVoted) {
        this.theVoted = theVoted;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    @Override
    public String toString() {
        return "VoteMessage{" +
            "userName='" + userName + '\'' +
            ", theVoted='" + theVoted + '\'' +
            ", stage='" + stage + '\'' +
            '}';
    }
}
