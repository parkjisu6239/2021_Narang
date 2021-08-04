package com.exp.narang.websocket.mafia.model;

import com.exp.narang.db.entity.User;
import com.exp.narang.websocket.mafia.model.role.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Player {
    private User user;
    private Role role;
    private boolean stillAlive = true;

    public Player(User user) {
        this.user = user;
    }




//	public boolean isMafia() {
//		return this.role.isMafia();
//	}
//
//
//    public void safe() {
//        this.stillAlive = true;
//    }
//
    public void kill() {
        this.stillAlive = false;
    }
//
//    public boolean isAlive() {
//        return stillAlive;
//    }
}
