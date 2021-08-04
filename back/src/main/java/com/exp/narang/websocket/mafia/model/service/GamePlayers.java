package com.exp.narang.websocket.mafia.model.service;


import com.exp.narang.db.entity.User;
import com.exp.narang.websocket.mafia.model.Player;
import com.exp.narang.websocket.mafia.model.role.Role;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
@Getter
@Setter
public class GamePlayers {
    private static final Logger log = LoggerFactory.getLogger(GamePlayers.class);

    private List<Player> players;

    public GamePlayers(List<User> users) {
        this.players = new ArrayList<>();
        for (User user : users) {
            this.players.add(new Player(user));
        }
    }

    // players의 개수를 가져온다!
    public int countOfPlayers() {
        return this.players.size();
    }

    // players의 역할을 분배한다.
    public void setRole(List<Role> roles) {
        for (int i = 0; i < countOfPlayers(); i++) {
            this.players.get(i).setRole(roles.get(i));
        }
    }

    public String findRoleName(String username) {
        for (Player player : this.players) {
            if (player.getUser().getUsername().equals(username)) {
                return player.getRole().getRoleName();
            }
        }
        return "undefined";
    }
//
//    public List<Player> getPlayers() {
//        return this.players;
//    }
//
//    public Player getPlayer(String userName) {
//        for (Player player : this.players) {
//            if (player.isSameNickName(userName)) {
//                return player;
//            }
//        }
//        return null;
//    }
//
//    public void removeDeadPlayer(Player player) {
//        this.players.remove(player);
//    }
//
//    public GameResultType judgementPlayersCount() {
//        int mafiaCount = (int) this.players.stream().filter(player -> player.isMafia()).count();
//        int citizenCount = (this.players.size() - mafiaCount);
//        log.debug("judgementPlayersCount:: MafiaCount: {}, CitizenCount: {}", mafiaCount, citizenCount);
//        if (mafiaCount == 0) {
//            return GameResultType.CITIZEN_WIN;
//        } else if (citizenCount <= mafiaCount) {
//            return GameResultType.MAFIA_WIN;
//        }
//        return GameResultType.KEEP_GOING;
//    }
//
//    public String getRoleString() {
//        String roleString = "";
//        for (Player p : players) {
//            roleString += p.getUser().getNickname() + ":" + p.getRoleName() + "&";
//        }
//        if (roleString.length() > 0) {
//            return roleString.substring(0, roleString.length() - 1);
//        }
//        return roleString;
//    }
}
