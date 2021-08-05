package com.exp.narang.websocket.mafia.model.manager;

import com.exp.narang.websocket.mafia.model.Player;
import com.exp.narang.websocket.mafia.model.service.GamePlayers;
import com.exp.narang.websocket.mafia.model.service.GameResult;
import com.exp.narang.websocket.mafia.model.service.GameResultType;

import com.exp.narang.websocket.mafia.request.VoteMessage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
@Getter
@Setter
@Slf4j
public class VoteManager {

    private Map<Player, Player> voteStatus;
    private GamePlayers gamePlayers;

    public VoteManager(GamePlayers gamePlayers) {
        this.gamePlayers = gamePlayers;
        this.voteStatus = new HashMap<>(this.gamePlayers.countOfPlayers());
    }

    public boolean handleVote(VoteMessage voteMessage) {
        log.debug("handleVote: {}", voteMessage);
        Player playerVoting = this.gamePlayers.getPlayer(voteMessage.getUsername());
        Player playerVoted = this.gamePlayers.getPlayer(voteMessage.getTheVoted());

        if (playerVoting == null) {
            return false;
        }

        voteStatus.put(playerVoting, playerVoted);
        //TODO Below code is TEST CODE, DELETE or COMMENT this code before commit.
//        if (this.players.getPlayer("testUser1") != null) {
//            this.voteStatus.put(this.players.getPlayer("testUser1"), this.players.getPlayer("testUser1"));
//        }
//        if (this.players.getPlayer("testUser2") != null) {
//            this.voteStatus.put(this.players.getPlayer("testUser2"), this.players.getPlayer("testUser2"));
//        }
//        if (this.players.getPlayer("testUser3") != null) {
//            this.voteStatus.put(this.players.getPlayer("testUser3"), this.players.getPlayer("testUser3"));
//        }
//        if (this.players.getPlayer("testUser4") != null) {
//            this.voteStatus.put(this.players.getPlayer("testUser4"), this.players.getPlayer("testUser4"));
//        }
//        if (this.players.getPlayer("testUser5") != null) {
//            this.voteStatus.put(this.players.getPlayer("testUser5"), this.players.getPlayer("testUser5"));
//        }
        // test room 에 미리 들어가 있던 세명의 testUser 는 각각 자신을 vote 한다.

        log.debug("VOTESTATUS.SIZE: {}       PLAYERS: {}", voteStatus.size(), this.gamePlayers.countOfPlayers());
        if (!voteMessage.getStage().equals("night") && voteStatus.size() >= this.gamePlayers.countOfPlayers()) {
            log.debug("handleVote::voteStatus: {}", voteStatus.toString());
            return true;
        }

        if (voteMessage.getStage().equals("night") && voteStatus.size() >= this.gamePlayers.countOfMafias()) {
            log.debug("handleVote::voteStatus: {}", voteStatus.toString());
            return true;
        }
        return false;
    }

    public GameResult returnGameResult(VoteMessage voteMessage) {

        String stage = voteMessage.getStage();
        log.debug("returnGameResult:stage: {}", stage);
        String selectedUsername = null;
        if (stage.equals("day1")) {
            selectedUsername = determineResultOfDay(countVoteOfDay());
            log.debug("returnGameResult:Day logic:selectedUserNickName: {}", selectedUsername);
        } else if (stage.equals("day2")) {
            selectedUsername = determineResultOfDay(countVoteOfDay(voteMessage), voteMessage);
            log.debug("returnGameResult:Night logic:selectedUserNickName: {}", selectedUsername);
        } else if (stage.equals("night")) {
            selectedUsername = determineResultOfNight(countVoteOfMafia());
            log.debug("returnGameResult:Night logic:selectedUserNickName: {}", selectedUsername);
        }
        GameResultType gameResultType = this.gamePlayers.judgementPlayersCount();
        switch (gameResultType) {
            case MAFIA_WIN:
                log.debug("마피아가 승리하였습니다.");
                return GameResult.returnMafiaWin();
            case CITIZEN_WIN:
                log.debug("시민이 승리하였습니다.");
                return GameResult.returnCitizenWin();
            case KEEP_GOING:
                log.debug("KEEP_GOING::selectedUser: {}", selectedUsername);
                return GameResult.returnSelectedUser(selectedUsername);
            default:
                throw new RuntimeException("Unexpected Error!");
        }
    }

    private Map<Player, Integer> countVoteOfDay() {
        Map<Player, Integer> countStatus = new HashMap<>();
        voteStatus.keySet().forEach(player -> countStatus.put(player, 0));
        voteStatus.values().forEach(player -> {
            if (player != null) { //기권표를 걸러낸다.
                countStatus.put(player, countStatus.get(player) + 1);
            }
        });
        log.debug("countStatus setting: {}", countStatus);
        return countStatus;
    }

    private Map<Boolean, Integer> countVoteOfDay(VoteMessage voteMessage) {
        Map<Boolean, Integer> countStatus = new HashMap<>();
        voteStatus.values().forEach(player -> {
            if(player == null) {
                countStatus.put(false, countStatus.get(false) + 1);
            } else if(player.getUser().getUsername().equals(voteMessage.getUsername())) {
                countStatus.put(true, countStatus.get(true) + 1);
            }

        });
        log.debug("countStatus setting: {}", countStatus);
        return countStatus;
    }

    private Map<Player, Integer> countVoteOfMafia() {
        Map<Player, Integer> countStatusOfMafia = new HashMap<>();
        voteStatus.keySet().stream().forEach(player -> countStatusOfMafia.put(player, 0));
        voteStatus.values().forEach(player -> {
                if (player != null) { //기권표를 걸러낸다.
                    countStatusOfMafia.put(player, countStatusOfMafia.get(player) + 1);
                }
        });
        log.debug("countVoteOfMafia:countStatusOfMafia: {}", countStatusOfMafia);
        return countStatusOfMafia;
    }

    // 투표된 username 반환 // 1차 투표 결과를 수행하는 로직
    private String determineResultOfDay(Map<Player, Integer> countStatus) {
        Player selectedPlayer = null;
        int base = 0;
        for (Map.Entry<Player, Integer> entry : countStatus.entrySet()) {
            if (entry.getValue() > base) {
                selectedPlayer = entry.getKey();
                base = entry.getValue();
            } else if (base == entry.getValue()) {
                selectedPlayer = null;
            }
        }
        log.debug("determineResultOfDay:resultSelectedPlayer: {}", selectedPlayer);
        log.debug("\tday1 로직을 수행합니다.");
        if (selectedPlayer != null) {
            selectedPlayer.kill();
            this.gamePlayers.removeDeadPlayer(selectedPlayer);
            log.debug("determineResultOfDay:selectPlayer: {}, \n1. {}가 플레이어에서 제외되었습니다.", selectedPlayer, selectedPlayer);
            voteStatus.clear();
            log.debug("투표 현황을 초기화합니다. determineResultOfDay::voteStatus: {}", voteStatus);
            return selectedPlayer.getUser().getUsername();
        }
        log.debug("determineResultOfDay:동률이거나 모두가 투표를 하지 않아 아무도 죽지 않았습니다. \n");
        voteStatus.clear(); //투표가 종료된 뒤 voteStatus 초기화
        log.debug("투표 현황을 초기화합니다. determineResultOfDay::voteStatus: {}", voteStatus);
        return "";
    }
    // 최종 반론 결과를 수행하는 로직
    private String determineResultOfDay(Map<Boolean, Integer> countStatus, VoteMessage voteMessage) {
        if(countStatus.get(false) < countStatus.get(true)) {
            Player player = this.gamePlayers.getPlayer(voteMessage.getUsername());
            player.kill();
            this.gamePlayers.removeDeadPlayer(player);
            log.debug("determineResultOfDay:selectPlayer: {}, \n1. {}가 플레이어에서 제외되었습니다.", player, player);
            voteStatus.clear();
            log.debug("투표 현황을 초기화합니다. determineResultOfDay::voteStatus: {}", voteStatus);
            return player.getUser().getUsername();
        }
        log.debug("determineResultOfDay:동률이거나 모두가 투표를 하지 않아 아무도 죽지 않았습니다. \n");
        voteStatus.clear(); //투표가 종료된 뒤 voteStatus 초기화
        log.debug("투표 현황을 초기화합니다. determineResultOfDay::voteStatus: {}", voteStatus);
        return "";
    }
    // mafia가 죽일 사람을 결정한다.
    private String determineResultOfNight(Map<Player, Integer> countStatusOfMafia) {
        Player mafiaSelectPlayer = null;
        Player doctorSelectPlayer = null;
        int base = 0;
        for (Map.Entry<Player, Integer> entry : countStatusOfMafia.entrySet()) {
            if (entry.getValue() > base) {
                mafiaSelectPlayer = entry.getKey();
                base = entry.getValue();
            }
        }
        log.debug("determineResultOfNight:mafiaSelectPlayer: {}", mafiaSelectPlayer);
        log.debug("\tnight 로직을 수행합니다.");

        // 마피아 투표가 동점일 경우에는 처리안함. 아무나 한명 죽일 것 같음
        if (mafiaSelectPlayer != null) {
            mafiaSelectPlayer.kill();
            this.gamePlayers.removeDeadPlayer(mafiaSelectPlayer); //투표의 결과로 사망시 players에서 제외
            log.debug("determineResultOfNight:mafiaSelectPlayer: {}, \n1. {}가 플레이어에서 제외되었습니다.", mafiaSelectPlayer, mafiaSelectPlayer);
            voteStatus.clear(); //투표가 종료된 뒤 voteStatus 초기화
            log.debug("투표 현황을 초기화합니다. determineResultOfNight::voteStatus: {}", voteStatus);
            return mafiaSelectPlayer.getUser().getUsername();
        }

        log.debug("determineResultOfNight:아무도 죽지 않았습니다. \n4.mafia가 아무도 죽이지 않았고 doctor도 아무도 살리지 않았습니다.", mafiaSelectPlayer);
        voteStatus.clear(); //투표가 종료된 뒤 voteStatus 초기화
        log.debug("투표 현황을 초기화합니다. determineResultOfNight::voteStatus: {}", voteStatus);
        return "";
    }
}
