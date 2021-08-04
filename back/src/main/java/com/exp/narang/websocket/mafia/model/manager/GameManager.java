package com.exp.narang.websocket.mafia.model.manager;

import com.exp.narang.api.service.RoomService;
import com.exp.narang.db.entity.User;
import com.exp.narang.websocket.mafia.model.service.GamePlayers;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


@Getter
@Setter
public class GameManager {
    private static final Logger log = LoggerFactory.getLogger(GameManager.class);

    @Autowired
    RoomService roomService;

    private Long roomId;
    private GamePlayers gamePlayers;
    private VoteManager voteManager;
    private String roleString;



    public GameManager () {}

    public GameManager (Long roomId) {
        this.roomId = roomId;
        List<User> users = roomService.findUserListByRoomId(roomId);
        this.gamePlayers = new GamePlayers(users);
        RoleManager.assignRoleToPlayers(this.gamePlayers);
    }

//    public GameManager(Set<User> users) {
//        this.players = new GamePlayers(users);
//        RoleManager.assignRoleToPlayers(this.players);
//        this.roleString = players.getRoleString();
//        this.voteManager = new VoteManager(players);
//    }
//
    public String findRoleNameByUsername(String username) {
        return this.gamePlayers.findRoleName(username);
    }
//
//    public GameResult returnVoteResult(VoteMessage voteMessage) {
//        log.debug("returnVoteResult: {}", voteMessage);
//        if (!voteManager.handleVote(voteMessage)) {
//            return GameResult.votingStatus();
//        }
//        GameResult gr = voteManager.returnGameResult(voteMessage.getStage());
//        if (gr.isFinished()) {
//            gr.setRoleString(roleString);
//        }
//        return gr;
//    }
}
