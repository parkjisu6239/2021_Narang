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
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

@Getter
@Setter
@Slf4j

// 게임에 필요한 로직을 관리한다.
public class GameManager {
//    private static final Logger log = LoggerFactory.getLogger(GameManager.class);

    private RoomService roomService;
    private GamePlayers gamePlayers;
    private VoteManager voteManager;

    private Set<String> usernameSet;
    private Long roomId;
    private String roleString;
    private boolean isGameStarted;

    public GameManager () {}

    public GameManager (Long roomId, RoomService roomService) {
        this.roomId = roomId;
        this.roomService = roomService;
        List<User> users = roomService.findUserListByRoomId(roomId);
        this.gamePlayers = new GamePlayers(users);
        RoleManager.assignRoleToPlayers(this.gamePlayers);
        this.roleString = gamePlayers.getRoleString();
        this.voteManager = new VoteManager(gamePlayers);
        this.usernameSet = new HashSet<>();
    }

    /**
     * 게임에 참여한 사용자의 수를 카운트 하는 메서드
     * @return 게임을 시작할지 여부
     */
    public boolean addPlayer(String username) {
        log.debug("mafia addPlayer 실행 ~~");
        usernameSet.add(username);
        log.debug(usernameSet.size() + "요거 유저 셋 사이즈");
        log.debug(this.gamePlayers.getPlayers().size() + "요거 게임플레이어 셋 사이즈");
        boolean allConnected = usernameSet.size() == this.gamePlayers.getPlayers().size();
        // 전부 연결 되었을 때
        if(allConnected) {
            System.out.println("모두 연결됨");
            // 이미 게임이 시작되었으면 null 반환
            if (isGameStarted){
                log.debug("게임 이미 시작됨");

                return false;
            }
            // 게임이 시작되지 않았으면 게임 시작 표시
            log.debug("게임 이미 시작됨");
            isGameStarted = true;
            return true;
        }
        return false;
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
