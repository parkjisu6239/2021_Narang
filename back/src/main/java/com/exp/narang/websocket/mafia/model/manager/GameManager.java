package com.exp.narang.websocket.mafia.model.manager;

import com.exp.narang.api.model.service.RoomService;
import com.exp.narang.api.model.db.entity.User;
import com.exp.narang.websocket.mafia.model.Player;
import com.exp.narang.websocket.mafia.request.MafiaMessage;
import com.exp.narang.websocket.mafia.response.GamePlayers;

import com.exp.narang.websocket.mafia.response.GameResult;
import com.exp.narang.websocket.mafia.request.VoteMessage;
import com.exp.narang.websocket.mafia.response.RoleResult;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Getter
@Setter
// 게임에 필요한 로직을 관리한다.
public class GameManager {
    private static final Logger log = LoggerFactory.getLogger(GameManager.class);

    private RoomService roomService;

    private Long roomId;
    private GamePlayers gamePlayers;
    private VoteManager voteManager;
    private String roleString;

    public GameManager () {}

    public GameManager (Long roomId, RoomService roomService) {
        this.roomId = roomId;
        this.roomService = roomService;
        List<User> users = roomService.findUserListByRoomId(roomId);
        this.gamePlayers = new GamePlayers(users);
        RoleManager.assignRoleToPlayers(this.gamePlayers);
        this.roleString = gamePlayers.getRoleString();
        this.voteManager = new VoteManager(gamePlayers);
    }

    public RoleResult findRoleNameByUsername(String username) {
        return this.gamePlayers.findRoleName(username);
    }

    public GameResult returnVoteResult(VoteMessage voteMessage) {
        log.debug("returnVoteResult: {}", voteMessage);
        if (!voteManager.handleVote(voteMessage)) {
            return GameResult.votingStatus();
        }
        GameResult gr = voteManager.returnGameResult(voteMessage);
        if (gr.isFinished()) {
            gr.setRoleString(roleString);
        }
        return gr;
    }

    public int isEveryMafiaComplete(MafiaMessage mafiaMessage){
        return gamePlayers.everyMafiaMissionComplete(mafiaMessage);
    }
}
