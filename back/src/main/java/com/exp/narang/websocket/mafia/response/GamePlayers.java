package com.exp.narang.websocket.mafia.response;


import com.exp.narang.api.model.db.entity.User;
import com.exp.narang.websocket.mafia.model.Player;
import com.exp.narang.websocket.mafia.model.role.Role;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
@Getter
@Setter
// 게임 참가자들과 관련된 로직을 처리한다.
public class GamePlayers {
    private static final Logger log = LoggerFactory.getLogger(GamePlayers.class);

    private List<Player> players;

    private RoleResult roleResult = new RoleResult();

    // 참가자 리스트 추가하는 생성자
    public GamePlayers(List<User> users) {
        this.players = new ArrayList<>();
        for (User user : users) {
            this.players.add(new Player(user));
        }
    }

    // players의 수를 가져온다.
    public int countOfPlayers() {
        return this.players.size();
    }
    public int countOfMafias() { return (int) this.players.stream().filter(player -> player.isMafia()).count(); }


    // players의 역할을 분배한다.
    public void setRole(List<Role> roles) {
        for (int i = 0; i < countOfPlayers(); i++) {
            this.players.get(i).setRole(roles.get(i));
        }
    }

    // 각 player의 역할과 미션 번호를 리턴한다.
    public RoleResult findRoleName(String username) {
        for (Player player : this.players) {
            if (player.getUser().getUsername().equals(username)) {
                roleResult.setRoleName(player.getRole().getRoleName()); // 역할 저장
                if(player.getRole().isMafia()) roleResult.setMissionNumber(0); // 마피아는 미션 번호 저장(랜덤으로 부여할 예정)
                else roleResult.setMissionNumber(-1); // 시민은 미션 번호에 -1 저장
                return roleResult;
            }
        }
        return null;
    }

    public Player getPlayer(String username) {
        for (Player player : this.players) {
            if (player.getUser().getUsername().equals(username)) {
                return player;
            }
        }
        return null;
    }

    public void removeDeadPlayer(Player player) {
        this.players.remove(player);
    }

    public GameResultType judgementPlayersCount() {
        int mafiaCount = this.countOfMafias();
        int citizenCount = (this.countOfPlayers() - mafiaCount);
        log.debug("judgementPlayersCount:: MafiaCount: {}, CitizenCount: {}", mafiaCount, citizenCount);
        if (mafiaCount == 0) {
            return GameResultType.CITIZEN_WIN;
        } else if (citizenCount <= mafiaCount) {
            return GameResultType.MAFIA_WIN;
        }
        return GameResultType.KEEP_GOING;
    }
    // 게임이 완전히 끝났을 경우 유저 이름과 유저의 역할을 String 형태로 반환해준다
    public String getRoleString() {
        StringBuffer roleString = new StringBuffer();
        for (Player p : players) {
            roleString.append(p.getUser().getUsername());
            roleString.append(":");
            roleString.append(p.getRole().getRoleName());
            roleString.append("&");
        }
        if (roleString.length() > 0) {
            return roleString.substring(0, roleString.length() - 1);
        }
        return roleString.toString();
    }
}
