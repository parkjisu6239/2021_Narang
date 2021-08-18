package com.exp.narang.websocket.mafia.response;

import com.exp.narang.websocket.mafia.model.Player;
import lombok.Getter;
import lombok.Setter;
import retrofit2.http.HEAD;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class GameResult {
    private static final String MAFIA_WIN_MESSAGE = "마피아가 승리하였습니다.";
    private static final String CITIZEN_WIN_MESSAGE = "시민이 승리하였습니다.";

    private String msg;
    private boolean isFinished;
    private boolean completeVote;
    private String roleString;
    private Map<String, Integer> voteStatus;
    private int missionNumber = -1;

    public GameResult() { }

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

    public GameResult(String killedUser, boolean completeVote, Map<Player, Integer> countStatus) {
        this(killedUser, completeVote);
        this.voteStatus = new HashMap<>();
        if(countStatus != null) {
            for (Map.Entry<Player, Integer> entry : countStatus.entrySet()) {
                String username = entry.getKey().getUser().getUsername();
                Integer count = entry.getValue();
                this.voteStatus.put(username, count);
            }
        }
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public static GameResult votingStatus() {
        return new GameResult("투표가 진행 중입니다", false);
    }

    public static GameResult returnMafiaWin() { return new GameResult(GameResultType.MAFIA_WIN); }

    public static GameResult returnCitizenWin() {
        return new GameResult(GameResultType.CITIZEN_WIN);
    }

    public static GameResult returnKilledUser(String killedUsername, Map<Player, Integer> countStatus) {
        return new GameResult(killedUsername, true, countStatus);
    }
    public static GameResult returnSelectedUser(String selectedUsername, Map<Player, Integer> countStatus) {
        return new GameResult(selectedUsername, false, countStatus); }

    @Override
    public String toString() {
        return String.format("[isFinished:%s] msg: %s", isFinished, msg);
    }
}
