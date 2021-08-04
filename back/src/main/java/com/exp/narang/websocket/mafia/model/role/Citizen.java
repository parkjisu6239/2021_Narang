package com.exp.narang.websocket.mafia.model.role;

import com.exp.narang.websocket.mafia.model.Player;

public class Citizen extends Role {

    public Citizen() {
        super(RoleName.Citizen);
    }

    @Override
    public void vote(Player player) {
    }
}
