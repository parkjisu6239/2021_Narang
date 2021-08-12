package com.exp.narang.websocket.mafia.model.manager;

import com.exp.narang.websocket.mafia.model.role.Role;
import com.exp.narang.websocket.mafia.model.role.AssignRole;
import com.exp.narang.websocket.mafia.response.GamePlayers;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 참가자의 역할을 분배한다.
public class RoleManager {
    private static Map<Integer, AssignRole> mappings = new HashMap<>();

    static {
        // 참여자 수에 따라 마피아, 시민의 수가 달라진다.
        mappings.put(2, new AssignRole(2, 1,1));
        mappings.put(3, new AssignRole(3, 1,2));
        mappings.put(4, new AssignRole(4, 1,3));
        mappings.put(5, new AssignRole(5,1,4));
        mappings.put(6, new AssignRole(6,2, 4));
        mappings.put(7, new AssignRole(7,2,5));
        mappings.put(8, new AssignRole(8,2,6));
        mappings.put(9, new AssignRole(9,3,6));
    }

    // 마피아, 시민 수에 맞게 역할 분담을 한다.
    public static List<Role> assignRoleToPlayers(GamePlayers players) {
        AssignRole ar = mappings.get(players.countOfPlayers());
        List<Role> roles = ar.makeRole();

        // 섞어
        Collections.shuffle(roles);
        players.setRole(roles);
        return roles;
    }
}
