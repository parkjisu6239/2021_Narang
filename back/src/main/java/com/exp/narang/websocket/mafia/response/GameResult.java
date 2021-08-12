package com.exp.narang.websocket.mafia.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameResult {
    private static final String MAFIA_WIN_MESSAGE = "마피아가 승리하였습니다.";
    private static final String CITIZEN_WIN_MESSAGE = "시민이 승리하였습니다.";

    private String msg;
    private boolean isFinished;
    private boolean completeVote;
    private String roleString;
    private int missionNumber = -1;

    public GameResult() { }

    public boolean isCompleteVote() {
        return completeVote;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getRoleString() {
        return roleString;
    }

    public void setRoleString(String roleString) {
        this.roleString = roleString;
    }

    public GameResult(GameResultType type) {
        this.isFinished = true;
        this.completeVote = true;
        switch (type) {
            case MAFIA_WIN:
                this.msg = MAFIA_WIN_MESSAGE;
                break;
            case CITIZEN_WIN:
                this.msg = CITIZEN_WIN_MESSAGE;
        }
    }

    public GameResult(String killedUser, boolean completeVote) {
        this.isFinished = false;
        this.completeVote = completeVote;
        this.msg = killedUser;
        if(completeVote == true) this.missionNumber = (int)(Math.random() * 100) % 12; // 투표가 완전히 끝날 때만 미션 갱신(0~11)
    }

    public static GameResult votingStatus() {
        return new GameResult("투표가 진행 중입니다", false);
    }

    public static GameResult returnMafiaWin() { return new GameResult(GameResultType.MAFIA_WIN); }

    public static GameResult returnCitizenWin() {
        return new GameResult(GameResultType.CITIZEN_WIN);
    }

    public static GameResult returnKilledUser(String killedUsername) {
        return new GameResult(killedUsername, true);
    }
    public static GameResult returnSelectedUser(String selectedUsername) {
        return new GameResult(selectedUsername, false);
    }

    @Override
    public String toString() {
        return String.format("[isFinished:%s] msg: %s", isFinished, msg);
    }
}
