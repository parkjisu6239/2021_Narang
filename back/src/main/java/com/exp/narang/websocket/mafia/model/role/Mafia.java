package com.exp.narang.websocket.mafia.model.role;

import com.exp.narang.websocket.mafia.model.Player;
public class Mafia extends Role {

    public Mafia() {
        super(RoleName.Mafia);
    }

    @Override
    public void vote(Player player) {
        player.kill();
    }
}
