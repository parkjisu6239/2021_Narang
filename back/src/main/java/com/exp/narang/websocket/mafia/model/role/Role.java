package com.exp.narang.websocket.mafia.model.role;

import com.exp.narang.websocket.mafia.model.Player;

public abstract class Role {

    private RoleName roleName;
    
    public Role(RoleName roleName) {
    	this.roleName = roleName;
    }

    public abstract void vote(Player player);

    public String getRoleName() {
        return roleName.name();
    }

    @Override
    public String toString() {
        return roleName.name();
    }

	public boolean isMafia() {
		return roleName.equals(RoleName.Mafia);
	}

}
