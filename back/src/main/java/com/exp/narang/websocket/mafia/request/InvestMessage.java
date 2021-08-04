package com.exp.narang.websocket.mafia.request;

public class InvestMessage {
    String theVoted;
    
    public InvestMessage(){};
    
    public InvestMessage(String theVoted) {
        this.theVoted = theVoted;
    }

    public String getTheVoted() {
        return theVoted;
    }
    public void setTheVoted(String theVoted) {
        this.theVoted = theVoted;
    }
}
