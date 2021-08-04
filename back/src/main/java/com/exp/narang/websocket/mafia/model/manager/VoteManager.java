package com.exp.narang.websocket.mafia.model.manager;

import com.exp.narang.websocket.mafia.model.service.GamePlayers;
import com.exp.narang.websocket.mafia.model.service.GameResult;
import com.exp.narang.websocket.mafia.model.service.GameResultType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class VoteManager {
//    private static final Logger log = LoggerFactory.getLogger(VoteManager.class);
//
//    private Map<Player, Player> voteStatus;
//    private GamePlayers players;
//
//    public VoteManager(GamePlayers players) {
//        this.players = players;
//        this.voteStatus = new HashMap<>(this.players.countOfPlayers());
//    }
//
//    public boolean handleVote(VoteMessage voteMessage) {
//        log.debug("handleVote: {}", voteMessage);
//        Player playerVoting = this.players.getPlayer(voteMessage.getUserName());
//        Player playerVoted = this.players.getPlayer(voteMessage.getTheVoted());
//
//        if (playerVoting == null) {
//            return false;
//        }
//
//        voteStatus.put(playerVoting, playerVoted);
//        //TODO Below code is TEST CODE, DELETE or COMMENT this code before commit.
////        if (this.players.getPlayer("testUser1") != null) {
////            this.voteStatus.put(this.players.getPlayer("testUser1"), this.players.getPlayer("testUser1"));
////        }
////        if (this.players.getPlayer("testUser2") != null) {
////            this.voteStatus.put(this.players.getPlayer("testUser2"), this.players.getPlayer("testUser2"));
////        }
////        if (this.players.getPlayer("testUser3") != null) {
////            this.voteStatus.put(this.players.getPlayer("testUser3"), this.players.getPlayer("testUser3"));
////        }
////        if (this.players.getPlayer("testUser4") != null) {
////            this.voteStatus.put(this.players.getPlayer("testUser4"), this.players.getPlayer("testUser4"));
////        }
////        if (this.players.getPlayer("testUser5") != null) {
////            this.voteStatus.put(this.players.getPlayer("testUser5"), this.players.getPlayer("testUser5"));
////        }
//        // test room 에 미리 들어가 있던 세명의 testUser 는 각각 자신을 vote 한다.
//
//        log.debug("VOTESTATUS.SIZE: {}       PLAYERS: {}", voteStatus.size(), this.players.countOfPlayers());
//        if (voteStatus.size() >= this.players.countOfPlayers()) {
//            log.debug("handleVote::voteStatus: {}", voteStatus.toString());
//            return true;
//        }
//        return false;
//    }
//
//    public GameResult returnGameResult(String stage) {
//        log.debug("returnGameResult:stage: {}", stage);
//        String selectedUserNickName;
//        if (stage.equals("day")) {
//            selectedUserNickName = determineResultOfDay(countVoteOfDay());
//            log.debug("returnGameResult:Day logic:selectedUserNickName: {}", selectedUserNickName);
//        } else {
//            selectedUserNickName = determineResultOfNight(countVoteOfMafia(), countVoteOfDoctor());
//            log.debug("returnGameResult:Night logic:selectedUserNickName: {}", selectedUserNickName);
//        }
//        GameResultType gameResultType = this.players.judgementPlayersCount();
//        switch (gameResultType) {
//            case MAFIA_WIN:
//                log.debug("마피아가 승리하였습니다.");
//                return GameResult.returnMafiaWin();
//            case CITIZEN_WIN:
//                log.debug("시민이 승리하였습니다.");
//                return GameResult.returnCitizenWin();
//            case KEEP_GOING:
//                log.debug("KEEP_GOING::selectedUser: {}", selectedUserNickName);
//                return GameResult.returnSelectedUser(selectedUserNickName);
//            default:
//                throw new RuntimeException("Unexpected Error!");
//        }
//    }
//
//    private Map<Player, Integer> countVoteOfDay() {
//        Map<Player, Integer> countStatus = new HashMap<>();
//        voteStatus.keySet().forEach(player -> countStatus.put(player, 0));
//        voteStatus.values().forEach(player -> {
//            if (player != null) { //기권표를 걸러낸다.
//                countStatus.put(player, countStatus.get(player) + 1);
//            }
//        });
//        log.debug("countStatus setting: {}", countStatus);
//        return countStatus;
//    }
//
//    private Map<Player, Integer> countVoteOfMafia() {
//        Map<Player, Integer> countStatusOfMafia = new HashMap<>();
//        voteStatus.keySet().stream()
//            .forEach(player -> countStatusOfMafia.put(player, 0));
//        voteStatus.keySet().stream()
//            .filter(player -> player.isMafia())
//            .forEach(player -> {
//                Player selectedPlayerByMafia = voteStatus.get(player);
//                if (selectedPlayerByMafia != null) { //기권표를 걸러낸다.
//                    countStatusOfMafia.put(selectedPlayerByMafia, countStatusOfMafia.get(selectedPlayerByMafia) + 1);
//                }
//            });
//        log.debug("countVoteOfMafia:countStatusOfMafia: {}", countStatusOfMafia);
//        return countStatusOfMafia;
//    }
//
//    private Map<Player, Integer> countVoteOfDoctor() {
//        Map<Player, Integer> countStatusOfDoctor = new HashMap<>();
//        voteStatus.keySet().stream()
//            .forEach(player -> countStatusOfDoctor.put(player, 0));
//        voteStatus.keySet().stream()
//            .filter(player -> player.isDoctor())
//            .forEach(player -> {
//                Player selectedPlayerByDoctor = voteStatus.get(player);
//                if (selectedPlayerByDoctor != null) { //기권표를 걸러낸다.
//                    countStatusOfDoctor.put(selectedPlayerByDoctor, countStatusOfDoctor.get(selectedPlayerByDoctor) + 1);
//                }
//            });
//        log.debug("countVoteOfDoctor:countStatusOfDoctor: {}", countStatusOfDoctor);
//        return countStatusOfDoctor;
//    }
//
//    private String determineResultOfDay(Map<Player, Integer> countStatus) {
//        Player selectedPlayer = null;
//        int base = 0;
//        for (Map.Entry<Player, Integer> entry : countStatus.entrySet()) {
//            if (entry.getValue() > base) {
//                selectedPlayer = entry.getKey();
//                base = entry.getValue();
//            } else if (base == entry.getValue()) {
//                selectedPlayer = null;
//            }
//        }
//        log.debug("determineResultOfDay:resultSelectedPlayer: {}", selectedPlayer);
//        log.debug("\tday 로직을 수행합니다.");
//        if (selectedPlayer != null) {
//            selectedPlayer.kill();
//            this.players.removeDeadPlayer(selectedPlayer);
//            log.debug("determineResultOfDay:selectPlayer: {}, \n1. {}가 플레이어에서 제외되었습니다.", selectedPlayer, selectedPlayer);
//            voteStatus.clear();
//            log.debug("투표 현황을 초기화합니다. determineResultOfDay::voteStatus: {}", voteStatus);
//            return selectedPlayer.getUser().getNickname();
//        }
//        log.debug("determineResultOfDay:동률이거나 모두가 투표를 하지 않아 아무도 죽지 않았습니다. \n");
//        voteStatus.clear(); //투표가 종료된 뒤 voteStatus 초기화
//        log.debug("투표 현황을 초기화합니다. determineResultOfDay::voteStatus: {}", voteStatus);
//        return "";
//    }
//
//    private String determineResultOfNight(Map<Player, Integer> countStatusOfMafia,
//                                          Map<Player, Integer> countStatusOfDoctor) {
//        Player mafiaSelectPlayer = null;
//        Player doctorSelectPlayer = null;
//        int base = 0;
//        for (Map.Entry<Player, Integer> entry : countStatusOfMafia.entrySet()) {
//            if (entry.getValue() > base) {
//                mafiaSelectPlayer = entry.getKey();
//                base = entry.getValue();
//            }
//        }
//        base = 0;
//        for (Map.Entry<Player, Integer> entry : countStatusOfDoctor.entrySet()) {
//            if (entry.getValue() > base) {
//                doctorSelectPlayer = entry.getKey();
//                base = entry.getValue();
//            }
//        }
//
//        log.debug("determineResultOfNight:mafiaSelectPlayer: {}", mafiaSelectPlayer);
//        log.debug("determineResultOfNight:doctorSelectPlayer: {}", doctorSelectPlayer);
//        log.debug("\tnight 로직을 수행합니다.");
//
//        if (mafiaSelectPlayer != null && doctorSelectPlayer != null) {
//            mafiaSelectPlayer.kill();
//            doctorSelectPlayer.safe();
//
//            if (!mafiaSelectPlayer.isAlive()) {
//                this.players.removeDeadPlayer(mafiaSelectPlayer); //투표의 결과로 사망시 players에서 제외
//                log.debug("determineResultOfNight:mafiaSelectPlayer: {}, \n1. {}가 플레이어에서 제외되었습니다.", mafiaSelectPlayer, mafiaSelectPlayer);
//                voteStatus.clear(); //투표가 종료된 뒤 voteStatus 초기화
//                log.debug("투표 현황을 초기화합니다. determineResultOfNight::voteStatus: {}", voteStatus);
//                return mafiaSelectPlayer.getUser().getNickname();
//            }
//            log.debug("determineResultOfNight:아무도 죽지 않았습니다. \n2. mafia가 죽였지만 doctor가 다시 살렸습니다.");
//            voteStatus.clear(); //투표가 종료된 뒤 voteStatus 초기화
//            log.debug("투표 현황을 초기화합니다. determineResultOfNight::voteStatus: {}", voteStatus);
//            return "";
//
//        } else if (mafiaSelectPlayer != null) {//doctorSelectPlayer == null
//            mafiaSelectPlayer.kill();
//            this.players.removeDeadPlayer(mafiaSelectPlayer);
//            log.debug("determineResultOfNight:mafiaSelectPlayer: {}, {}가 플레이어에서 제외되었습니다.\n3. doctor는 아무도 선택하지 않았고 mafia가 선택한 사람이 죽었습니다.", mafiaSelectPlayer, mafiaSelectPlayer);
//            voteStatus.clear(); //투표가 종료된 뒤 voteStatus 초기화
//            log.debug("투표 현황을 초기화합니다. determineResultOfNight::voteStatus: {}", voteStatus);
//            return mafiaSelectPlayer.getUser().getNickname();
//
//        } else if (doctorSelectPlayer != null) {//mafiaSelectPlayer == null
//            doctorSelectPlayer.safe();
//            log.debug("determineResultOfNight:아무도 죽지 않았습니다. \n4.mafia가 아무도 죽이지 않았고 doctor가 {}를 다시 살렸습니다.", doctorSelectPlayer);
//            voteStatus.clear(); //투표가 종료된 뒤 voteStatus 초기화
//            log.debug("투표 현황을 초기화합니다. determineResultOfNight::voteStatus: {}", voteStatus);
//            return "";
//        }
//
//        log.debug("determineResultOfNight:아무도 죽지 않았습니다. \n4.mafia가 아무도 죽이지 않았고 doctor도 아무도 살리지 않았습니다.", doctorSelectPlayer);
//        voteStatus.clear(); //투표가 종료된 뒤 voteStatus 초기화
//        log.debug("투표 현황을 초기화합니다. determineResultOfNight::voteStatus: {}", voteStatus);
//        return "";
//    }
}
