package com.exp.narang.websocket.mafia.model;

import com.exp.narang.api.model.db.entity.User;
import com.exp.narang.websocket.mafia.model.role.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// 참가자의 상태를 관리한다.
public class Player {
    private User user;
    private Role role;
    private int missionNumber; // -1
    private boolean stillAlive; // true

    public Player(User user) {
        this.user = user;
    }

	public boolean isMafia() {
		return this.role.isMafia();
	}

	public void safe() {
        this.stillAlive = true;
    }

    public void kill() {
        this.stillAlive = false;
    }

    public boolean isAlive() {
        return stillAlive;
    }
}
